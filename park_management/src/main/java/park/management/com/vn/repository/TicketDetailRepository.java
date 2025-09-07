package park.management.com.vn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import park.management.com.vn.entity.TicketDetail;

import java.util.List;

@Repository
public interface TicketDetailRepository extends JpaRepository<TicketDetail,Long> {

    List<TicketDetail> findByTicketOrder_Id(Long ticketOrderId);


}
