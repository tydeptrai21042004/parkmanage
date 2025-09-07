package park.management.com.vn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.constant.PermissionConstant;
import park.management.com.vn.model.request.RoleRequest;
import park.management.com.vn.model.response.RoleResponse;
import park.management.com.vn.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

  private final RoleService roleService;

  @GetMapping
  @PreAuthorize(PermissionConstant.VIEW_ROLE)
  public ResponseEntity<List<RoleResponse>> getAllRole() {
    return ResponseEntity.ok(roleService.getAllRole());
  }

  @PostMapping
  @PreAuthorize(PermissionConstant.CREATE_ROLE)
  public ResponseEntity<RoleResponse> createRole(@RequestBody RoleRequest request) {
    return ResponseEntity.ok(roleService.createRole(request));
  }

  @PutMapping("/{id}")
  @PreAuthorize(PermissionConstant.UPDATE_ROLE)
  public ResponseEntity<RoleResponse> updateRole(
      @PathVariable Long id,
      @RequestBody RoleRequest request) {
    return ResponseEntity.ok(roleService.updateRole(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize(PermissionConstant.DELETE_ROLE)
  public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
    roleService.deleteRole(id);
    return ResponseEntity.noContent().build();
  }
}
