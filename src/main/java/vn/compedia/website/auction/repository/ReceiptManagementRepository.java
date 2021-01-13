package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.payment.ReceiptManagement;

public interface ReceiptManagementRepository extends CrudRepository<ReceiptManagement, Long>, ReceiptManagementRepositoryCustom {
    @Query("select ac from Payment p, AuctionRegister ar, Account ac where p.auctionRegisterId = ar.auctionRegisterId and ar.accountId = ac.accountId and p.paymentId = :paymentId")
    Account getAllByPaymentId(@Param("paymentId") Long paymentId);

    @Query("select rm.status from ReceiptManagement rm where rm.receiptManagementId = :receiptManagementId")
    Integer findStatusByReceiptManagementId(@Param("receiptManagementId") Long receiptManagementId);

    @Query("select count (rm.receiptManagementId) from ReceiptManagement rm, Payment p where rm.paymentId = p.paymentId and p.paymentId = :paymentId")
    Integer countIdByCode(@Param("paymentId") Long paymentId);

    @Query(" select rm from ReceiptManagement rm,Payment p where rm.paymentId = p.paymentId and rm.paymentId =:paymentId")
    ReceiptManagement findReceiptManagementByPaymentId(@Param("paymentId")Long id);

    @Query(" select rm from ReceiptManagement rm,Payment p where rm.receiptManagementId =:receiptManagementId")
    ReceiptManagement findReceiptManagementByReceiptManagementId(@Param("receiptManagementId")Long id);
}
