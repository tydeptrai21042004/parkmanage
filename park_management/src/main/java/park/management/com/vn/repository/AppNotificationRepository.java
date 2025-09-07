package park.management.com.vn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import park.management.com.vn.entity.AppNotification;

import java.time.LocalDateTime;
import java.util.List;

public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {

    @Query("""
        select n from AppNotification n
        where n.status = 'PENDING' and n.scheduledAt <= :now
        order by n.scheduledAt asc
    """)
    List<AppNotification> findDue(LocalDateTime now);
}
