package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {

  @NotNull
  private Long userId;

  @NotEmpty
  private List<Long> roleIds;
}
