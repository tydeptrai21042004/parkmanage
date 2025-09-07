package park.management.com.vn.mapper;

import org.mapstruct.Mapper;
import park.management.com.vn.entity.BranchReview;
import park.management.com.vn.model.request.BranchReviewRequest;
import park.management.com.vn.model.response.BranchReviewResponse;

@Mapper(componentModel = "spring")
public interface BranchReviewMapper {
    BranchReview toEntity(BranchReviewRequest request);

    BranchReviewResponse toResponse(BranchReview review);
}
