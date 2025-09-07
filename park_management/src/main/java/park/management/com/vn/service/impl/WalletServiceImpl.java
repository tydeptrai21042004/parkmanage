package park.management.com.vn.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import park.management.com.vn.entity.UserEntity;
import park.management.com.vn.entity.Wallet;
import park.management.com.vn.mapper.WalletMapper;
import park.management.com.vn.model.request.WalletRequest;
import park.management.com.vn.model.response.WalletResponse;
import park.management.com.vn.repository.UserRepository;
import park.management.com.vn.repository.WalletRepository;
import park.management.com.vn.service.WalletService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

  private final WalletRepository walletRepository;
  private final WalletMapper mapper;
  private final UserRepository usersRepository;

  /** Create (if missing) and return the wallet for the authenticated user. */
  @Override
  @Transactional
  public WalletResponse createWallet(WalletRequest request) {
    // 1) Resolve current user from JWT (preferred)
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = Optional.ofNullable(auth).map(Authentication::getName).orElse(null);

    UserEntity user;
    if (username != null && !username.isBlank()) {
      user = usersRepository.findByUsername(username)
          .orElseThrow(() -> new RuntimeException("User not found by username: " + username));
    } else if (request.getUserId() != null) {
      // fallback for admin/system flows
      user = usersRepository.findById(request.getUserId())
          .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));
    } else {
      throw new IllegalArgumentException("Cannot resolve user (missing JWT and userId).");
    }

    // 2) Idempotent: one wallet per user
    Wallet existing = walletRepository.findByUserEntityId(user.getId()).orElse(null);
    if (existing != null) {
      return mapper.toResponse(existing);
    }

    // 3) Create a new wallet
    Wallet wallet = mapper.toEntity(request);
    wallet.setUserEntity(user);

    BigDecimal bal = wallet.getBalance();
    if (bal == null || bal.compareTo(BigDecimal.ZERO) < 0) {
      wallet.setBalance(BigDecimal.ZERO);
    }

    wallet = walletRepository.save(wallet);
    return mapper.toResponse(wallet);
  }

  @Override
  public WalletResponse getWalletById(Long id) {
    Wallet wallet = walletRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Wallet not found: " + id));
    return mapper.toResponse(wallet);
  }

  @Override
  public List<WalletResponse> getAllWallets() {
    return walletRepository.findAll().stream()
        .map(mapper::toResponse)
        .collect(Collectors.toList());
  }

  /** Update only mutable fields; keep link to user and audit fields. */
  @Override
  @Transactional
  public WalletResponse updateWallet(Long id, WalletRequest request) {
    Wallet existing = walletRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Wallet not found: " + id));

    // Update balance only if provided; request.getBalance() is BigDecimal
    if (request.getBalance() != null) {
      BigDecimal newBalance = request.getBalance();
      if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException("balance must be >= 0");
      }
      existing.setBalance(newBalance);
    }

    // Forbid moving wallet to another user
    if (request.getUserId() != null && !request.getUserId().equals(existing.getUserEntity().getId())) {
      throw new RuntimeException("Cannot change wallet owner");
    }

    return mapper.toResponse(walletRepository.save(existing));
  }

  @Override
  @Transactional
  public void deleteWalletById(Long id) {
    walletRepository.deleteById(id);
  }
}
