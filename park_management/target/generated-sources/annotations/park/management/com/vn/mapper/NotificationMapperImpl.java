package park.management.com.vn.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import park.management.com.vn.entity.Notification;
import park.management.com.vn.model.request.NotificationRequest;
import park.management.com.vn.model.response.NotificationResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-07T15:21:09+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.16 (Amazon.com Inc.)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public Notification toEntity(NotificationRequest request) {
        if ( request == null ) {
            return null;
        }

        Notification notification = new Notification();

        notification.setMessage( request.getMessage() );
        notification.setNotificationType( request.getNotificationType() );
        notification.setSentAt( request.getSentAt() );
        notification.setStatus( request.getStatus() );

        return notification;
    }

    @Override
    public NotificationResponse toResponse(Notification entity) {
        if ( entity == null ) {
            return null;
        }

        NotificationResponse.NotificationResponseBuilder notificationResponse = NotificationResponse.builder();

        notificationResponse.id( entity.getId() );
        notificationResponse.message( entity.getMessage() );
        notificationResponse.notificationType( entity.getNotificationType() );
        notificationResponse.sentAt( entity.getSentAt() );
        notificationResponse.status( entity.getStatus() );
        notificationResponse.createdAt( entity.getCreatedAt() );
        notificationResponse.updatedAt( entity.getUpdatedAt() );

        return notificationResponse.build();
    }
}
