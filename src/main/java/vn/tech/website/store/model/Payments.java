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
@Table(name = "payments")
public class Payments extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "orders_id")
    private Integer ordersId;

    @Column(name = "code")
    private String code;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "type")
    private Integer type;

    @Column(name = "status")
    private Integer status;
}
