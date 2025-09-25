package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import park.management.com.vn.entity.base.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "voucher_usage", indexes = {
    @Index(name = "idx_vu_voucher", columnList = "voucher_id"),
    @Index(name = "idx_vu_user", columnList = "user_id"),
    @Index(name = "idx_vu_guest", columnList = "guest_email")
})
@Getter @Setter
public class VoucherUsage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    // either userId or guestEmail (one of them must be present)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "guest_email", length = 128)
    private String guestEmail;

    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;

    @Column(name = "order_id", nullable = false)
    private Long orderId;
}
