package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.payment.NotifyPaymentDto;
import vn.compedia.website.auction.dto.payment.NotifyPaymentSearchDto;

import java.util.List;

public interface NotifyPaymentRepositoryCustom {
    List<NotifyPaymentDto> search(NotifyPaymentSearchDto searchDto);
    Long countSearch(NotifyPaymentSearchDto searchDto);
}
