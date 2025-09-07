package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import park.management.com.vn.entity.base.BaseEntity;

@Entity
@Table(name = "game_review")
@Getter @Setter
public class GameReview extends BaseEntity {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id", nullable = false)
  private Game game;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(nullable = false)
  private Integer rating; // 1..5

  @Column(length = 1000)
  private String comment;

  @Column(nullable = false)
  private Boolean approved = false;
}
