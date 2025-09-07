package park.management.com.vn.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import park.management.com.vn.entity.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "branch_review")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BranchReview extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity userEntity;

  @ManyToOne
  @JoinColumn(name = "branch_id")
  private ParkBranch parkBranch;

  @Column(name = "rating")
  private Integer rating;

  @Column(name = "comment")
  private String comment;

  @Column(name = "approved")
  private Boolean approved;
}
