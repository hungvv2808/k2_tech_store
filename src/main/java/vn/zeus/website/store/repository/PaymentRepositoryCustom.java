package vn.zeus.website.store.repository;

import vn.zeus.website.store.dto.payment.PaymentDto;
import vn.zeus.website.store.dto.payment.PaymentSearchDto;

import java.util.List;

public interface PaymentRepositoryCustom  {
    List<PaymentDto> search(PaymentSearchDto paymentSearchDto);
    Long countSearch(PaymentSearchDto paymentSearchDto);
}