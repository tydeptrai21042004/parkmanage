package park.management.com.vn.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import park.management.com.vn.entity.Shift;
import park.management.com.vn.model.request.ShiftRequest;
import park.management.com.vn.model.response.ShiftResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-07T15:21:09+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.16 (Amazon.com Inc.)"
)
@Component
public class ShiftMapperImpl implements ShiftMapper {

    @Override
    public Shift toEntity(ShiftRequest request) {
        if ( request == null ) {
            return null;
        }

        Shift shift = new Shift();

        shift.setStartTime( request.getStartTime() );
        shift.setEndTime( request.getEndTime() );
        shift.setDescription( request.getDescription() );

        return shift;
    }

    @Override
    public ShiftResponse toResponse(Shift entity) {
        if ( entity == null ) {
            return null;
        }

        ShiftResponse.ShiftResponseBuilder shiftResponse = ShiftResponse.builder();

        shiftResponse.id( entity.getId() );
        shiftResponse.startTime( entity.getStartTime() );
        shiftResponse.endTime( entity.getEndTime() );
        shiftResponse.description( entity.getDescription() );
        shiftResponse.createdAt( entity.getCreatedAt() );
        shiftResponse.updatedAt( entity.getUpdatedAt() );
        shiftResponse.createdBy( entity.getCreatedBy() );
        shiftResponse.updatedBy( entity.getUpdatedBy() );

        return shiftResponse.build();
    }
}
