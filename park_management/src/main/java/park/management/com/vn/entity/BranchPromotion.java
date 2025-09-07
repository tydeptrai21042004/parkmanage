package park.management.com.vn.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import park.management.com.vn.constant.DiscountType;
import park.management.com.vn.entity.base.BaseEntity;

@Entity
@Table(name = "branch_promotion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchPromotion extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "branch_id")
  private ParkBranch parkBranch;

  @Column(name = "description")
  private String description;

  @Column(name = "discount_value")
  private BigDecimal discountValue;

  @Enumerated(EnumType.STRING)
  @Column(name = "discount_type")
  private DiscountType discountType;

  @Column(name = "valid_from", nullable = false) // e.g. 2025-05-20T00:00
  private LocalDateTime validFrom;

  @Column(name = "valid_until", nullable = false) // e.g. 2025-05-26T23:59
  private LocalDateTime validUntil;

  @Column(name = "is_active")
  private Boolean isActive;






}
