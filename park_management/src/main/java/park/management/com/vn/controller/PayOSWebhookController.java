package park.management.com.vn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.constant.TopupStatus;
import park.management.com.vn.entity.Wallet;
import park.management.com.vn.entity.WalletTopup;
import park.management.com.vn.repository.WalletRepository;
import park.management.com.vn.repository.WalletTopupRepository;
import park.management.com.vn.service.WalletTopupService;
import vn.payos.PayOS;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/payment/payos")
@RequiredArgsConstructor
public class PayOSWebhookController {

  private final PayOS payOS;
  private final WalletTopupService topupService;
  private final ObjectMapper mapper;

  /** Production path: verify then settle (2xx regardless to prevent retries storms) */
  @PostMapping("/webhook")
  public ResponseEntity<ObjectNode> handle(@RequestBody ObjectNode body) {
    ObjectNode resp = mapper.createObjectNode();
    try {
      Webhook webhook = mapper.convertValue(body, Webhook.class);
      WebhookData data = payOS.verifyPaymentWebhookData(webhook); // HMAC verify by SDK

      if (data != null && "00".equals(data.getCode())) {
        boolean applied = topupService.settleTopupFromWebhook(data);
        log.info("PayOS webhook OK: orderCode={} amount={} linkId={} applied={}",
            data.getOrderCode(), data.getAmount(), data.getPaymentLinkId(), applied);
      } else {
        log.warn("PayOS webhook ignored (verification or code != 00). data={}", data);
      }
      resp.put("message", "ok");
      return ResponseEntity.ok(resp);
    } catch (Exception e) {
      log.error("PayOS webhook verify/handle failed: {}", e.getMessage(), e);
      resp.put("message", "ignored");
      return ResponseEntity.ok(resp);
    }
  }

  /** DEV helper to force-complete a top-up without PayOS callback */
  @Slf4j
  @RestController
  @RequestMapping("/api/payment/payos")
  @RequiredArgsConstructor
  static class PayOSWebhookDevController {

    private final ObjectMapper mapper;
    private final WalletTopupRepository topupRepo;
    private final WalletRepository walletRepo;

    @PostMapping("/webhook/dev-complete/{orderCode}")
    public ResponseEntity<ObjectNode> devComplete(@PathVariable long orderCode) {
      ObjectNode resp = mapper.createObjectNode();
      try {
        WalletTopup topup = topupRepo.findByOrderCode(orderCode)
            .orElseThrow(() -> new IllegalArgumentException("Topup not found for orderCode: " + orderCode));

        if (topup.getStatus() == TopupStatus.SUCCEEDED) {
          resp.put("message", "already succeeded");
          return ResponseEntity.ok(resp);
        }

        Wallet w = topup.getWallet();
        BigDecimal paid = topup.getAmount() == null ? BigDecimal.ZERO : topup.getAmount();
        BigDecimal current = w.getBalance() == null ? BigDecimal.ZERO : w.getBalance();
        w.setBalance(current.add(paid));

        topup.setStatus(TopupStatus.SUCCEEDED);
        topupRepo.save(topup);
        walletRepo.save(w);

        log.info("[DEV] Forced success. orderCode={}, walletId={}, +{}, newBalance={}",
            orderCode, w.getId(), paid, w.getBalance());

        resp.put("message", "ok");
        resp.put("walletId", w.getId());
        resp.put("newBalance", w.getBalance().toPlainString());
        return ResponseEntity.ok(resp);
      } catch (Exception e) {
        log.error("[DEV] dev-complete failed: {}", e.getMessage(), e);
        resp.put("message", "error");
        resp.put("error", e.getMessage());
        return ResponseEntity.ok(resp);
      }
    }
  }
}
