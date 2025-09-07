package park.management.com.vn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.model.request.BranchPromotionRequest;
import park.management.com.vn.model.response.BranchPromotionResponse;
import park.management.com.vn.service.BranchPromotionService;

import java.util.List;

@RestController
@RequestMapping("/api/branch-promotion")
@RequiredArgsConstructor
public class BranchPromotionController {

    private final BranchPromotionService branchPromotionService;

    @GetMapping("/{id}")
    public ResponseEntity<BranchPromotionResponse> getBranchPromotionById(@PathVariable Long id) {
        return ResponseEntity.ok(branchPromotionService.getBranchPromotionResponseById(id));
    }

    @GetMapping
    public ResponseEntity<List<BranchPromotionResponse>> getAllBranchPromotion() {
        return ResponseEntity.ok(branchPromotionService.getAllBranchPromotion());
    }

    @PostMapping
    public ResponseEntity<BranchPromotionResponse> createBranchPromotion(@RequestBody BranchPromotionRequest request) {
        return ResponseEntity.ok(branchPromotionService.createBranchPromotion(request));
    }


    @PutMapping("/{id}")
    public ResponseEntity<BranchPromotionResponse> updateBranchPromotion(@PathVariable Long id, @RequestBody BranchPromotionRequest request) {
        return ResponseEntity.ok(branchPromotionService.updateBranchPromotion(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranchPromotion(@PathVariable Long id) {
        branchPromotionService.deleteBranchPromotion(id);
        return ResponseEntity.noContent().build();
    }
}