package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.payment.other.PaymentCheckOrderResponse;

public interface PaymentCheckOrderResponseRepository extends CrudRepository<PaymentCheckOrderResponse, Long> {
}
