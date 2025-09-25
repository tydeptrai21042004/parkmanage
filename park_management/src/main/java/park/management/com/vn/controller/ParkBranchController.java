package park.management.com.vn.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.model.request.ParkBranchRequest;
import park.management.com.vn.model.request.UpdateImageRequest;
import park.management.com.vn.model.response.ParkBranchResponse;
import park.management.com.vn.repository.ParkBranchRepository;
import park.management.com.vn.entity.ParkBranch;
import park.management.com.vn.service.ParkBranchService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/park-branch")
public class ParkBranchController {

    private final ParkBranchService parkBranchService;
    private final ParkBranchRepository parkBranchRepository; // thêm để update ảnh nhanh

    @GetMapping
    public ResponseEntity<List<ParkBranchResponse>> getAllBranchPark() {
        return ResponseEntity.ok(parkBranchService.getAllBranchPark());
    }

    @PostMapping
    public ResponseEntity<ParkBranchResponse> createBranchPark(@RequestBody ParkBranchRequest request) {
        return ResponseEntity.ok(parkBranchService.createBranchPark(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkBranchResponse> updateBranchPark(
            @PathVariable Long id,
            @RequestBody ParkBranchRequest request) {
        return ResponseEntity.ok(parkBranchService.updateBranchPark(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranchPark(@PathVariable Long id) {
        parkBranchService.deleteBranchPark(id);
        return ResponseEntity.noContent().build();
    }

    // NEW: update ảnh riêng
    @PutMapping("/{id}/image")
    public ResponseEntity<Void> updateImage(@PathVariable Long id,
                                            @RequestBody UpdateImageRequest req) {
        ParkBranch b = parkBranchRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("BRANCH_NOT_FOUND"));
        b.setImageUrl(req.getImageUrl());
        parkBranchRepository.save(b);
        return ResponseEntity.noContent().build();
    }
}
