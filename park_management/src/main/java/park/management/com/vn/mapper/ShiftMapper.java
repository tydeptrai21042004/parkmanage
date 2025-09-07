package park.management.com.vn.mapper;

import org.mapstruct.Mapper;
import park.management.com.vn.entity.Shift;
import park.management.com.vn.model.request.ShiftRequest;
import park.management.com.vn.model.response.ShiftResponse;

@Mapper(componentModel = "spring")
public interface ShiftMapper {

    Shift toEntity(ShiftRequest request);

    ShiftResponse toResponse(Shift entity);
}
