package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;

@Builder
@Setter
@Getter
@lombok.Data
public class RoleRequest {
    @NotNull
    private String name;
    @NotNull
    private String description;
}
