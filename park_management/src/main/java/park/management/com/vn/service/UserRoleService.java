package park.management.com.vn.service;

import jakarta.validation.Valid;
import park.management.com.vn.model.request.UpdateUserRoleRequest;

public interface UserRoleService {

  void updateRole(UpdateUserRoleRequest request);
}
