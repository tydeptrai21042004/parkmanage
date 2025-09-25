package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GameRequest(
    @NotNull Long branchId,
    @NotBlank String name,
    String description,
    // mới theo spec có ảnh + status
    String imageUrl,
    Boolean status
) {}
