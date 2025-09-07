package park.management.com.vn.service;

import jakarta.validation.Valid;
import park.management.com.vn.model.request.CreatePermissionResponse;
import park.management.com.vn.model.response.CreatePermissionRequest;

public interface PermissionService {

  CreatePermissionResponse create(@Valid CreatePermissionRequest createPermissionRequest);
}
