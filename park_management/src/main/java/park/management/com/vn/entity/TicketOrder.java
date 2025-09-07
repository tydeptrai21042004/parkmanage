package park.management.com.vn.entity;

import jakarta.persistence.*;

import lombok.*;

import park.management.com.vn.constant.TicketStatus;
import park.management.com.vn.entity.base.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "ticket_order")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketOrder extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status; // REQUESTED, PAID, CANCELLED, REFUNDED

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

}

