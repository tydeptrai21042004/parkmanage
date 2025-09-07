package park.management.com.vn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import park.management.com.vn.service.RolePermissionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/role-permission")
public class RolePermissionController {

  private final RolePermissionService rolePermissionService;
}
