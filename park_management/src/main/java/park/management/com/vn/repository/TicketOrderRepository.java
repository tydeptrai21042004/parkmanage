package park.management.com.vn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import park.management.com.vn.entity.TicketOrder;

import java.util.List;

public interface TicketOrderRepository extends JpaRepository<TicketOrder, Long> {

  // Spring Data property path (preferred)
  List<TicketOrder> findByUserEntity_Id(Long userId);

  // Optional alias to match your spec name exactly
  @Query("select o from TicketOrder o where o.userEntity.id = :uid")
  List<TicketOrder> findByUserId(@Param("uid") Long userId);
}
