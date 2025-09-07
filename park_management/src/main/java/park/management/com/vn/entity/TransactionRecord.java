package park.management.com.vn.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import park.management.com.vn.entity.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "transaction_record")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@lombok.Data
public class TransactionRecord extends BaseEntity {

    @Column(name = "amount")
    private double amount;

    @Column(name = "type")
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    private Wallet wallet;
}
