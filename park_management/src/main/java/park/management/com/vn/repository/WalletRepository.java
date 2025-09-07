package park.management.com.vn.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import park.management.com.vn.entity.Wallet;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // keep your existing non-locking finder if you still need it
    Optional<Wallet> findByUserEntityId(Long userId);

    // NEW: lock the wallet row for the current user during purchase
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Wallet w where w.userEntity.id = :userId")
    Optional<Wallet> findByUserEntity_IdForUpdate(@Param("userId") Long userId);

    // optional: id-based locker (you already had something similar)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Wallet w where w.id = :id")
    Optional<Wallet> findByIdForUpdate(@Param("id") Long id);
}
