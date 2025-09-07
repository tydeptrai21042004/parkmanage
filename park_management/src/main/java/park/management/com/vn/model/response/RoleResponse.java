package park.management.com.vn.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@lombok.Data
public class RoleResponse {

  private Long id;
  private String name;
  private String description;
}
