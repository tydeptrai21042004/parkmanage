package park.management.com.vn.service;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import park.management.com.vn.entity.UserEntity;
import park.management.com.vn.model.request.LoginRequest;
import park.management.com.vn.model.request.RegisterUserRequest;
import park.management.com.vn.model.request.UserRequest;
import park.management.com.vn.model.response.LoginResponse;
import park.management.com.vn.model.response.RegisterUserResponse;
import park.management.com.vn.model.response.UserResponse;

public interface UserService {

  List<UserEntity> getAllUsers();

  Optional<UserEntity> findUserById(Long id);

  UserEntity getUserById(Long id);

  RegisterUserResponse createUser(RegisterUserRequest request);

  UserEntity updateUser(Long id, UserRequest userRequest);

  void deleteUser(Long id);

  LoginResponse login(@Valid LoginRequest request);

  UserResponse getUserDetail(Long id);

  boolean havePermission(String username, String roleAdmin, String permission);
}
