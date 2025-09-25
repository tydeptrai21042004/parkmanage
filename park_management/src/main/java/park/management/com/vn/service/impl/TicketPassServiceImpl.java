package park.management.com.vn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import park.management.com.vn.entity.ParkBranch;
import park.management.com.vn.entity.TicketDetail;
import park.management.com.vn.entity.TicketOrder;
import park.management.com.vn.entity.TicketPass;
import park.management.com.vn.model.response.TicketPassLink;
import park.management.com.vn.model.response.TicketPassRedeemResponse;
import park.management.com.vn.model.response.TicketPassStatusResponse;
import park.management.com.vn.repository.TicketPassRepository;
import park.management.com.vn.utils.PassCodeGenerator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketPassServiceImpl implements park.management.com.vn.service.TicketPassService {

  private final TicketPassRepository repo;
  private final PassCodeGenerator codes;

  @PersistenceContext
  private EntityManager em;

  private static final ZoneId HCMC = ZoneId.of("Asia/Ho_Chi_Minh");
  private final String frontendBase = System.getenv().getOrDefault("FRONTEND_BASE_URL", "http://localhost:5173");

  @Override
  @Transactional
  public List<TicketPassLink> createForOrder(TicketOrder order) {
    List<TicketPassLink> out = new ArrayList<>();

    // Resolve branch from order
    ParkBranch branch = null;
    try {
      branch = order.getParkBranch();
    } catch (NoSuchMethodError | RuntimeException ignored) {
      // If your entity uses a different getter name, adjust here.
    }

    // Defensive: ensure we have details even if order.getDetails() is null/empty
    List<TicketDetail> details = order.getDetails();
    if (details == null || details.isEmpty()) {
      details = em.createQuery(
          "SELECT d FROM TicketDetail d WHERE d.ticketOrder.id = :oid",
          TicketDetail.class
      ).setParameter("oid", order.getId()).getResultList();
    }

    for (TicketDetail d : details) {
      int qty = (d.getQuantity() == null) ? 0 : d.getQuantity();
      for (int i = 0; i < qty; i++) {
        TicketPass p = new TicketPass();
        p.setCode(codes.newCode(24));
        p.setOrder(order);
        // Adjust setter name to your entity (detail vs ticketDetail)
        p.setDetail(d);
        if (branch != null) {
          p.setBranch(branch);
        }
        p.setTicketDate(order.getTicketDate());
        repo.save(p);

        out.add(new TicketPassLink(
            p.getId(),
            p.getCode(),
            frontendBase + "/p/" + p.getCode(),
            p.getStatus().name()
        ));
      }
    }
    return out;
  }

  @Override
  @Transactional
  public List<TicketPassLink> createForOrderId(Long orderId) {
    TicketOrder order = em.find(TicketOrder.class, orderId);
    if (order == null) throw new RuntimeException("ORDER_NOT_FOUND");
    return createForOrder(order);
  }

  @Override
  @Transactional(readOnly = true)
  public TicketPassStatusResponse status(String code) {
    TicketPass p = repo.findByCode(code).orElseThrow(() -> new RuntimeException("PASS_NOT_FOUND"));
    var effective = p.getStatus();
    if (effective == TicketPass.Status.ACTIVE && LocalDate.now(HCMC).isAfter(p.getTicketDate())) {
      effective = TicketPass.Status.EXPIRED;
    }
    return new TicketPassStatusResponse(
        p.getId(),
        effective.name(),
        p.getTicketDate(),
        p.getBranch() != null ? p.getBranch().getId() : null
    );
  }

  @Override
  @Transactional
  public TicketPassRedeemResponse redeem(String code, Long staffUserId) {
    var st = status(code);
    if ("EXPIRED".equals(st.status())) throw new RuntimeException("PASS_EXPIRED");
    int updated = repo.redeemIfActive(code, Instant.now(), staffUserId);
    if (updated == 0) throw new RuntimeException("PASS_ALREADY_REDEEMED_OR_INVALID");
    TicketPass p = repo.findByCode(code).orElseThrow();
    return new TicketPassRedeemResponse(p.getId(), p.getStatus().name(), p.getRedeemedAt());
  }
}
