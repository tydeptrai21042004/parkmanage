package park.management.com.vn.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import park.management.com.vn.constaint.NotificationStatus;
import park.management.com.vn.constaint.NotificationType;
import park.management.com.vn.entity.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "notification")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity userEntity;

  @Column(name = "message", nullable = false)
  private String message;

  @Enumerated(EnumType.STRING)
  @Column(name = "notification_type")
  private NotificationType notificationType; // 'email' or 'app'

  @Column(name = "sent_at")
  private LocalDateTime sentAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private NotificationStatus status; // 'sent', 'pending', 'failed'
}