package park.management.com.vn.service;

import jakarta.validation.constraints.NotEmpty;
import park.management.com.vn.entity.Role;
import park.management.com.vn.model.request.RoleRequest;
import park.management.com.vn.model.response.RoleResponse;

import java.util.List;
import java.util.Optional;

public interface RoleService {

  Optional<Role> findById(Long id);

  List<RoleResponse> getAllRole();

  RoleResponse createRole(RoleRequest request);

  RoleResponse updateRole(Long id, RoleRequest request);

  void deleteRole(Long id);

  List<Role> findByIdIn(List<Long> roleIds);
}
