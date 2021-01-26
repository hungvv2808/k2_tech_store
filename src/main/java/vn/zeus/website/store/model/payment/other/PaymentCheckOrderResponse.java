package vn.zeus.website.store.model.payment.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.zeus.website.store.model.BaseModel;

import javax.persistence.*;

@Entity
@Table(name = "payment_check_order_response")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentCheckOrderResponse extends BaseModel {
    private static final long serialVersionUID = 1098733298905837574L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_check_order_response_id")
    private Long paymentCheckOrderResponseId;

    @Column(name = "result_code")
    private String result_code;

    @Column(name = "result_message")
    private String result_message;

    @Column(name = "payment_return_url_request_id")
    private Long paymentReturnUrlRequestId;
}
