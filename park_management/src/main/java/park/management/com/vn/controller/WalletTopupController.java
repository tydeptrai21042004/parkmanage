package park.management.com.vn.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.service.WalletTopupService;
import park.management.com.vn.service.dto.StartTopupResult;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletTopupController {

  private final WalletTopupService topupService;

  @PostMapping("/{walletId}/topups")
  public ResponseEntity<TopupResponse> startTopup(
      @PathVariable Long walletId,
      @Valid @RequestBody TopupRequest req,
      @RequestHeader(value = "X-Return-Url", required = false) String returnUrl,
      @RequestHeader(value = "X-Cancel-Url", required = false) String cancelUrl
  ) {
    log.info("[TOPUP] startTopup walletId={} amount={} returnUrl={} cancelUrl={}",
        walletId, req.getAmount(), returnUrl, cancelUrl);

    StartTopupResult data = topupService.startTopup(walletId, req.getAmount(), returnUrl, cancelUrl);

    log.info("[TOPUP] link created: orderCode={} paymentLinkId={} checkoutUrl={}",
        data.getOrderCode(), data.getPaymentLinkId(), data.getCheckoutUrl());

    return ResponseEntity.ok(new TopupResponse(data.getCheckoutUrl(), data.getPaymentLinkId()));
  }

  @Data
  public static class TopupRequest {
    @NotNull
    @DecimalMin(value = "1000") // BigDecimal-friendly min validation
    private BigDecimal amount;
  }

  public static record TopupResponse(String checkoutUrl, String paymentLinkId) {}
}
