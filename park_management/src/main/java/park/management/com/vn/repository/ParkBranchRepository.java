package park.management.com.vn.repository;

import park.management.com.vn.entity.ParkBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkBranchRepository extends JpaRepository<ParkBranch,Long> {

}
