package park.management.com.vn.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Builder
@Setter
@Getter
@lombok.Data
public class LoginResponse {

  private String accessToken;
}
