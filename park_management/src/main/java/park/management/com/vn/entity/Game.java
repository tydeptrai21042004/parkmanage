package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import park.management.com.vn.entity.base.BaseEntity;

@Entity
@Table(name = "game")
@Getter @Setter
public class Game extends BaseEntity {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "branch_id", nullable = false)
  private ParkBranch parkBranch;

  @Column(nullable = false, length = 128)
  private String name;

  @Column(length = 2000)
  private String description;

  @Column(length = 256)
  private String location; // optional in-park location
}
