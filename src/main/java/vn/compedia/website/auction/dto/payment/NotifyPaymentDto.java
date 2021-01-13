package vn.compedia.website.auction.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.model.payment.NotifyPayment;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotifyPaymentDto extends NotifyPayment {
    private String auctionRegisterCode;
    private String fullNameNguoiDangKy;
    private String assetName;
    private String regulationCode;
    private Integer paymentFormalityId;
    private Integer paymentStatus;
    private String paymentNote;
    private Long paymentId;
    private Date paymentStartTime;
    private Date paymentEndTime;
}
