package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.compedia.website.auction.model.payment.Payment;
import vn.compedia.website.auction.util.DbConstant;

import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, Long>, PaymentRepositoryCustom {
    Payment findByAuctionRegisterId(Long auctionRegisterId);

    Payment findPaymentByPaymentId(Long paymentId);

    boolean existsPaymentByAuctionRegisterIdAndStatus(Long auctionRegisterId, Integer status);

    List<Payment> findPaymentsByAuctionRegisterId(Long auctionRegisterId);

    @Query(" select p from Payment p, ReceiptManagement rm where p.paymentId = rm.paymentId and p.paymentId =:paymentId")
    Payment findPaymentPaymentId(@Param("paymentId") Long id);

    @Query("select count (p.auctionRegisterId) from Payment p where p.auctionRegisterId = :auctionRegisterId")
    Integer countAuctionRegisterId(@Param("auctionRegisterId") Long auctionRegisterId);

    @Query("select count (p.auctionRegisterId) from Payment p where p.status = " + DbConstant.PAYMENT_STATUS_PAID + " and p.auctionRegisterId = :auctionRegisterId")
    Integer countAuctionRegisterIdSuccess(@Param("auctionRegisterId") Long auctionRegisterId);

    @Query("select p.sendBillStatus from Payment p where p.paymentId = :paymentId")
    Integer findStatusByPaymentId(@Param("paymentId") Long paymentId);

    @Query("select p from Payment p where p.status = " + DbConstant.PAYMENT_STATUS_PAID + " or p.status = " + DbConstant.PAYMENT_STATUS_REFUND + " order by p.paymentId desc")
    List<Payment> findAllPaymentStatus();

    @Query("select sum(pm.money) from Payment pm where pm.paymentFormality = :paymentFormality")
    Long getTotalMoney(@Param("paymentFormality") Integer paymentFormality);

    @Query("select p from Payment p where p.auctionRegisterId =:auctionRegisterId and p.status = :status")
    Payment getPaymentByAuctionRegisterIdAndStatus(@Param("auctionRegisterId")Long auctionRegisterId,@Param("status")Integer status);
}
