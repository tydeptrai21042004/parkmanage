package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketTypeRequest {
  @NotNull private Long parkBranchId;   // FK sang branch
  @NotBlank private String name;
  private String description;
  @NotNull private BigDecimal basePrice;
  private Boolean status;               // soft-delete/enable
}
