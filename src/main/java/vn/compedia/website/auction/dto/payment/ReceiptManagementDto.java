package vn.compedia.website.auction.dto.payment;

import lombok.Getter;
import lombok.Setter;
import vn.compedia.website.auction.model.payment.ReceiptManagement;

import java.util.Date;

@Getter
@Setter

public class ReceiptManagementDto extends ReceiptManagement {
    private boolean sendBillStatus;
    private String mail;
    private String time;
    private String moneyVietWords;
    private String paymentCode;
    private String provinceName;
    private String districtName;
    private String communeName;
    private Integer paymentStatus;
    private String titleBill;
    private String assetName;
    private String regulationCode;
    private String username;

    public ReceiptManagementDto() {
    }
}
