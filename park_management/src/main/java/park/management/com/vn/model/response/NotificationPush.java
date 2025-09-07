package park.management.com.vn.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NotificationPush {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime scheduledAt;
}
