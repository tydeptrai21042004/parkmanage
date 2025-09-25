package park.management.com.vn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import park.management.com.vn.entity.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
  List<Event> findByParkBranch_Id(Long branchId);
}
