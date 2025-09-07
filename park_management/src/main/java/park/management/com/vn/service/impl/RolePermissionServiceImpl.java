package park.management.com.vn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.management.com.vn.repository.RolePermissionRepository;
import park.management.com.vn.service.RolePermissionService;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {

  private final RolePermissionRepository repository;
}
