package park.management.com.vn.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import park.management.com.vn.constant.TopupStatus;
import park.management.com.vn.entity.Wallet;
import park.management.com.vn.entity.WalletTopup;
import park.management.com.vn.repository.WalletRepository;
import park.management.com.vn.repository.WalletTopupRepository;
import park.management.com.vn.service.WalletTopupService;
import park.management.com.vn.service.dto.StartTopupResult;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.WebhookData;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletTopupServiceImpl implements WalletTopupService {

  private final PayOS payOS;
  private final WalletRepository walletRepo;
  private final WalletTopupRepository topupRepo;

  /** Optional S2S confirm endpoint (keep if you still use it elsewhere) */
  @Value("${app.internal-topup-confirm-url:}")
  private String internalTopupConfirmUrl;

  /** Generate a merchant-wide unique positive orderCode. */
  private long generateOrderCode(Long walletId) {
    long millis = Instant.now().toEpochMilli();              // 13 digits
    long salt   = (walletId != null ? walletId : 0L) % 1000; // up to 3 digits
    return millis * 1000 + salt;                             // ~16 digits
  }

  @Override
  @Transactional
  public StartTopupResult startTopup(Long walletId, BigDecimal amount, String returnUrl, String cancelUrl) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be > 0");
    }

    Wallet wallet = walletRepo.findById(walletId)
        .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));

    log.info("[TOPUP] PENDING record -> walletId={} amount={}", walletId, amount);

    WalletTopup pending = topupRepo.save(
        WalletTopup.builder()
            .wallet(wallet)
            .amount(amount)
            .orderCode(0L)
            .status(TopupStatus.PENDING)
            .build()
    );

    long orderCode = generateOrderCode(wallet.getId());
    pending.setOrderCode(orderCode);
    topupRepo.save(pending);
    log.info("[TOPUP] orderCode generated: {}", orderCode);

    ItemData item = ItemData.builder()
        .name("Wallet top-up for user " + (wallet.getUserEntity() != null ? wallet.getUserEntity().getId() : "unknown"))
        .quantity(1)
        .price(amount.intValueExact()) // integer VND per PayOS
        .build();

    PaymentData payment = PaymentData.builder()
        .orderCode(orderCode)
        .amount(amount.intValueExact())
        .description("Top-up wallet #" + wallet.getId())
        .returnUrl(returnUrl)
        .cancelUrl(cancelUrl)
        .item(item)
        .build();

    try {
      CheckoutResponseData link = payOS.createPaymentLink(payment);
      pending.setPaymentLinkId(link.getPaymentLinkId());
      topupRepo.save(pending);

      log.info("[TOPUP] PayOS link created: orderCode={} paymentLinkId={} checkoutUrl={}",
          orderCode, link.getPaymentLinkId(), link.getCheckoutUrl());

      return new StartTopupResult(orderCode, link.getPaymentLinkId(), link.getCheckoutUrl());
    } catch (Exception e) {
      log.error("[TOPUP] PayOS createPaymentLink failed (walletId={}, amount={}, orderCode={}): {}",
          walletId, amount, orderCode, e.getMessage(), e);
      throw new IllegalStateException("Failed to create PayOS payment link: " + e.getMessage(), e);
    }
  }

  /** Keep legacy entry; delegate to the new settlement method */
  @Override
  @Transactional
  public void handleWebhook(WebhookData data) {
    settleTopupFromWebhook(data);
  }

  /** Called by webhook controller AFTER SDK verification */
  @Override
  @Transactional
  public boolean settleTopupFromWebhook(WebhookData d) {
    if (d == null) return false;

    WalletTopup topup = topupRepo.findByOrderCode(d.getOrderCode())
        .orElseThrow(() -> new IllegalStateException("Topup not found for orderCode: " + d.getOrderCode()));

    if (topup.getStatus() == TopupStatus.SUCCEEDED) {
      log.info("[SETTLE] already succeeded, orderCode={}", d.getOrderCode());
      return false; // idempotent no-op
    }
    if (topup.getStatus() != TopupStatus.PENDING) {
      log.warn("[SETTLE] status not PENDING ({}), orderCode={}", topup.getStatus(), d.getOrderCode());
      return false;
    }

    // amount check (PayOS amounts are integer VND)
    BigDecimal paid = BigDecimal.valueOf(d.getAmount());
    if (paid.compareTo(topup.getAmount()) != 0) {
      topup.setStatus(TopupStatus.FAILED);
      topupRepo.save(topup);
      log.error("[SETTLE] Amount mismatch: expected {} got {} (orderCode={})",
          topup.getAmount(), paid, d.getOrderCode());
      return false;
    }

    // credit wallet
    Wallet wallet = topup.getWallet();
    BigDecimal current = wallet.getBalance() == null ? BigDecimal.ZERO : wallet.getBalance();
    wallet.setBalance(current.add(paid));
    walletRepo.save(wallet);

    // mark success & keep link/reference if present
    topup.setStatus(TopupStatus.SUCCEEDED);
    if (d.getPaymentLinkId() != null && (topup.getPaymentLinkId() == null || topup.getPaymentLinkId().isBlank())) {
      topup.setPaymentLinkId(d.getPaymentLinkId());
    }
    topupRepo.save(topup);

    log.info("[SETTLE] SUCCESS walletId={} +{} newBalance={} orderCode={}",
        wallet.getId(), paid, wallet.getBalance(), d.getOrderCode());
    return true;
  }

  /** Required by interface – usable from polling or other flows */
  @Override
  @Transactional
  public void confirmTopup(Long orderCode, BigDecimal paidAmount, String paymentLinkId) {
    log.info("[CONFIRM] confirmTopup orderCode={} paidAmount={} paymentLinkId={}",
        orderCode, paidAmount, paymentLinkId);

    WalletTopup topup = topupRepo.findByOrderCode(orderCode)
        .orElseThrow(() -> new IllegalStateException("Topup not found for orderCode: " + orderCode));

    if (topup.getStatus() == TopupStatus.SUCCEEDED) {
      log.info("[CONFIRM] already SUCCESS. orderCode={}", orderCode);
      return; // idempotent
    }
    if (paidAmount == null || paidAmount.compareTo(topup.getAmount()) != 0) {
      topup.setStatus(TopupStatus.FAILED);
      topupRepo.save(topup);
      log.error("[CONFIRM] Amount mismatch: expected {} but got {} (orderCode={})",
          topup.getAmount(), paidAmount, orderCode);
      throw new IllegalStateException("Amount mismatch: expected " + topup.getAmount() + " but got " + paidAmount);
    }

    Wallet wallet = topup.getWallet();
    BigDecimal current = wallet.getBalance() == null ? BigDecimal.ZERO : wallet.getBalance();
    wallet.setBalance(current.add(paidAmount));

    topup.setStatus(TopupStatus.SUCCEEDED);
    if (paymentLinkId != null && (topup.getPaymentLinkId() == null || topup.getPaymentLinkId().isBlank())) {
      topup.setPaymentLinkId(paymentLinkId);
    }

    topupRepo.save(topup);
    walletRepo.save(wallet);

    log.info("[CONFIRM] SUCCESS walletId={} +{} newBalance={} orderCode={}",
        wallet.getId(), paidAmount, wallet.getBalance(), orderCode);
  }

  /** (Optional) S2S polling fallback — do NOT construct WebhookData manually */
  @Override
  @Transactional
  public void confirmByPolling(Long orderCode) {
    log.info("[S2S] confirmByPolling start: orderCode={}", orderCode);
    final Object linkInfo;
    try {
      linkInfo = payOS.getPaymentLinkInformation(orderCode); // PaymentLinkData from SDK
    } catch (Exception e) {
      log.error("[S2S] getPaymentLinkInformation failed | orderCode={} | msg={}", orderCode, e.getMessage(), e);
      throw new IllegalStateException("PayOS getPaymentLinkInformation failed", e);
    }

    try {
      String status = String.valueOf(linkInfo.getClass().getMethod("getStatus").invoke(linkInfo));
      long amountVnd = ((Number) linkInfo.getClass().getMethod("getAmount").invoke(linkInfo)).longValue();
      String paymentLinkId = String.valueOf(linkInfo.getClass().getMethod("getPaymentLinkId").invoke(linkInfo));

      log.info("[S2S] PayOS link status: orderCode={} status={} amount={} paymentLinkId={}",
          orderCode, status, amountVnd, paymentLinkId);

      if (!"PAID".equalsIgnoreCase(status)) {
        log.warn("[S2S] Not PAID yet -> skip credit. orderCode={} status={}", orderCode, status);
        return;
      }
      // Reuse idempotent path
      confirmTopup(orderCode, BigDecimal.valueOf(amountVnd), paymentLinkId);
    } catch (ReflectiveOperationException e) {
      log.error("[S2S] Unable to read PaymentLinkData; adjust getters/type for your SDK.", e);
      throw new IllegalStateException("Adjust getters for PayOS link info type", e);
    }
  }
}
