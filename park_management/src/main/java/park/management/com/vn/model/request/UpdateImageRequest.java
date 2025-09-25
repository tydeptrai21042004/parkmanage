package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UpdateImageRequest {
  @NotBlank
  private String imageUrl;
}
