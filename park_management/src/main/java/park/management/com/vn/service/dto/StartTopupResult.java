package park.management.com.vn.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartTopupResult {
  private long orderCode;
  private String paymentLinkId;
  private String checkoutUrl;
}
