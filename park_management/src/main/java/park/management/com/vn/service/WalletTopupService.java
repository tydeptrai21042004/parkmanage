package park.management.com.vn.service;

import java.math.BigDecimal;
import park.management.com.vn.service.dto.StartTopupResult;
import vn.payos.type.WebhookData;

public interface WalletTopupService {
  StartTopupResult startTopup(Long walletId, BigDecimal amount, String returnUrl, String cancelUrl);

  /** Legacy entry (keep if some code still calls it) */
  void handleWebhook(WebhookData data);

  /** New explicit entry for verified webhooks (what your controller likely calls) */
  boolean settleTopupFromWebhook(WebhookData data);

  /** Direct mutation used by webhook or polling once we know the paid amount */
  void confirmTopup(Long orderCode, BigDecimal paidAmount, String paymentLinkId);

  /** Server-to-server polling (optional fallback if you need it) */
  void confirmByPolling(Long orderCode);
}
