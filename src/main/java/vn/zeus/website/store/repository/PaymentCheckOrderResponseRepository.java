package vn.zeus.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.zeus.website.store.model.payment.other.PaymentCheckOrderResponse;

public interface PaymentCheckOrderResponseRepository extends CrudRepository<PaymentCheckOrderResponse, Long> {
}
