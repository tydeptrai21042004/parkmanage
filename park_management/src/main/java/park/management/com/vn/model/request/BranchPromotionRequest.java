package park.management.com.vn.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Builder
@Setter
@Getter
@lombok.Data
public class BranchPromotionRequest {
    @NotNull
    private Long parkBranchId;

    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Discount must be greater than 0")
    private BigDecimal discount;

    @NotNull
    private LocalDateTime from;

    @NotNull
    private LocalDateTime to;

    private Boolean isActive;
}
