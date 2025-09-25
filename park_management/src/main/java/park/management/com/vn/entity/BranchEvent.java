package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.*;
import park.management.com.vn.entity.base.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "branch_event")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BranchEvent extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "park_branch_id", nullable = false)
  private ParkBranch parkBranch;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "text")
  private String content;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(name = "start_time", nullable = false)
  private LocalDateTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalDateTime endTime;

  @Column(nullable = false)
  private Boolean status = true;
}
