package park.management.com.vn.model.response;

import lombok.Data;

@Data
public class CreatePermissionRequest {

  private Long id;
  private String name;
  private String description;
}
