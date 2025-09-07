package park.management.com.vn.model.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import park.management.com.vn.constaint.NotificationStatus;
import park.management.com.vn.constaint.NotificationType;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@lombok.Data
public class NotificationResponse {

    private Long id;
    private Long userId;
    private String message;
    private NotificationType notificationType;
    private LocalDateTime sentAt;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
