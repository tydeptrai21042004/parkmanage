package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.*;
import park.management.com.vn.entity.base.BaseEntity;

@Entity
@Table(name = "bulk_pricing_rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkPricingRule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_type_id")
    private TicketType ticketType;

    @Column(name = "min_quantity", nullable = false)
    private Integer minQuantity;

    /**
     * Percentage 0..100 (stored as Integer).
     * Wherever you need BigDecimal, convert explicitly with BigDecimal.valueOf(discountPercent).
     */
    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;
}
