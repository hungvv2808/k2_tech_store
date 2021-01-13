package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.compedia.website.auction.model.payment.other.PaymentReturnUrlResponse;

public interface PaymentReturnUrlResponseRepository extends CrudRepository<PaymentReturnUrlResponse, Long> {
    @Query("select count (prur.payment_return_url_response_id) from PaymentReturnUrlResponse prur where prur.paymentReturnUrlRequestId = :paymentReturnUrlRequestId")
    Integer countAuctionRegisterResponse(@Param("paymentReturnUrlRequestId") Long paymentReturnUrlRequestId);

    @Query("select prur.status from PaymentReturnUrlResponse prur where prur.auctionRegisterId = :auctionRegisterId")
    Integer getStatusResponse(@Param("auctionRegisterId") Long auctionRegisterId);
}
