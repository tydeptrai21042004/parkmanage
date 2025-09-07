package park.management.com.vn.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import park.management.com.vn.entity.ParkBranch;
import park.management.com.vn.model.request.ParkBranchRequest;
import park.management.com.vn.model.response.ParkBranchResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-07T15:21:10+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.16 (Amazon.com Inc.)"
)
@Component
public class ParkBranchMapperImpl implements ParkBranchMapper {

    @Override
    public ParkBranch toEntity(ParkBranchRequest request) {
        if ( request == null ) {
            return null;
        }

        ParkBranch parkBranch = new ParkBranch();

        parkBranch.setName( request.getName() );
        parkBranch.setAddress( request.getAddress() );
        parkBranch.setLocation( request.getLocation() );
        parkBranch.setOpen( request.getOpen() );
        parkBranch.setClose( request.getClose() );

        return parkBranch;
    }

    @Override
    public ParkBranchResponse toResponse(ParkBranch entity) {
        if ( entity == null ) {
            return null;
        }

        ParkBranchResponse.ParkBranchResponseBuilder parkBranchResponse = ParkBranchResponse.builder();

        parkBranchResponse.id( entity.getId() );
        parkBranchResponse.name( entity.getName() );
        parkBranchResponse.address( entity.getAddress() );
        parkBranchResponse.location( entity.getLocation() );
        parkBranchResponse.open( entity.getOpen() );
        parkBranchResponse.close( entity.getClose() );
        parkBranchResponse.createdAt( entity.getCreatedAt() );
        parkBranchResponse.updatedAt( entity.getUpdatedAt() );

        return parkBranchResponse.build();
    }
}
