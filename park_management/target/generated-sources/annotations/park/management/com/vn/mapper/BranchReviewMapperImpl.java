package park.management.com.vn.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import park.management.com.vn.entity.BranchReview;
import park.management.com.vn.model.request.BranchReviewRequest;
import park.management.com.vn.model.response.BranchReviewResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-07T15:21:09+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.16 (Amazon.com Inc.)"
)
@Component
public class BranchReviewMapperImpl implements BranchReviewMapper {

    @Override
    public BranchReview toEntity(BranchReviewRequest request) {
        if ( request == null ) {
            return null;
        }

        BranchReview branchReview = new BranchReview();

        branchReview.setRating( request.getRating() );
        branchReview.setComment( request.getComment() );
        branchReview.setApproved( request.getApproved() );

        return branchReview;
    }

    @Override
    public BranchReviewResponse toResponse(BranchReview review) {
        if ( review == null ) {
            return null;
        }

        BranchReviewResponse.BranchReviewResponseBuilder branchReviewResponse = BranchReviewResponse.builder();

        branchReviewResponse.id( review.getId() );
        branchReviewResponse.rating( review.getRating() );
        branchReviewResponse.comment( review.getComment() );
        branchReviewResponse.approved( review.getApproved() );
        branchReviewResponse.createdAt( review.getCreatedAt() );
        branchReviewResponse.updatedAt( review.getUpdatedAt() );

        return branchReviewResponse.build();
    }
}
