package park.management.com.vn.service;

import park.management.com.vn.model.request.BranchReviewRequest;
import park.management.com.vn.model.response.BranchReviewResponse;

import java.util.List;

public interface BranchReviewService {

    BranchReviewResponse createReview(BranchReviewRequest request);
    List<BranchReviewResponse> getAllReviews();
    BranchReviewResponse getReviewById(Long id);
}
