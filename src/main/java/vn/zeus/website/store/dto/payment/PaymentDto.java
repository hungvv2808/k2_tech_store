package vn.zeus.website.store.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.zeus.website.store.model.payment.Payment;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto extends Payment {
    @JsonProperty("serial_number")
    private int serialNumber;
    private String mail;
    private String fullName;
    private String name;
    private String address;
    private String moneyVietWords;
    private String time;
    @JsonProperty("regulation_id")
    private Long regulationId;
    @JsonProperty("regulation_code")
    private String regulationCode;
    @JsonProperty("asset_id")
    private Long assetId;
    @JsonProperty("asset_name")
    private String assetName;
    private String fullNameNguoiDangKy;

    // receipt management
    private Long receiptManagementId;
    private String receiptCode;
    private String receiptPayerFullname;
    private String receiptPayerAddress;
    private Long receiptAmount;
    private String receiptContentPayment;
    private boolean receiptStatus;
}