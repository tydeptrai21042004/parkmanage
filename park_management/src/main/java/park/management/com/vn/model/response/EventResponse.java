package park.management.com.vn.model.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Long parkBranchId;

    // NEW
    private String imageUrl;
    private Boolean status;

    // match với BaseEntity (LocalDateTime)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
