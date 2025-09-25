package park.management.com.vn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import park.management.com.vn.entity.BranchReview;

import java.util.List;

@Repository
public interface BranchReviewRepository extends JpaRepository<BranchReview, Long> {
    List<BranchReview> findByParkBranch_Id(Long branchId);
}
