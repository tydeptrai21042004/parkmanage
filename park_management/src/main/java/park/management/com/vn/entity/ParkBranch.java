package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.*;
import park.management.com.vn.entity.base.BaseEntity;

import java.time.LocalTime;

@Entity
@Table(name = "park_branch")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParkBranch extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String name;

  @Column(name = "address")
  private String address;

  @Column(name = "location")
  private String location;

  @Column(name = "open_time", nullable = false)
  private LocalTime openTime;

  @Column(name = "close_time", nullable = false)
  private LocalTime closeTime;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(nullable = false)
  private Boolean status = true;
}
