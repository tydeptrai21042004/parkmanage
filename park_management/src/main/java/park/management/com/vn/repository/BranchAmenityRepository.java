package park.management.com.vn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import park.management.com.vn.entity.BranchAmenity;

import java.util.List;

@Repository
public interface BranchAmenityRepository extends JpaRepository<BranchAmenity, Long> {
    List<BranchAmenity> findByParkBranch_Id(Long branchId);
}
