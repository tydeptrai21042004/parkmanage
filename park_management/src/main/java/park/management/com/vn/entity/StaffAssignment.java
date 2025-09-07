package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.*;
import park.management.com.vn.entity.base.BaseEntity;

import java.time.LocalDate;
import lombok.*;
import jakarta.persistence.*;


@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "staff_assignment")
@lombok.Data
public class StaffAssignment extends BaseEntity {

    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private BranchStaff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    private Shift shift;
}
