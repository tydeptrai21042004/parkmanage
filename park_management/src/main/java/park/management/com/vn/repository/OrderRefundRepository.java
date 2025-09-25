package park.management.com.vn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import park.management.com.vn.entity.OrderRefund;

public interface OrderRefundRepository extends JpaRepository<OrderRefund, Long> {
    boolean existsByOrder_Id(Long orderId);          // <- needed by service
    Optional<OrderRefund> findByOrder_Id(Long orderId); // <- needed by service
}
