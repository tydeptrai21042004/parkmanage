package park.management.com.vn.config;

import java.io.Serializable;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import park.management.com.vn.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionEvaluatorImpl implements PermissionEvaluator {

  private final UserService userService;
  private static final String ROLE_ADMIN = "ROLE_ADMIN";

  @Override
  public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
    if (Objects.isNull(auth) || Objects.isNull(permission)) {
      return false;
    }
    return userService.havePermission(auth.getName(), ROLE_ADMIN, (String) permission);
  }

  @Override
  public boolean hasPermission(Authentication auth, Serializable targetId, String targetType,
      Object permission) {
    if (Objects.isNull(auth) || Objects.isNull(targetType)) {
      return false;
    }
    return true;
  }
}
