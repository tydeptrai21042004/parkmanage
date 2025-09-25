package park.management.com.vn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import park.management.com.vn.entity.base.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "voucher", indexes = {
    @Index(name = "ux_voucher_code", columnList = "code", unique = true)
})
@Getter @Setter
public class Voucher extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true, length = 64)
    private String code;

    // 0.0 .. 0.5  (i.e., 0%..50%)
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("0.5")
    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal percent; // e.g. 0.10 means 10%

    // ≤ 500,000
    @NotNull
    @Column(nullable = false, precision = 12, scale = 0)
    private BigDecimal maxDiscount; // VND

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startAt;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime endAt;

    @NotNull
    @Column(nullable = false)
    private Boolean active = true;
}
