package park.management.com.vn.mapper;

import org.mapstruct.Mapper;
import park.management.com.vn.entity.Role;
import park.management.com.vn.model.request.RoleRequest;
import park.management.com.vn.model.response.RoleResponse;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toEntity(RoleRequest request);

    RoleResponse toResponse(Role entity);
}
