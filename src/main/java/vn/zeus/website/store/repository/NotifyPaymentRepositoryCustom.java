package vn.zeus.website.store.repository;

import vn.zeus.website.store.dto.payment.NotifyPaymentDto;
import vn.zeus.website.store.dto.payment.NotifyPaymentSearchDto;

import java.util.List;

public interface NotifyPaymentRepositoryCustom {
    List<NotifyPaymentDto> search(NotifyPaymentSearchDto searchDto);
    Long countSearch(NotifyPaymentSearchDto searchDto);
}
