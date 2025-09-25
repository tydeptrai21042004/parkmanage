package park.management.com.vn.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.management.com.vn.constant.TicketStatus;
import park.management.com.vn.entity.DailyTicketInventory;
import park.management.com.vn.entity.TicketDetail;
import park.management.com.vn.entity.TicketOrder;
import park.management.com.vn.entity.TicketType;
import park.management.com.vn.mapper.TicketMapper;
import park.management.com.vn.model.request.TicketRequest;
import park.management.com.vn.model.response.TicketResponse;
import park.management.com.vn.repository.DailyTicketInventoryRepository;
import park.management.com.vn.repository.TicketDetailRepository;
import park.management.com.vn.repository.TicketOrderRepository;
import park.management.com.vn.service.EmailService;
import park.management.com.vn.service.OrderService;
import park.management.com.vn.service.TicketService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final TicketService ticketService; // reuses all your business rules
  private final TicketOrderRepository orderRepo;
  private final TicketDetailRepository detailRepo;
  private final DailyTicketInventoryRepository inventoryRepo;
  private final TicketMapper ticketMapper;
  private final EmailService emailService;

  @Override
  @Transactional
  public TicketResponse create(TicketRequest request, Long userId) {
    Long orderId = ticketService.createTicketOrder(request, userId);
    return ticketService.getTicketResponseById(orderId);
  }

  @Override
  @Transactional
  public TicketResponse get(Long id) {
    return ticketService.getTicketResponseById(id);
  }

  @Override
  @Transactional
  public List<TicketResponse> listByUser(Long userId) {
    if (userId == null) return List.of();
    List<TicketOrder> orders = orderRepo.findByUserEntity_Id(userId);
    List<TicketResponse> out = new ArrayList<>(orders.size());
    for (TicketOrder o : orders) {
      List<TicketDetail> details = detailRepo.findByTicketOrder_Id(o.getId());
      out.add(ticketMapper.toResponse(o, details));
    }
    return out;
  }

  @Override
  @Transactional
  public TicketResponse updateStatus(Long id, TicketStatus newStatus, String note) {
    TicketOrder order = orderRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("ORDER_NOT_FOUND"));

    TicketStatus current = order.getStatus();
    boolean becamePaid = false;

    switch (newStatus) {
      case PAID -> {
        if (current == TicketStatus.CANCELLED || current == TicketStatus.REFUNDED)
          throw new RuntimeException("ORDER_ALREADY_CLOSED");
        order.setStatus(TicketStatus.PAID);
        becamePaid = true;
      }
      case CANCELLED -> {
        if (current == TicketStatus.PAID)
          throw new RuntimeException("CANNOT_CANCEL_PAID_ORDER");
        if (current == TicketStatus.CANCELLED || current == TicketStatus.REFUNDED)
          throw new RuntimeException("ORDER_ALREADY_CLOSED");
        // revert inventory on cancel
        revertInventoryForOrder(order);
        order.setStatus(TicketStatus.CANCELLED);
      }
      case REFUNDED -> {
        if (current != TicketStatus.PAID)
          throw new RuntimeException("ONLY_PAID_ORDER_CAN_BE_REFUNDED");
        // revert inventory on refund (business choice)
        revertInventoryForOrder(order);
        order.setStatus(TicketStatus.REFUNDED);
      }
      default -> throw new RuntimeException("UNSUPPORTED_STATUS");
    }

    TicketOrder saved = orderRepo.save(order);

    // ---- EMAIL after PAID ----
    if (becamePaid) {
      String recipient = saved.getCustomerEmail();
      if (recipient != null && !recipient.isBlank()) {
        String subject = "[Park] Thanh toán thành công #" + saved.getId();
        String html = """
            <p>Xin chào %s,</p>
            <p>Đơn đặt vé <b>#%d</b> tại chi nhánh <b>%s</b> đã thanh toán thành công.</p>
            <p>Ngày sử dụng: %s</p>
            <p>Số tiền: <b>%s VND</b></p>
            <p>Cảm ơn bạn đã sử dụng dịch vụ!</p>
            """.formatted(
                safe(saved.getCustomerName()),
                saved.getId(),
                safe(saved.getParkBranch() != null ? saved.getParkBranch().getName() : "N/A"),
                safe(saved.getTicketDate()),
                safe(saved.getFinalAmount())
            );
        emailService.sendHtml(recipient, subject, html);
      }
    }

    List<TicketDetail> details = detailRepo.findByTicketOrder_Id(saved.getId());
    return ticketMapper.toResponse(saved, details);
  }

  @Override
  @Transactional
  public void cancelIfUnpaid(Long id) {
    TicketOrder order = orderRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("ORDER_NOT_FOUND"));

    if (order.getStatus() == TicketStatus.PAID)
      throw new RuntimeException("CANNOT_DELETE_PAID_ORDER");

    if (order.getStatus() != TicketStatus.CANCELLED) {
      revertInventoryForOrder(order);
      order.setStatus(TicketStatus.CANCELLED);
      orderRepo.save(order);
    }
  }

  private void revertInventoryForOrder(TicketOrder order) {
    LocalDate date = order.getTicketDate();
    List<TicketDetail> details = detailRepo.findByTicketOrder_Id(order.getId());
    for (TicketDetail d : details) {
      TicketType type = d.getTicketType();
      DailyTicketInventory inv = inventoryRepo
          .getDailyTicketInventoriesByTicketType_IdAndDate(type.getId(), date)
          .orElse(null);
      if (inv != null) {
        int newSold = Math.max(0, inv.getSold() - d.getQuantity());
        inv.setSold(newSold);
        inventoryRepo.save(inv);
      }
    }
  }

  private static String safe(Object o) {
    return o == null ? "" : o.toString();
  }
}
