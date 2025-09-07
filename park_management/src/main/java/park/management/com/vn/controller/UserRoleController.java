package park.management.com.vn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import park.management.com.vn.constant.PermissionConstant;
import park.management.com.vn.model.request.UpdateUserRoleRequest;
import park.management.com.vn.service.UserRoleService;

@RestController
@RequestMapping("/api/user-role")
@RequiredArgsConstructor
public class UserRoleController {

  private final UserRoleService userRoleService;

  @PostMapping
  @PreAuthorize(PermissionConstant.ASSIGN_ROLE)
  public ResponseEntity<?> updateRole(@RequestBody @Valid UpdateUserRoleRequest request) {
    userRoleService.updateRole(request);
    return ResponseEntity.ok().build();
  }
}
