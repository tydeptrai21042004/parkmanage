package park.management.com.vn.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateNotificationRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    // optional. If null => broadcast to all
    private String targetUsername;

    // optional. If null => now
    private LocalDateTime scheduledAt;
}
