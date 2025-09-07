package park.management.com.vn.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.*;
import park.management.com.vn.entity.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "ticket_detail")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDetail extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "ticket_order_id", nullable = false)
    private TicketOrder ticketOrder;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketType ticketType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    private Integer discountPercent;

    @Column(nullable = false)
    private BigDecimal finalPrice; // (unit * quantity) - discount
}