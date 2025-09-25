package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.*;
import park.management.com.vn.entity.base.BaseEntity;

import java.time.LocalTime;

@Entity
@Table(name = "shift")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shift extends BaseEntity {

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime = LocalTime.of(10, 0); // default 10:00

  @Column(name = "end_time", nullable = false)
  private LocalTime endTime = LocalTime.of(22, 0);   // default 22:00

  // CSV like "MON,TUE,WED", or adapt to your enum/list later
  @Column(name = "days_of_week", length = 50)
  private String daysOfWeek;

  @Column(name = "description")
  private String description;
}
