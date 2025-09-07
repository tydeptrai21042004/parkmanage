package park.management.com.vn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import park.management.com.vn.entity.BulkPricingRule;

import java.util.Optional;

@Repository
public interface BulkPricingRuleRepository extends JpaRepository<BulkPricingRule,Long> {

    Optional<BulkPricingRule> findByTicketType_Id(Long ticketTypeId);

}
