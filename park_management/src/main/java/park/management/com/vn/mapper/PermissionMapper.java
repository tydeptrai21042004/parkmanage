package park.management.com.vn.mapper;


import org.mapstruct.Mapper;
import park.management.com.vn.entity.Permission;
import park.management.com.vn.model.request.CreatePermissionResponse;
import park.management.com.vn.model.response.CreatePermissionRequest;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

  Permission toEntity(CreatePermissionRequest createPermissionRequest);

  CreatePermissionResponse toCreatePermissionResponse(Permission save);
}
