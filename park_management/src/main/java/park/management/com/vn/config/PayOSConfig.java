package park.management.com.vn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayOSConfig {

  @Value("${PAYOS_CLIENT_ID}")
  private String clientId;

  @Value("${PAYOS_API_KEY}")
  private String apiKey;

  @Value("${PAYOS_CHECKSUM_KEY}")
  private String checksumKey;

  @Bean
  public PayOS payOS() {
    // Initialize SDK with your channel keys
    return new PayOS(clientId, apiKey, checksumKey);
  }
}
