package park.management.com.vn.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import park.management.com.vn.entity.TicketType;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
  List<TicketType> findAllByStatusTrue();
  List<TicketType> findByParkBranch_Id(Long branchId);
  List<TicketType> findByParkBranch_IdAndStatusTrue(Long branchId);
}
