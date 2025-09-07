package park.management.com.vn.service;

import park.management.com.vn.model.request.NotificationRequest;
import park.management.com.vn.model.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse getNotificationById(Long id);

    List<NotificationResponse> getAllNotifications();

    NotificationResponse createNotification(NotificationRequest request);

    NotificationResponse updateNotification(Long id, NotificationRequest request);

    void deleteNotification(Long id);
}
