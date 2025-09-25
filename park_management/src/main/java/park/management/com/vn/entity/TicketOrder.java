package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.*;
import park.management.com.vn.constant.TicketStatus;
import park.management.com.vn.entity.base.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ticket_order")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketOrder extends BaseEntity {

    // ===== CHANGED: make user nullable to support guests =====
    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private UserEntity userEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status; // REQUESTED, PENDING, PAID, CANCELLED, REFUNDED

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private BigDecimal finalAmount;

    private String paymentMethod;

    private LocalDateTime paymentTime;

    @Column(nullable = false)
    private LocalDate ticketDate;

    @ManyToOne
    @JoinColumn(name = "park_branch_id", nullable = false)
    private ParkBranch parkBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private BranchPromotion promotion;

    @OneToMany(mappedBy = "ticketOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketDetail> details;

    // ===== NEW: guest info =====
    private String customerName;
    private Integer customerAge;
    private String customerEmail;
    private String customerPhone;
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "voucher_id")
private Voucher voucher;

@Column(name = "discount_amount", precision = 12, scale = 0)
private java.math.BigDecimal discountAmount;
    // (Optional later) voucher snapshot fields:
    // private String voucherCode;
    // private BigDecimal discountAmount;
}
