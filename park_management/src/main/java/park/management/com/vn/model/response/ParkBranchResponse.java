package park.management.com.vn.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@lombok.Data
public class ParkBranchResponse {

    private Long id;
    private String name;
    private String address;
    private String location;
    private LocalDateTime open;
    private LocalDateTime close;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
