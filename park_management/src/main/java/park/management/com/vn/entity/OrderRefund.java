package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import park.management.com.vn.entity.base.BaseEntity;

import java.time.LocalDateTime;
import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "order_refund")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRefund extends BaseEntity {

    @Column(name = "reason")
    private String reason;
    @Column(name = "refund_date", nullable = false)
    private LocalDateTime refundDate;

    @OneToOne
    @JoinColumn(name = "ticket_order_id")
    private TicketDetail ticketOrder;

}
