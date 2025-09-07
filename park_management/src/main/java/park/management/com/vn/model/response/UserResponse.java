package park.management.com.vn.model.response;

import java.util.List;
import lombok.Data;

@Data
public class UserResponse {

  private Integer id;
  private String username;
  private String email;
  private List<RoleResponse> roles;
}
