package park.management.com.vn.bootstrap;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import park.management.com.vn.entity.Permission;
import park.management.com.vn.entity.Role;
import park.management.com.vn.entity.UserEntity;
import park.management.com.vn.repository.PermissionRepository;
import park.management.com.vn.repository.RoleRepository;
import park.management.com.vn.repository.UserRepository;

@Component
@Order(1)
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

  private final UserRepository users;
  private final RoleRepository roles;
  private final PermissionRepository perms;
  private final PasswordEncoder encoder;

  @PersistenceContext
  private EntityManager em;

  @Override
  @Transactional
  public void run(String... args) {
    // 1) Ensure base Role + Permission exist
    Role adminRole = getOrCreateRole("ADMIN");
    Permission permCreateNotif = getOrCreatePermission("NOTIFICATION_CREATE");

    // 2) Ensure join: ADMIN -> NOTIFICATION_CREATE (idempotent)
    upsertRolePermission(adminRole.getId(), permCreateNotif.getId());

    // 3) Ensure admin user exists
    UserEntity adminUser = users.findByUsername("admin").orElseGet(() -> {
      UserEntity u = new UserEntity();
      u.setUsername("admin");
      u.setEmail("admin@local");
      u.setPassword(encoder.encode("Admin@1234"));
      // If you have an "enabled/active" flag, set it here (e.g., u.setActive(true))
      return users.save(u);
    });

    // 4) Ensure join: admin user -> ADMIN role (idempotent)
    upsertUserRole(adminUser.getId(), adminRole.getId());
  }

  private Role getOrCreateRole(String name) {
    Role found = em.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
        .setParameter("name", name)
        .setMaxResults(1)
        .getResultStream()
        .findFirst()
        .orElse(null);
    if (found != null) return found;

    Role r = new Role();
    r.setName(name);
    return roles.save(r);
  }

  private Permission getOrCreatePermission(String name) {
    Permission found = em.createQuery("SELECT p FROM Permission p WHERE p.name = :name", Permission.class)
        .setParameter("name", name)
        .setMaxResults(1)
        .getResultStream()
        .findFirst()
        .orElse(null);
    if (found != null) return found;

    Permission p = new Permission();
    p.setName(name);
    return perms.save(p);
  }

  /** Upsert into role_permission join table (PostgreSQL syntax). */
  private void upsertRolePermission(Long roleId, Long permId) {
    em.createNativeQuery("""
        INSERT INTO role_permission(role_id, permission_id)
        VALUES (:roleId, :permId)
        ON CONFLICT DO NOTHING
        """)
      .setParameter("roleId", roleId)
      .setParameter("permId", permId)
      .executeUpdate();
  }

  /** Upsert into user_role join table (PostgreSQL syntax). */
  private void upsertUserRole(Long userId, Long roleId) {
    em.createNativeQuery("""
        INSERT INTO user_role(user_id, role_id)
        VALUES (:userId, :roleId)
        ON CONFLICT DO NOTHING
        """)
      .setParameter("userId", userId)
      .setParameter("roleId", roleId)
      .executeUpdate();
  }
}
