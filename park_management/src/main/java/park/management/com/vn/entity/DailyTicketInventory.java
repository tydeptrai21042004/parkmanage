package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import park.management.com.vn.entity.base.BaseEntity;

import java.time.LocalDate;
import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "daily_ticket_inventory")
@Getter
@Setter
@lombok.Data
public class DailyTicketInventory extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "ticket_type_id")
    private TicketType ticketType;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer totalAvailable;

    @Column(nullable = false)
    private Integer sold;

    // ====== helper to satisfy service calls ======
    public Integer getTotalAvailable() {
        return totalAvailable;
    }
}
