package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.*;
import park.management.com.vn.entity.base.BaseEntity;

import java.math.BigDecimal;

@Entity
@Table(
    name = "wallet",
    uniqueConstraints = @UniqueConstraint(name = "uk_wallet_user", columnNames = "user_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet extends BaseEntity {

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity userEntity;
}
