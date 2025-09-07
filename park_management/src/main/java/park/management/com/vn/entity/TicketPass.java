package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

import park.management.com.vn.entity.base.BaseEntity;     // ✅ correct package
// (optional but clear)
import park.management.com.vn.entity.ParkBranch;
import park.management.com.vn.entity.TicketDetail;
import park.management.com.vn.entity.TicketOrder;

@Entity
@Table(
    name = "ticket_pass",
    uniqueConstraints = @UniqueConstraint(name = "uk_pass_code", columnNames = "code")
)
@Getter @Setter
public class TicketPass extends BaseEntity {              // ✅ now resolves

  public enum Status { ACTIVE, REDEEMED, CANCELLED, EXPIRED }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 64, unique = true)
  private String code;    // opaque base62 token

  @ManyToOne(fetch = FetchType.LAZY) 
  @JoinColumn(name = "order_id", nullable = false)
  private TicketOrder order;

  @ManyToOne(fetch = FetchType.LAZY) 
  @JoinColumn(name = "detail_id")
  private TicketDetail detail;

  @ManyToOne(fetch = FetchType.LAZY) 
  @JoinColumn(name = "branch_id", nullable = false)
  private ParkBranch branch;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private Status status = Status.ACTIVE;

  @Column(name = "ticket_date", nullable = false)
  private LocalDate ticketDate;

  @Version
  private Integer version;

  private Instant redeemedAt;
  private Long redeemedByUserId;
}
