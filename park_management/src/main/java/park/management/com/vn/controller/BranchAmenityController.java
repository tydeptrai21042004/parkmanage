package park.management.com.vn.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.entity.BranchAmenity;
import park.management.com.vn.model.request.UpdateImageRequest;
import park.management.com.vn.repository.BranchAmenityRepository;

import java.util.List;

@RestController
@RequestMapping("/api/branch-amenities")
@RequiredArgsConstructor
@Tag(name = "branch-amenity-controller")
public class BranchAmenityController {

  private final BranchAmenityRepository branchAmenityRepository;

  @GetMapping
  public ResponseEntity<List<BranchAmenity>> list() {
    return ResponseEntity.ok(branchAmenityRepository.findAll());
  }

  // NEW: getAll...ofBranch
  @GetMapping("/of-branch/{branchId}")
  public ResponseEntity<List<BranchAmenity>> ofBranch(@PathVariable Long branchId) {
    return ResponseEntity.ok(branchAmenityRepository.findByParkBranch_Id(branchId));
  }

  // NEW: update ảnh riêng
  @PutMapping("/{id}/image")
  public ResponseEntity<Void> updateImage(@PathVariable Long id,
                                          @RequestBody @Valid UpdateImageRequest req) {
    BranchAmenity a = branchAmenityRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("AMENITY_NOT_FOUND"));
    a.setImageUrl(req.getImageUrl()); // đảm bảo entity có field imageUrl
    branchAmenityRepository.save(a);
    return ResponseEntity.noContent().build();
  }
}
