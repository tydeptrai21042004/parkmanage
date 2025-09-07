package park.management.com.vn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.management.com.vn.entity.Notification;
import park.management.com.vn.mapper.NotificationMapper;
import park.management.com.vn.model.request.NotificationRequest;
import park.management.com.vn.model.response.NotificationResponse;
import park.management.com.vn.repository.NotificationRepository;
import park.management.com.vn.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
import park.management.com.vn.service.NotificationService;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper mapper;
    private final UserRepository usersRepository;

    @Override
    public NotificationResponse createNotification(NotificationRequest request) {
        Notification entity = mapper.toEntity(request);
        entity.setUserEntity(usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        return mapper.toResponse(notificationRepository.save(entity));
    }

    @Override
    public NotificationResponse getNotificationById(Long id) {
        Notification entity = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        return mapper.toResponse(entity);
    }

    @Override
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponse updateNotification(Long id, NotificationRequest request) {
        Notification existing = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        Notification updated = mapper.toEntity(request);
        updated.setId(id);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setCreatedBy(existing.getCreatedBy());
        return mapper.toResponse(notificationRepository.save(updated));
    }

    @Override
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
