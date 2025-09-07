package park.management.com.vn.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import park.management.com.vn.entity.UserEntity;
import park.management.com.vn.model.request.RegisterUserRequest;
import park.management.com.vn.model.response.RegisterUserResponse;
import park.management.com.vn.model.response.UserResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-07T15:21:10+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.16 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity toEntity(RegisterUserRequest request) {
        if ( request == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setEmail( request.getEmail() );
        userEntity.setPassword( request.getPassword() );

        return userEntity;
    }

    @Override
    public RegisterUserResponse toRegisterUserResponse(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        RegisterUserResponse.RegisterUserResponseBuilder registerUserResponse = RegisterUserResponse.builder();

        registerUserResponse.username( userEntity.getUsername() );
        registerUserResponse.email( userEntity.getEmail() );

        return registerUserResponse.build();
    }

    @Override
    public UserResponse toUserDetailResponse(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        if ( userEntity.getId() != null ) {
            userResponse.setId( userEntity.getId().intValue() );
        }
        userResponse.setUsername( userEntity.getUsername() );
        userResponse.setEmail( userEntity.getEmail() );

        return userResponse;
    }
}
