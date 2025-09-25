package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class BranchAmenityRequest {
  @NotNull
  private Long parkBranchId;
  @NotNull
  private Long amenityTypeId;
  @NotNull
  private String name;
  private String description;

  // field mới
  private String imageUrl;
  private Boolean status;
}
