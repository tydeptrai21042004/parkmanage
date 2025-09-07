package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import park.management.com.vn.constaint.NotificationStatus;
import park.management.com.vn.constaint.NotificationType;

import java.time.LocalDateTime;
import lombok.Data;

@Builder
@Setter
@Getter
@lombok.Data
public class NotificationRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String message;

    @NotNull
    private NotificationType notificationType;

    private LocalDateTime sentAt;

    @NotNull
    private NotificationStatus status;
}
