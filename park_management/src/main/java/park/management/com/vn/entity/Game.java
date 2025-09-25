package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.*;
import park.management.com.vn.entity.base.BaseEntity;

@Entity
@Table(name = "game")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Game extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "park_branch_id", nullable = false)
  private ParkBranch parkBranch;

  @Column(nullable = false, length = 128)
  private String name;

  @Column(length = 2000)
  private String description;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(nullable = false)
  private Boolean status = true;
}
