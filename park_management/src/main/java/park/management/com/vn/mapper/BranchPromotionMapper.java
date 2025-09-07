package park.management.com.vn.mapper;

import org.mapstruct.Mapper;
import park.management.com.vn.entity.BranchPromotion;
import park.management.com.vn.model.request.BranchPromotionRequest;
import park.management.com.vn.model.response.BranchPromotionResponse;

@Mapper(componentModel = "spring")
public interface BranchPromotionMapper {

    BranchPromotion toEntity(BranchPromotionRequest request);

    BranchPromotionResponse toResponse(BranchPromotion entity);
}
