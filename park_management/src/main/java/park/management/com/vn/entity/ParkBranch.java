package park.management.com.vn.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import park.management.com.vn.entity.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "park_branch")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParkBranch extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String name;
  @Column(name = "address")
  private String address;
  @Column(name = "location")
  private String location;

  private LocalDateTime open;
  private LocalDateTime close;
}
