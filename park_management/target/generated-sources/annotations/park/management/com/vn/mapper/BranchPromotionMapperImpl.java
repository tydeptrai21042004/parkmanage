package park.management.com.vn.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import park.management.com.vn.entity.BranchPromotion;
import park.management.com.vn.model.request.BranchPromotionRequest;
import park.management.com.vn.model.response.BranchPromotionResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-07T15:21:09+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.16 (Amazon.com Inc.)"
)
@Component
public class BranchPromotionMapperImpl implements BranchPromotionMapper {

    @Override
    public BranchPromotion toEntity(BranchPromotionRequest request) {
        if ( request == null ) {
            return null;
        }

        BranchPromotion branchPromotion = new BranchPromotion();

        branchPromotion.setDescription( request.getDescription() );
        branchPromotion.setIsActive( request.getIsActive() );

        return branchPromotion;
    }

    @Override
    public BranchPromotionResponse toResponse(BranchPromotion entity) {
        if ( entity == null ) {
            return null;
        }

        BranchPromotionResponse.BranchPromotionResponseBuilder branchPromotionResponse = BranchPromotionResponse.builder();

        branchPromotionResponse.id( entity.getId() );
        branchPromotionResponse.description( entity.getDescription() );
        branchPromotionResponse.isActive( entity.getIsActive() );
        branchPromotionResponse.createdAt( entity.getCreatedAt() );
        branchPromotionResponse.updatedAt( entity.getUpdatedAt() );

        return branchPromotionResponse.build();
    }
}
