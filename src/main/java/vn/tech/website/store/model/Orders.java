package vn.tech.website.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Orders extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Integer ordersId;

    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "code")
    private String code;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "note")
    private String note;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "shipping")
    private Double shipping;
}
