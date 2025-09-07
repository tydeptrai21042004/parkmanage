package park.management.com.vn.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import park.management.com.vn.entity.Role;
import park.management.com.vn.model.request.RoleRequest;
import park.management.com.vn.model.response.RoleResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-07T15:21:09+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.16 (Amazon.com Inc.)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public Role toEntity(RoleRequest request) {
        if ( request == null ) {
            return null;
        }

        Role role = new Role();

        role.setName( request.getName() );
        role.setDescription( request.getDescription() );

        return role;
    }

    @Override
    public RoleResponse toResponse(Role entity) {
        if ( entity == null ) {
            return null;
        }

        RoleResponse.RoleResponseBuilder roleResponse = RoleResponse.builder();

        roleResponse.id( entity.getId() );
        roleResponse.name( entity.getName() );
        roleResponse.description( entity.getDescription() );

        return roleResponse.build();
    }
}
