package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.*;
import park.management.com.vn.entity.base.BaseEntity;

@Entity
@Table(name = "branch_amenity")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BranchAmenity extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "park_branch_id", nullable = false)
  private ParkBranch parkBranch;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "amenity_type_id", nullable = false)
  private AmenityType amenityType;

  // Game replaces Product as the related content for the amenity (optional)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id")
  private Game game;

  @Column(name = "name", nullable = false, unique = true, length = 255)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(nullable = false)
  private Boolean status = true;
}
