package park.management.com.vn.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;

@Builder
@Setter
@Getter
@lombok.Data
public class BranchReviewRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long branchId;

    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer rating;

    @Size(max = 1000)
    private String comment;

    @NotNull
    private Boolean approved;
}
