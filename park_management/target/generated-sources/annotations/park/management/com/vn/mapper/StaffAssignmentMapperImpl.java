package park.management.com.vn.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import park.management.com.vn.entity.StaffAssignment;
import park.management.com.vn.model.request.StaffAssignmentRequest;
import park.management.com.vn.model.response.StaffAssignmentResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-07T15:21:10+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.16 (Amazon.com Inc.)"
)
@Component
public class StaffAssignmentMapperImpl implements StaffAssignmentMapper {

    @Override
    public StaffAssignment toEntity(StaffAssignmentRequest request) {
        if ( request == null ) {
            return null;
        }

        StaffAssignment staffAssignment = new StaffAssignment();

        staffAssignment.setAssignedDate( request.getAssignedDate() );

        return staffAssignment;
    }

    @Override
    public StaffAssignmentResponse toResponse(StaffAssignment entity) {
        if ( entity == null ) {
            return null;
        }

        StaffAssignmentResponse.StaffAssignmentResponseBuilder staffAssignmentResponse = StaffAssignmentResponse.builder();

        staffAssignmentResponse.id( entity.getId() );
        staffAssignmentResponse.assignedDate( entity.getAssignedDate() );
        staffAssignmentResponse.createdAt( entity.getCreatedAt() );
        staffAssignmentResponse.updatedAt( entity.getUpdatedAt() );

        return staffAssignmentResponse.build();
    }
}
