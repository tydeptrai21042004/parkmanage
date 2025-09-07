package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;

@Builder
@Setter
@Getter
@lombok.Data
public class RegisterUserRequest {
  @NotNull
  private String email;
  @NotNull
  private String password;
}
