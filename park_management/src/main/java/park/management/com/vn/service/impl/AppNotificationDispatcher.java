package park.management.com.vn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import park.management.com.vn.entity.AppNotification;
import park.management.com.vn.model.response.NotificationPush;
import park.management.com.vn.repository.AppNotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppNotificationDispatcher {

    private final AppNotificationRepository repo;
    private final SimpMessagingTemplate messaging;

    // Check every 5s for due notifications
    @Scheduled(fixedDelay = 5000)
    public void dispatchDue() {
        List<AppNotification> due = repo.findDue(LocalDateTime.now());
        for (AppNotification n : due) {
            try {
                NotificationPush payload =
                    new NotificationPush(n.getId(), n.getTitle(), n.getContent(), n.getScheduledAt());
                if (n.getTargetUsername() == null || n.getTargetUsername().isBlank()) {
                    // broadcast
                    messaging.convertAndSend("/topic/notifications", payload);
                } else {
                    // per-user
                    messaging.convertAndSendToUser(n.getTargetUsername(), "/queue/notifications", payload);
                }
                n.setDeliveredAt(LocalDateTime.now());
                n.setStatus(AppNotification.Status.SENT);
            } catch (Exception e) {
                log.error("Failed to deliver notification {}: {}", n.getId(), e.getMessage());
                n.setStatus(AppNotification.Status.FAILED);
            }
        }
        if (!due.isEmpty()) {
            repo.saveAll(due);
        }
    }
}
