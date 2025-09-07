package park.management.com.vn.mapper;

import org.mapstruct.Mapper;
import park.management.com.vn.entity.UserEntity;
import park.management.com.vn.model.request.RegisterUserRequest;
import park.management.com.vn.model.response.RegisterUserResponse;
import park.management.com.vn.model.response.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserEntity toEntity(RegisterUserRequest request);

  RegisterUserResponse toRegisterUserResponse(UserEntity userEntity);
  UserResponse toUserDetailResponse(UserEntity userEntity);
}
