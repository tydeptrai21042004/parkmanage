package park.management.com.vn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.model.request.StaffAssignmentRequest;
import park.management.com.vn.model.response.StaffAssignmentResponse;
import park.management.com.vn.service.StaffAssignmentService;

import java.util.List;

@RestController
@RequestMapping("/api/staff-assignments")
@RequiredArgsConstructor
public class StaffAssignmentController {

    private final StaffAssignmentService staffAssignmentService;

    @GetMapping
    public ResponseEntity<List<StaffAssignmentResponse>> getAllStaffAssignment() {
        return ResponseEntity.ok(staffAssignmentService.getAllStaffAssignment());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffAssignmentResponse> getStaffAssignmentById(@PathVariable Long id) {
        return ResponseEntity.ok(staffAssignmentService.getStaffAssignmentById(id));
    }

    @PostMapping
    public ResponseEntity<StaffAssignmentResponse> createStaffAssignment(@Valid @RequestBody StaffAssignmentRequest request) {
        StaffAssignmentResponse response = staffAssignmentService.createStaffAssignment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffAssignmentResponse> updateStaffAssignment(@PathVariable Long id,
                                                          @Valid @RequestBody StaffAssignmentRequest request) {
        StaffAssignmentResponse response = staffAssignmentService.updateStaffAssignment(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaffAssignment(@PathVariable Long id) {
        staffAssignmentService.deleteStaffAssignmentById(id);
        return ResponseEntity.noContent().build();
    }
}