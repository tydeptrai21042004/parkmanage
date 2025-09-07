package park.management.com.vn.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import park.management.com.vn.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

  List<Role> findByIdIn(List<Long> roleIds);
}
