package park.management.com.vn.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import park.management.com.vn.entity.base.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket_type")
public class TicketType extends BaseEntity {

    private String name;            // @Column(nullable = false, unique = true) moved to DDL; JPA can infer
    private String description;

    private BigDecimal basePrice;   // @Column(nullable = false) in DDL

    private Boolean isCancelable;   // default true if null (enforce in service if needed)

    private LocalTime startTime;
    private LocalTime endTime;

    // NEW: link a ticket type to a Game (so “ticket leads to game”)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    // ====== helpers to satisfy service calls (keep) ======
    @Override
    public Long getId() {
        return super.getId();
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }
}
