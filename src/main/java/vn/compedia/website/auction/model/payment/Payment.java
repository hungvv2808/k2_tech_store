package vn.compedia.website.auction.model.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.util.Constant;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "payment")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Payment {
    private static final long serialVersionUID = -313470329227921430L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    @JsonProperty("payment_id")
    private Long paymentId;

    @Column(name = "auction_register_id")
    @JsonIgnore
    private Long auctionRegisterId;

    @Column(name = "money")
    private Long money;

    @Column(name = "payment_formality")
    @JsonProperty("payment_formality")
    private Integer paymentFormality;

    @Column(name = "note")
    private String note;

    @Column(name = "file_path")
    @JsonProperty("file_path")
    private String filePath;

    @Column(name = "file_name")
    @JsonProperty("file_name")
    private String fileName;

    @Column(name = "send_bill_status")
    @JsonProperty("send_bill_status")
    private boolean sendBillStatus;

    @Column(name = "receipt_file_path")
    private String receiptFilePath;

    @Column(name = "status")
    private Integer status;

    @Column(name = "code")
    private String code;

    @Column(name = "create_date")
    @JsonProperty("create_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss dd/MM/yyyy", timezone = Constant.TIME_ZONE_DEFAULT)
    protected Date createDate;

    @Column(name = "update_date")
    @JsonIgnore
    protected Date updateDate;

    @Column(name = "create_by")
    @JsonIgnore
    protected Long createBy;

    @Column(name = "update_by")
    @JsonIgnore
    protected Long updateBy;

    public Payment(Long paymentId, String code) {
        this.paymentId = paymentId;
        this.code = code;
    }
}

