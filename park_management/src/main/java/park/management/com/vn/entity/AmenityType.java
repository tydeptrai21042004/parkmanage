package park.management.com.vn.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import park.management.com.vn.entity.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "amenity_type")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AmenityType extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
