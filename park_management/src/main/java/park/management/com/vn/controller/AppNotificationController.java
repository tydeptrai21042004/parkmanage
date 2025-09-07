package park.management.com.vn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.model.request.CreateNotificationRequest;
import park.management.com.vn.service.AppNotificationService;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class AppNotificationController {

    private final AppNotificationService service;

    // Tạo/schedule notification: chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateNotificationRequest req) {
        Long id = service.create(req);
        return ResponseEntity.ok().body(
            java.util.Map.of("id", id, "message", "Notification scheduled/queued")
        );
    }
}
