package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.payment.PaymentDto;
import vn.compedia.website.auction.dto.payment.PaymentSearchDto;
import vn.compedia.website.auction.model.payment.Payment;

import java.util.List;

public interface PaymentRepositoryCustom  {
    List<PaymentDto> search(PaymentSearchDto paymentSearchDto);
    Long countSearch(PaymentSearchDto paymentSearchDto);
}
