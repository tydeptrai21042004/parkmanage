package park.management.com.vn.service;

import java.util.List;
import java.util.Optional;

import park.management.com.vn.entity.BranchPromotion;

import park.management.com.vn.entity.ParkBranch;
import park.management.com.vn.entity.Role;
import park.management.com.vn.model.request.ParkBranchRequest;
import park.management.com.vn.model.response.ParkBranchResponse;

public interface ParkBranchService {
  Optional<ParkBranch> findById(Long id);

    ParkBranch getById(Long id);

    List<ParkBranchResponse> getAllBranchPark();

  ParkBranchResponse createBranchPark(ParkBranchRequest request);

  ParkBranchResponse updateBranchPark(Long id, ParkBranchRequest request);

  void deleteBranchPark(Long id);


}
