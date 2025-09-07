package park.management.com.vn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.model.request.BranchStaffRequest;
import park.management.com.vn.model.response.BranchStaffResponse;
import park.management.com.vn.service.BranchStaffService;

import java.util.List;

@RestController
@RequestMapping("/api/branch-staff")
@RequiredArgsConstructor
public class BranchStaffController {

    private final BranchStaffService branchStaffService;

    @GetMapping("/{id}")
    public ResponseEntity<BranchStaffResponse> getBranchStaffById(@PathVariable Long id) {
        BranchStaffResponse response = branchStaffService.getBranchStaffById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BranchStaffResponse>> getAllBranchStaff() {
        List<BranchStaffResponse> responseList = branchStaffService.getAllBranchStaff();
        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    public ResponseEntity<BranchStaffResponse> createBranchStaff(@Valid @RequestBody BranchStaffRequest request) {
        BranchStaffResponse response = branchStaffService.createBranchStaff(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranchStaff(@PathVariable Long id) {
        branchStaffService.deleteBranchStaff(id);
        return ResponseEntity.noContent().build();
    }
}
