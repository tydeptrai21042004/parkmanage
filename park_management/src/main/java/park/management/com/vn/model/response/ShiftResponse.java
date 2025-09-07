package park.management.com.vn.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@Setter
@Getter
@lombok.Data
public class ShiftResponse {
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
