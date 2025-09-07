package park.management.com.vn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import park.management.com.vn.entity.WalletTopup;

import java.util.Optional;

public interface WalletTopupRepository extends JpaRepository<WalletTopup, Long> {
  Optional<WalletTopup> findByPaymentLinkId(String paymentLinkId);
  Optional<WalletTopup> findByOrderCode(Long orderCode);
}
