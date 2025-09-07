package park.management.com.vn.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import park.management.com.vn.entity.ParkBranch;
import park.management.com.vn.entity.UserEntity;
import park.management.com.vn.entity.UserRole;
import park.management.com.vn.entity.Wallet;
import park.management.com.vn.exception.user.UserNotFoundException;
import park.management.com.vn.mapper.RoleMapper;
import park.management.com.vn.mapper.UserMapper;
import park.management.com.vn.model.request.LoginRequest;
import park.management.com.vn.model.request.RegisterUserRequest;
import park.management.com.vn.model.request.UserRequest;
import park.management.com.vn.model.response.LoginResponse;
import park.management.com.vn.model.response.RegisterUserResponse;
import park.management.com.vn.model.response.RoleResponse;
import park.management.com.vn.model.response.UserResponse;
import park.management.com.vn.repository.UserRepository;
import park.management.com.vn.repository.WalletRepository;
import park.management.com.vn.service.ParkBranchService;
import park.management.com.vn.service.RoleService;
import park.management.com.vn.service.UserService;
import park.management.com.vn.utils.JWTTokenUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final WalletRepository walletRepository; // <-- THÊM
  private final UserMapper userMapper;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final JWTTokenUtils jwtTokenUtils;
  private final RoleService roleService;
  private final ParkBranchService parkBranchService;
  private final RoleMapper roleMapper;

  @Override
  public List<UserEntity> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public Optional<UserEntity> findUserById(Long id) {
    return userRepository.findById(id);
  }

  @Override
  public UserEntity getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  @Override
  public RegisterUserResponse createUser(RegisterUserRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new RuntimeException("Email already exists");
    }

    UserEntity userEntity = userMapper.toEntity(request);
    userEntity.setUsername(request.getEmail());
    userEntity.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));

    // Lưu user trước
    UserEntity saved = userRepository.save(userEntity);

    // Tạo ví gắn user ngay lập tức
    Wallet wallet = Wallet.builder()
        .userEntity(saved)
        .balance(BigDecimal.ZERO)
        .build();
    walletRepository.save(wallet);

    return userMapper.toRegisterUserResponse(saved);
  }

  @Override
  public UserEntity updateUser(Long id, UserRequest updatedUsers) {
    return userRepository.findById(id).map(user -> {
      ParkBranch parkBranch = parkBranchService.findById(updatedUsers.getParkBranchId())
          .orElseThrow(() -> new RuntimeException("ParkBranch not found!"));
      user.setParkBranch(parkBranch);
      return userRepository.save(user);
    }).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
  }

  @Override
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  @Override
  public LoginResponse login(LoginRequest request) {
    UserEntity userEntity = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

    if (bCryptPasswordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
      return LoginResponse.builder()
          .accessToken(jwtTokenUtils.generateAccessToken(userEntity))
          .build();
    }

    throw new RuntimeException("PASSWORD_INCORRECT");
  }

  @Override
  public UserResponse getUserDetail(Long id) {
    UserEntity userEntity = findUserById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

    UserResponse userDetail = userMapper.toUserDetailResponse(userEntity);
    Optional.ofNullable(userEntity.getUserRoles())
        .ifPresent(userRoles -> {
          for (UserRole userRole : userRoles) {
            RoleResponse roleResponse = roleMapper.toResponse(userRole.getRole());
            if (userDetail.getRoles() == null) {
              userDetail.setRoles(new ArrayList<>());
            }
            userDetail.getRoles().add(roleResponse);
          }
        });

    return userDetail;
  }

  @Override
  public boolean havePermission(String username, String roleAdmin, String permission) {
    return userRepository.havePermission(username, roleAdmin, permission);
  }
}
