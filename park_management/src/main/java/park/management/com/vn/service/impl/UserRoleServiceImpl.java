package park.management.com.vn.service.impl;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import park.management.com.vn.entity.Role;
import park.management.com.vn.entity.UserEntity;
import park.management.com.vn.entity.UserRole;
import park.management.com.vn.exception.user.UserNotFoundException;
import park.management.com.vn.model.request.UpdateUserRoleRequest;
import park.management.com.vn.repository.UserRoleRepository;
import park.management.com.vn.service.RoleService;
import park.management.com.vn.service.UserRoleService;
import park.management.com.vn.service.UserService;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

  private final UserRoleRepository repository;
  private final RoleService roleService;
  private final UserService userService;

  @Override
  @Transactional
  public void updateRole(UpdateUserRoleRequest request) {
    UserEntity userEntity = userService.findUserById(request.getUserId())
        .orElseThrow(() -> new UserNotFoundException(request.getUserId()));
    List<Role> roles = roleService.findByIdIn(request.getRoleIds());
    if (roles.isEmpty()) {
      throw new RuntimeException("Role not found");
    }

    List<UserRole> userRoles = new ArrayList<>();
    for (Role role : roles) {
      UserRole userRole = new UserRole(role, userEntity);
      userRoles.add(userRole);
    }
    // Xóa role cũ
    repository.deleteByUserEntityId(request.getUserId());

    // Add role mới
    repository.saveAll(userRoles);
  }
}
