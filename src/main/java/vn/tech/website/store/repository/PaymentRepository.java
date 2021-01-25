package vn.tech.website.store.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.tech.website.store.model.payment.Payment;
import vn.tech.website.store.util.DbConstant;

import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, Long>, PaymentRepositoryCustom {
    Payment findByStoreRegisterId(Long storeRegisterId);

    Payment findPaymentByPaymentId(Long paymentId);

    boolean existsPaymentByStoreRegisterIdAndStatus(Long storeRegisterId, Integer status);

    List<Payment> findPaymentsByStoreRegisterId(Long storeRegisterId);

    @Query(" select p from Payment p, ReceiptManagement rm where p.paymentId = rm.paymentId and p.paymentId =:paymentId")
    Payment findPaymentPaymentId(@Param("paymentId") Long id);

    @Query("select count (p.storeRegisterId) from Payment p where p.storeRegisterId = :storeRegisterId")
    Integer countStoreRegisterId(@Param("storeRegisterId") Long storeRegisterId);

    @Query("select count (p.storeRegisterId) from Payment p where p.status = " + DbConstant.PAYMENT_STATUS_PAID + " and p.storeRegisterId = :storeRegisterId")
    Integer countStoreRegisterIdSuccess(@Param("storeRegisterId") Long storeRegisterId);

    @Query("select p.sendBillStatus from Payment p where p.paymentId = :paymentId")
    Integer findStatusByPaymentId(@Param("paymentId") Long paymentId);

    @Query("select p from Payment p where p.status = " + DbConstant.PAYMENT_STATUS_PAID + " or p.status = " + DbConstant.PAYMENT_STATUS_REFUND + " order by p.paymentId desc")
    List<Payment> findAllPaymentStatus();

    @Query("select sum(pm.money) from Payment pm where pm.paymentFormality = :paymentFormality")
    Long getTotalMoney(@Param("paymentFormality") Integer paymentFormality);

    @Query("select p from Payment p where p.storeRegisterId =:storeRegisterId and p.status = :status")
    Payment getPaymentByStoreRegisterIdAndStatus(@Param("storeRegisterId")Long storeRegisterId,@Param("status")Integer status);
}
