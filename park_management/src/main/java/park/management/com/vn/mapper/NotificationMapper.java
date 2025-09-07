package park.management.com.vn.mapper;

import org.mapstruct.Mapper;
import park.management.com.vn.entity.Notification;
import park.management.com.vn.model.request.NotificationRequest;
import park.management.com.vn.model.response.NotificationResponse;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    Notification toEntity(NotificationRequest request);

    NotificationResponse toResponse(Notification entity);
}
