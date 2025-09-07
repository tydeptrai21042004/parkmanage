package park.management.com.vn.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@lombok.Data
public class BranchStaffResponse {
    private Long id;
    private String role;
    private String description;
    private String userFullName;
    private String parkBranchName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
