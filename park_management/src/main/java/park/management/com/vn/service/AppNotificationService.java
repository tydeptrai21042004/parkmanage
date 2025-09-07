package park.management.com.vn.service;

import park.management.com.vn.model.request.CreateNotificationRequest;

public interface AppNotificationService {
    Long create(CreateNotificationRequest req);
}
