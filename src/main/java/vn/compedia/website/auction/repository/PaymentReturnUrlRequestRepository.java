package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.compedia.website.auction.model.payment.other.PaymentReturnUrlRequest;

public interface PaymentReturnUrlRequestRepository extends CrudRepository<PaymentReturnUrlRequest,Long> {
    @Query("select count (prur.payment_return_url_request_id) from PaymentReturnUrlRequest prur where prur.order_code = :orderCode")
    Integer countRequest(@Param("orderCode") String orderCode);
}
