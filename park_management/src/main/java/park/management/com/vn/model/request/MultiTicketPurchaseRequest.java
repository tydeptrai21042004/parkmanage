package park.management.com.vn.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class MultiTicketPurchaseRequest {
  @NotNull private Long walletId;
  @NotNull private Long branchId;
  @NotNull private LocalDate visitDate;
  @NotEmpty private List<LineItem> items;

  // Optional: client-supplied idempotency key (retries won't double charge)
  private String clientRequestId;

  @Data
  public static class LineItem {
    @NotNull private Long ticketTypeId;
    @Min(1) private int quantity;
    // Optional override if you allow variable pricing per type (else compute internally)
    private BigDecimal unitPriceOverride;
  }
}
