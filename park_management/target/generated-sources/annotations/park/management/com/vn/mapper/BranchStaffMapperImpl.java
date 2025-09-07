package park.management.com.vn.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import park.management.com.vn.entity.BranchStaff;
import park.management.com.vn.model.request.BranchStaffRequest;
import park.management.com.vn.model.response.BranchStaffResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-07T15:21:09+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.16 (Amazon.com Inc.)"
)
@Component
public class BranchStaffMapperImpl implements BranchStaffMapper {

    @Override
    public BranchStaff toEntity(BranchStaffRequest request) {
        if ( request == null ) {
            return null;
        }

        BranchStaff branchStaff = new BranchStaff();

        branchStaff.setRole( request.getRole() );
        branchStaff.setDescription( request.getDescription() );

        return branchStaff;
    }

    @Override
    public BranchStaffResponse toResponse(BranchStaff entity) {
        if ( entity == null ) {
            return null;
        }

        BranchStaffResponse.BranchStaffResponseBuilder branchStaffResponse = BranchStaffResponse.builder();

        branchStaffResponse.id( entity.getId() );
        branchStaffResponse.role( entity.getRole() );
        branchStaffResponse.description( entity.getDescription() );
        branchStaffResponse.createdAt( entity.getCreatedAt() );
        branchStaffResponse.updatedAt( entity.getUpdatedAt() );

        return branchStaffResponse.build();
    }
}
