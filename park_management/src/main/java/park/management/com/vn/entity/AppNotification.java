package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.*;
import park.management.com.vn.entity.base.BaseEntity;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "app_notification")
public class AppNotification extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    // if null => broadcast to everyone
    private String targetUsername;

    // when to send (<= now => send immediately)
    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    private LocalDateTime deliveredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    public enum Status { PENDING, SENT, FAILED }
}
