package park.management.com.vn.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@lombok.Data
public class BranchPromotionResponse {

    private Long id;
    private Long parkBranchId;
    private String description;
    private BigDecimal discount;
    private LocalDateTime from;
    private LocalDateTime to;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
