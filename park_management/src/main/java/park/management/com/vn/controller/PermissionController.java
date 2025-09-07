package park.management.com.vn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import park.management.com.vn.model.request.CreatePermissionResponse;
import park.management.com.vn.model.response.CreatePermissionRequest;
import park.management.com.vn.service.PermissionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permission")
public class PermissionController {

  private final PermissionService permissionService;

  @PostMapping
  public ResponseEntity<CreatePermissionResponse> create(@RequestBody @Valid
  CreatePermissionRequest createPermissionRequest) {
    CreatePermissionResponse createPermissionResponse = permissionService.create(
        createPermissionRequest);
    return ResponseEntity.ok(createPermissionResponse);
  }
}
