package park.management.com.vn.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import vn.payos.PayOS;

@Slf4j
@Component
@Profile({"dev","local"})
@RequiredArgsConstructor
public class PayOSDevWebhookRegistrar {

  private final PayOS payOS;

  @Value("${app.webhook.base-url:}")
  private String webhookBaseUrl; // e.g. https://<trycloudflare>.trycloudflare.com

  @Value("${app.webhook.path:/api/payment/payos/webhook}")
  private String webhookPath;

  @EventListener(ApplicationReadyEvent.class)
  public void confirmWebhook() {
    if (webhookBaseUrl == null || webhookBaseUrl.isBlank()) {
      log.warn("[PayOS] app.webhook.base-url not set -> skip confirmWebhook");
      return;
    }
    String url = webhookBaseUrl.replaceAll("/+$","") + webhookPath;
    try {
      String verified = payOS.confirmWebhook(url);
      log.info("[PayOS] confirmWebhook OK -> {} (url={})", verified, url);
    } catch (Exception e) {
      log.error("[PayOS] confirmWebhook FAILED url={}", url, e);
    }
  }
}
