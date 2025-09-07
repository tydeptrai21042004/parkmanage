package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.*;
import park.management.com.vn.constant.TopupStatus;
import park.management.com.vn.entity.base.BaseEntity;

import java.math.BigDecimal;
import lombok.*;
import jakarta.persistence.*;



@Entity
@Table(name = "wallet_topup",
       uniqueConstraints = {
         @UniqueConstraint(name = "uk_wallet_topup_link", columnNames = "payment_link_id")
       })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class WalletTopup extends BaseEntity {

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", nullable = false)
  private Wallet wallet;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  // numeric order code sent to payOS (we'll use this record's id after save)
  @Column(name = "order_code", nullable = false)
  private Long orderCode;

  // returned by payOS after createPaymentLink (unique)
  @Column(name = "payment_link_id", length = 64)
  private String paymentLinkId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private TopupStatus status = TopupStatus.PENDING;
}
