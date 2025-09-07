package park.management.com.vn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import park.management.com.vn.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByUsername(String username);

  boolean existsByEmail(String email);

  @Query(value = "SELECT COUNT(u) > 0\n"
      + "FROM UserEntity u\n"
      + "INNER JOIN u.userRoles ur\n"
      + "INNER JOIN ur.role r\n"
      + "LEFT JOIN r.rolePermissions rp\n"
      + "LEFT JOIN rp.permission p\n"
      + "WHERE u.username = :username AND (r.name = :adminRole OR p.name = :permission)")
  boolean havePermission(String username, String adminRole, String permission);
}
