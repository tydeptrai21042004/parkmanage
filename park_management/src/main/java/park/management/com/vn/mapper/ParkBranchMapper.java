package park.management.com.vn.mapper;

import org.mapstruct.Mapper;
import park.management.com.vn.entity.ParkBranch;
import park.management.com.vn.model.request.ParkBranchRequest;
import park.management.com.vn.model.response.ParkBranchResponse;

@Mapper(componentModel = "spring")
public interface ParkBranchMapper {
    ParkBranch toEntity(ParkBranchRequest request);

    ParkBranchResponse toResponse(ParkBranch entity);
}
