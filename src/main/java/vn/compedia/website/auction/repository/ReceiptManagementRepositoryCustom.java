package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.payment.PaymentDto;
import vn.compedia.website.auction.dto.payment.ReceiptManagementDto;
import vn.compedia.website.auction.dto.payment.ReceiptManagementSearchDto;

import java.util.List;

public interface ReceiptManagementRepositoryCustom {
    List<ReceiptManagementDto> search(ReceiptManagementSearchDto receiptManagementSearchDto);
    Long countSearch(ReceiptManagementSearchDto receiptManagementSearchDto);
    List<PaymentDto> searchPaymentReceipt();
}
