package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;

@Builder
@Setter
@Getter
@lombok.Data
public class LoginRequest {

  @NotBlank
  private String username;
  @NotBlank
  private String password;
}
