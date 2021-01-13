package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.compedia.website.auction.model.payment.other.PaymentReturnUrlResponseRef;

public interface PaymentReturnUrlResponseRefRepository extends CrudRepository<PaymentReturnUrlResponseRef, Long> {
    @Query("select pr from PaymentReturnUrlResponseRef pr where pr.token_code = :tokenCode")
    PaymentReturnUrlResponseRef getAllByTokenCode(@Param("tokenCode") String tokenCode);

    @Query("select prurf from PaymentReturnUrlResponse prur, PaymentReturnUrlResponseRef prurf where prur.payment_return_url_response_id = prurf.payment_return_url_response_id and prur.auctionRegisterId = :auctionRegisterId")
    PaymentReturnUrlResponseRef getAllByAuctionRegisterId(@Param("auctionRegisterId") Long auctionRegisterId);
}
