package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.*;
import park.management.com.vn.entity.base.BaseEntity;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ticket_type")
public class TicketType extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 18, scale = 0)
    private BigDecimal basePrice;

    // CHÍNH: TicketType thuộc ParkBranch (không còn thuộc Game)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "park_branch_id", nullable = false)
    private ParkBranch parkBranch;

    // soft-delete/enable
    @Column(nullable = false)
    private Boolean status = true;
}
