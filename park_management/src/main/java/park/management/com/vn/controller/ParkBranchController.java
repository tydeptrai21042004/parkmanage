package park.management.com.vn.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import park.management.com.vn.entity.ParkBranch;
import park.management.com.vn.model.request.ParkBranchRequest;
import park.management.com.vn.model.response.ParkBranchResponse;
import park.management.com.vn.service.ParkBranchService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/park-branch")
public class ParkBranchController {

    private final ParkBranchService parkBranchService;

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
}
