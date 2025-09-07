package park.management.com.vn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.management.com.vn.entity.AppNotification;
import park.management.com.vn.model.request.CreateNotificationRequest;
import park.management.com.vn.repository.AppNotificationRepository;
import park.management.com.vn.service.AppNotificationService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppNotificationServiceImpl implements AppNotificationService {

    private final AppNotificationRepository repo;

    @Override
    public Long create(CreateNotificationRequest req) {
        AppNotification n = new AppNotification();
        n.setTitle(req.getTitle());
        n.setContent(req.getContent());
        n.setTargetUsername(req.getTargetUsername());
        n.setScheduledAt(req.getScheduledAt() != null ? req.getScheduledAt() : LocalDateTime.now());
        n.setStatus(AppNotification.Status.PENDING);
        return repo.save(n).getId();
    }
}
