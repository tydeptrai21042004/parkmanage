package park.management.com.vn.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@lombok.Data
public class RegisterUserResponse {

  private String username;
  private String email;
}
