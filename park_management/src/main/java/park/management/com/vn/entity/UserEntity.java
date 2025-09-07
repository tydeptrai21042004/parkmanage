package park.management.com.vn.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import park.management.com.vn.entity.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;


@lombok.Data
@Entity
@Table(name = "user_entity")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @ManyToOne(fetch = FetchType.LAZY)
  private ParkBranch parkBranch;

  @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
  private List<UserRole> userRoles;

  @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL)
  private Wallet wallet;
public String getUsername() { return username != null ? username : email; }

}
