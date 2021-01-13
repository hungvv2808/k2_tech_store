package vn.compedia.website.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.system.listener.RegulationListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "regulation")
@EntityListeners(RegulationListener.class)
public class  Regulation extends BaseModel {
    private static final long serialVersionUID = 6381841365641781534L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "regulation_id")
    private Long regulationId;

    @Column(name = "auction_req_id")
    private Long auctionReqId;

    @Column(name = "auction_formality_id")
    private Long auctionFormalityId;

    @Column(name = "code")
    private String code;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "number_of_rounds")
    private Integer numberOfRounds;

    @Column(name = "time_per_round")
    private Integer timePerRound;

    @Column(name = "start_registration_date")
    private Date startRegistrationDate;

    @Column(name = "end_registration_date")
    private Date endRegistrationDate;

    @Column(name = "auction_method_id")
    private Long auctionMethodId;

    @Column(name = "history_status")
    private boolean historyStatus;

    @Column(name = "status")
    private int status;

    @Column(name = "reason_cancel")
    private String reasonCancel;

    @Column(name = "auctioneer_id")
    private Long auctioneerId;

    @Column(name = "real_start_time")
    private Date realStartTime;

    @Column(name = "real_end_time")
    private Date realEndTime;

    @Column(name = "auction_status")
    private Integer auctionStatus;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "payment_start_time")
    private Date paymentStartTime;

    @Column(name = "payment_end_time")
    private Date paymentEndTime;

    @Column(name = "retract_price")
    private boolean retractPrice;

    public Regulation(Date createDate, Date updateDate, Long createBy, Long updateBy, Long regulationId, Long auctionReqId, Long auctionFormalityId, String code, Date startTime, Integer numberOfRounds, Integer timePerRound, Date startRegistrationDate, Date endRegistrationDate, Long auctionMethodId, boolean historyStatus, int status, String reasonCancel, Long auctioneerId, Date realStartTime, Date realEndTime, Integer auctionStatus, Date endTime, Date paymentStartTime, Date paymentEndTime, boolean retractPrice) {
        super(createDate, updateDate, createBy, updateBy);
        this.regulationId = regulationId;
        this.auctionReqId = auctionReqId;
        this.auctionFormalityId = auctionFormalityId;
        this.code = code;
        this.startTime = startTime;
        this.numberOfRounds = numberOfRounds;
        this.timePerRound = timePerRound;
        this.startRegistrationDate = startRegistrationDate;
        this.endRegistrationDate = endRegistrationDate;
        this.auctionMethodId = auctionMethodId;
        this.historyStatus = historyStatus;
        this.status = status;
        this.reasonCancel = reasonCancel;
        this.auctioneerId = auctioneerId;
        this.realStartTime = realStartTime;
        this.realEndTime = realEndTime;
        this.auctionStatus = auctionStatus;
        this.endTime = endTime;
        this.paymentStartTime = paymentStartTime;
        this.paymentEndTime = paymentEndTime;
        this.retractPrice = retractPrice;
    }

    public Regulation(Long regulationId, String code, Long auctioneerId, Long auctionFormalityId, Long auctionMethodId, Date startTime, Date realEndTime) {
        this.regulationId = regulationId;
        this.code = code;
        this.auctioneerId = auctioneerId;
        this.auctionFormalityId = auctionFormalityId;
        this.auctionMethodId = auctionMethodId;
        this.startTime = startTime;
        this.realEndTime = realEndTime;
    }

    public Regulation(Long regulationId, String code, Long auctioneerId, Long auctionFormalityId, Long auctionMethodId, Date startTime, Integer numberOfRounds, Integer timePerRound, Date realEndTime, Integer auctionStatus) {
        this.regulationId = regulationId;
        this.code = code;
        this.auctioneerId = auctioneerId;
        this.auctionFormalityId = auctionFormalityId;
        this.auctionMethodId = auctionMethodId;
        this.startTime = startTime;
        this.numberOfRounds = numberOfRounds;
        this.timePerRound = timePerRound;
        this.realEndTime = realEndTime;
        this.auctionStatus = auctionStatus;
    }

    public Regulation(Long regulationId, String code, Date startTime, Long auctionFormalityId, Long auctionMethodId) {
        this.regulationId = regulationId;
        this.code = code;
        this.startTime = startTime;
        this.auctionFormalityId = auctionFormalityId;
        this.auctionMethodId = auctionMethodId;
    }
}
