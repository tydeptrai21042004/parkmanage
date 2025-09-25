package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.*;
import park.management.com.vn.constant.DiscountType;
import park.management.com.vn.entity.base.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "branch_promotion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchPromotion extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "park_branch_id")
  private ParkBranch parkBranch;

  @Column(name = "description")
  private String description;

  @Column(name = "discount_value")
  private BigDecimal discountValue;

  @Enumerated(EnumType.STRING)
  @Column(name = "discount_type")
  private DiscountType discountType;

  @Column(name = "valid_from", nullable = false)
  private LocalDateTime validFrom;

  @Column(name = "valid_until", nullable = false)
  private LocalDateTime validUntil;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(nullable = false)
  private Boolean status = true;
}
