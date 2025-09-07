package park.management.com.vn.service;

import park.management.com.vn.entity.BranchPromotion;
import park.management.com.vn.model.request.BranchPromotionRequest;
import park.management.com.vn.model.response.BranchPromotionResponse;

import java.util.List;
import java.util.Optional;

public interface BranchPromotionService {
    BranchPromotionResponse getBranchPromotionResponseById(Long id);

    Optional<BranchPromotion> findBranchPromotionById(Long id);

    BranchPromotion getBranchPromotionById(Long id);

    List<BranchPromotionResponse> getAllBranchPromotion();

    BranchPromotionResponse createBranchPromotion(BranchPromotionRequest request);

    BranchPromotionResponse updateBranchPromotion(Long id, BranchPromotionRequest request);

    void deleteBranchPromotion(Long id);
}
