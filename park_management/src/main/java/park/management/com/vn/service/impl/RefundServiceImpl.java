package park.management.com.vn.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import park.management.com.vn.entity.OrderRefund;
import park.management.com.vn.entity.TicketOrder;
import park.management.com.vn.entity.TransactionRecord;
import park.management.com.vn.entity.UserEntity;
import park.management.com.vn.entity.Wallet;
import park.management.com.vn.repository.OrderRefundRepository;
import park.management.com.vn.repository.TicketPassRepository;
import park.management.com.vn.repository.TicketRepository;
import park.management.com.vn.repository.TransactionRecordRepository;
import park.management.com.vn.repository.WalletRepository;
import park.management.com.vn.service.RefundService;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

  private final TicketRepository ticketRepository;
  private final OrderRefundRepository refundRepository;
  private final TicketPassRepository passRepository;
  private final WalletRepository walletRepository;
  private final TransactionRecordRepository transactionRecordRepository;

  @Override
  @Transactional
  public OrderRefund requestRefund(Long orderId, String reason) {
    // 1) Load order
    TicketOrder order = ticketRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("ORDER_NOT_FOUND"));

    // 2) Rules
    LocalDate ticketDate = order.getTicketDate();
    if (ticketDate == null) throw new IllegalStateException("ORDER_HAS_NO_TICKET_DATE");

    Duration untilUse = Duration.between(LocalDateTime.now(), ticketDate.atStartOfDay());
    if (untilUse.toHours() < 24) {
      throw new IllegalStateException("REFUND_NOT_ALLOWED_LESS_THAN_24H_BEFORE_USE");
    }

    boolean anyRedeemed = passRepository.existsByOrder_IdAndRedeemedAtIsNotNull(orderId);
    if (anyRedeemed) {
      throw new IllegalStateException("REFUND_NOT_ALLOWED_AFTER_CHECKIN");
    }

    boolean already = refundRepository.existsByOrder_Id(orderId);
    if (already) {
      throw new IllegalStateException("REFUND_ALREADY_EXISTS_FOR_ORDER");
    }

    // 3) Amount = finalAmount
    BigDecimal amount = order.getFinalAmount() != null ? order.getFinalAmount() : BigDecimal.ZERO;

    // 4) Create refund row
    OrderRefund refund = new OrderRefund();
    refund.setOrder(order);
    refund.setAmount(amount);
    refund.setReason(reason);
    refund.setStatus(OrderRefund.Status.PENDING);
    refund.setRequestedAt(LocalDateTime.now());
    refund = refundRepository.save(refund);

    // 5) Wallet credit if in-app wallet was used (user != null)
    UserEntity user = order.getUserEntity();
    if (user != null) {
      Wallet wallet = walletRepository.findByUserEntity_IdForUpdate(user.getId())
          .orElseThrow(() -> new IllegalStateException("WALLET_NOT_FOUND_FOR_USER_" + user.getId()));

      wallet.setBalance(wallet.getBalance().add(amount));
      walletRepository.save(wallet);

      TransactionRecord tr = new TransactionRecord();
      tr.setWallet(wallet);
      tr.setAmount(amount.doubleValue()); // credit
      transactionRecordRepository.save(tr);

      refund.setStatus(OrderRefund.Status.COMPLETED);
      refund.setProcessedAt(LocalDateTime.now());
      refund = refundRepository.save(refund);
    } else {
      // External gateway (PayOS) case: leave PENDING, webhook should mark COMPLETED later.
    }

    return refund;
  }

  @Override
  @Transactional
  public OrderRefund getRefund(Long orderId) {
    return refundRepository.findByOrder_Id(orderId)
        .orElseThrow(() -> new IllegalArgumentException("REFUND_NOT_FOUND"));
  }
}
