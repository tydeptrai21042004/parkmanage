package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ConfirmTopupRequest {
  @NotNull private Long orderCode;
  @NotNull private BigDecimal amount;      // VND
  private String paymentLinkId;            // optional
}
