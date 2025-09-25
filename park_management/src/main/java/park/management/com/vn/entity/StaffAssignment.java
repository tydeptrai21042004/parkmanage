package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.*;
import park.management.com.vn.entity.base.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff_assignment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StaffAssignment extends BaseEntity {

  @Column(name = "assigned_date", nullable = false)
  private LocalDate assignedDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "branch_staff_id")
  private BranchStaff staff;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "shift_id")
  private Shift shift;

  // QR scan timestamps
  @Column(name = "scan_in_at")
  private LocalDateTime scanInAt;

  @Column(name = "scan_out_at")
  private LocalDateTime scanOutAt;

  @Column(nullable = false)
  private Boolean status = true;
}
