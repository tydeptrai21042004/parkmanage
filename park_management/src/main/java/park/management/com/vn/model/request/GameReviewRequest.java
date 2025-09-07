package park.management.com.vn.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GameReviewRequest(
    @NotNull Long gameId,
    @Min(1) @Max(5) int rating,
    String comment
) {}
