package park.management.com.vn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.management.com.vn.entity.Permission;
import park.management.com.vn.mapper.PermissionMapper;
import park.management.com.vn.model.request.CreatePermissionResponse;
import park.management.com.vn.model.response.CreatePermissionRequest;
import park.management.com.vn.repository.PermissionRepository;
import park.management.com.vn.service.PermissionService;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

  private final PermissionRepository repository;
  private final PermissionMapper permissionMapper;

  @Override
  public CreatePermissionResponse create(CreatePermissionRequest createPermissionRequest) {
    Permission entity = permissionMapper.toEntity(createPermissionRequest);
    Permission save = repository.save(entity);
    return permissionMapper.toCreatePermissionResponse(save);
  }
}
