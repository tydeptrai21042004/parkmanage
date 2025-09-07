package park.management.com.vn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import park.management.com.vn.entity.BranchPromotion;

@Repository
public interface BranchPromotionRepository extends JpaRepository<BranchPromotion, Long> {
}
