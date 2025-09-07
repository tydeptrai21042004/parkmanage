package park.management.com.vn.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import park.management.com.vn.entity.Permission;
import park.management.com.vn.model.request.CreatePermissionResponse;
import park.management.com.vn.model.response.CreatePermissionRequest;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-07T15:21:09+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.16 (Amazon.com Inc.)"
)
@Component
public class PermissionMapperImpl implements PermissionMapper {

    @Override
    public Permission toEntity(CreatePermissionRequest createPermissionRequest) {
        if ( createPermissionRequest == null ) {
            return null;
        }

        Permission permission = new Permission();

        permission.setId( createPermissionRequest.getId() );
        permission.setName( createPermissionRequest.getName() );
        permission.setDescription( createPermissionRequest.getDescription() );

        return permission;
    }

    @Override
    public CreatePermissionResponse toCreatePermissionResponse(Permission save) {
        if ( save == null ) {
            return null;
        }

        CreatePermissionResponse createPermissionResponse = new CreatePermissionResponse();

        createPermissionResponse.setName( save.getName() );
        createPermissionResponse.setDescription( save.getDescription() );

        return createPermissionResponse;
    }
}
