package vn.compedia.website.auction.dto.regulation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.model.AuctionFormality;
import vn.compedia.website.auction.model.AuctionMethod;
import vn.compedia.website.auction.model.Regulation;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegulationDto extends Regulation {

    private String auctionFormalityName;
    private String auctionMethodName;
    private Long assetId;
    private String filePathRegulation;
    private String fileNameRegulation;
    private String nameAccount;
    private List<AssetDto> assetDtoList;
    private AuctionFormality auctionFormality;
    private AuctionMethod auctionMethod;
    private String nameAsset;
    private boolean type;
    private String orgName;
    private boolean org;
    private String nameOfUser;
    private Integer typeFile;
    private Integer statusRefund;

    private Integer auctionStatus;
    private Integer statusDeposit;
    private boolean statusRefuseWin;
    private boolean statusJoined;
    private boolean statusEnding;
    private Boolean statusRetract;
    private Integer regulationAuctionStatus;

    private Integer signature;

    //Biên bản đấu giá
    private String filePathReport;
    private String fileNameReport;
    private Long regulationReportFileId;
    private Integer regulationReportFileStatus;
    private Integer assetStatus;
    // asset cancelPlaying
    private boolean assetCancelPlaying;

    private Long auctionId;
    private Long winnerId;
    private String filePathDeposit;
    private Integer winnerNth;

    public RegulationDto(Date createDate, Date updateDate, Long createBy, Long updateBy,
                         Long regulationId, Long auctionReqId, Long auctionFormalityId,
                         String code, Date startTime, Integer numberOfRounds, Integer timePerRound,
                         Date startRegistrationDate, Date endRegistrationDate, Long auctionMethodId,
                         boolean historyStatus, int status, String reasonCancel, Long auctioneerId,
                         Date realStartTime, Date realEndTime, Integer auctionStatus,
                         Date endTime, Date paymentStartTime, Date paymentEndTime, boolean retractPrice, String auctionFormalityName, String auctionMethodName, Long assetId,
                         String filePathRegulation, String nameAccount, List<AssetDto> assetDtoList) {
        super(createDate, updateDate, createBy, updateBy, regulationId, auctionReqId, auctionFormalityId, code, startTime, numberOfRounds, timePerRound, startRegistrationDate, endRegistrationDate, auctionMethodId, historyStatus, status, reasonCancel, auctioneerId, realStartTime, realEndTime, auctionStatus, endTime, paymentStartTime, paymentEndTime, retractPrice);
        this.auctionFormalityName = auctionFormalityName;
        this.auctionMethodName = auctionMethodName;
        this.assetId = assetId;
        this.filePathRegulation = filePathRegulation;
        this.nameAccount = nameAccount;
        this.assetDtoList = assetDtoList;
    }

    public RegulationDto(Date createDate, Date updateDate, Long createBy, Long updateBy, Long regulationId, Long auctionReqId, Long auctionFormalityId, String code, Date startTime, Integer numberOfRounds, Integer timePerRound, Date startRegistrationDate, Date endRegistrationDate, Long auctionMethodId, boolean historyStatus, int status, String reasonCancel, Long auctioneerId, Date realStartTime, Date realEndTime, Integer auctionStatus, Date endTime, Date paymentStartTime, Date paymentEndTime, boolean retractPrice, String auctionFormalityName, String auctionMethodName, Long assetId, String filePathRegulation, String nameAccount, List<AssetDto> assetDtoList, String nameAsset) {
        super(createDate, updateDate, createBy, updateBy, regulationId, auctionReqId, auctionFormalityId, code, startTime, numberOfRounds, timePerRound, startRegistrationDate, endRegistrationDate, auctionMethodId, historyStatus, status, reasonCancel, auctioneerId, realStartTime, realEndTime, auctionStatus, endTime, paymentStartTime, paymentEndTime, retractPrice);
        this.auctionFormalityName = auctionFormalityName;
        this.auctionMethodName = auctionMethodName;
        this.assetId = assetId;
        this.filePathRegulation = filePathRegulation;
        this.nameAccount = nameAccount;
        this.assetDtoList = assetDtoList;
        this.nameAsset = nameAsset;
    }
    public RegulationDto(Date createDate, Date updateDate, Long createBy, Long updateBy, Long regulationId, Long auctionReqId, Long auctionFormalityId, String code, Date startTime, Integer numberOfRounds, Integer timePerRound, Date startRegistrationDate, Date endRegistrationDate, Long auctionMethodId, boolean historyStatus, int status, String reasonCancel, Long auctioneerId, Date realStartTime, Date realEndTime, Integer auctionStatus, Date endTime, Date paymentStartTime, Date paymentEndTime, boolean retractPrice, String filePathRegulation) {
        super(createDate, updateDate, createBy, updateBy, regulationId, auctionReqId, auctionFormalityId, code, startTime, numberOfRounds, timePerRound, startRegistrationDate, endRegistrationDate, auctionMethodId, historyStatus, status, reasonCancel, auctioneerId, realStartTime, realEndTime, auctionStatus, endTime, paymentStartTime, paymentEndTime, retractPrice);
        this.filePathRegulation = filePathRegulation;
    }

    public RegulationDto(Date createDate, Date updateDate, Long createBy, Long updateBy, Long regulationId, Long auctionReqId, Long auctionFormalityId, String code, Date startTime, Integer numberOfRounds, Integer timePerRound, Date startRegistrationDate, Date endRegistrationDate, Long auctionMethodId, boolean historyStatus, int status, String reasonCancel, Long auctioneerId, Date realStartTime, Date realEndTime, Integer auctionStatus, Date endTime, Date paymentStartTime, Date paymentEndTime, boolean retractPrice) {
        super(createDate, updateDate, createBy, updateBy, regulationId, auctionReqId, auctionFormalityId, code, startTime, numberOfRounds, timePerRound, startRegistrationDate, endRegistrationDate, auctionMethodId, historyStatus, status, reasonCancel, auctioneerId, realStartTime, realEndTime, auctionStatus, endTime, paymentStartTime, paymentEndTime, retractPrice );
    }

    public RegulationDto(Date createDate, Date updateDate, Long createBy, Long updateBy, Long regulationId, Long auctionReqId, Long auctionFormalityId, String code, Date startTime, Integer numberOfRounds, Integer timePerRound, Date startRegistrationDate, Date endRegistrationDate, Long auctionMethodId, boolean historyStatus, int status, String reasonCancel, Long auctioneerId, Date realStartTime, Date realEndTime, Integer auctionStatus, Date endTime, Date paymentStartTime, Date paymentEndTime, boolean retractPrice, Long assetId) {
        super(createDate, updateDate, createBy, updateBy, regulationId, auctionReqId, auctionFormalityId, code, startTime, numberOfRounds, timePerRound, startRegistrationDate, endRegistrationDate, auctionMethodId, historyStatus, status, reasonCancel, auctioneerId, realStartTime, realEndTime, auctionStatus, endTime, paymentStartTime, paymentEndTime, retractPrice);
        this.assetId = assetId;
    }

    public RegulationDto(Long regulationId, String code, Long auctioneerId, Long auctionFormalityId, Long auctionMethodId, Date startTime, Integer numberOfRounds, Integer timePerRound, Date realEndTime, Integer auctionStatus, String auctionFormalityName, String auctionMethodName, String nameAccount) {
        super(regulationId, code, auctioneerId, auctionFormalityId, auctionMethodId, startTime, numberOfRounds, timePerRound, realEndTime, auctionStatus);
        this.auctionFormalityName = auctionFormalityName;
        this.auctionMethodName = auctionMethodName;
        this.nameAccount = nameAccount;
    }

    public RegulationDto(Long regulationId, String code,  Date startTime, boolean type, Long auctionFormalityId, String auctionFormalityName, Long auctionMethodId, String auctionMethodName) {
        super(regulationId, code, startTime, auctionFormalityId, auctionMethodId);
        this.type = type;
        this.auctionFormalityName = auctionFormalityName;
        this.auctionMethodName = auctionMethodName;
    }

    // property sale history impl
    public RegulationDto(Long regulationId, String code, Date startTime, boolean type, Long auctionFormalityId, String auctionFormalityName, Long auctionMethodId, String auctionMethodName, Long assetId, Integer statusRefund, Integer auctionStatus, Integer statusDeposit, boolean statusRefuseWin, boolean statusJoined, boolean statusEnding, Integer signature, Boolean statusRetract, Integer regulationAuctionStatus, Integer assetStatus, Long auctionId, Long winnerId, String filePathDeposit, Integer winnerNth, boolean assetCancelPlaying) {
        super(regulationId, code, startTime, auctionFormalityId, auctionMethodId);
        this.type = type;
        this.auctionFormalityName = auctionFormalityName;
        this.auctionMethodName = auctionMethodName;
        this.assetId = assetId;
        this.statusRefund = statusRefund;
        this.auctionStatus = auctionStatus;
        this.statusDeposit = statusDeposit;
        this.statusRefuseWin = statusRefuseWin;
        this.statusJoined = statusJoined;
        this.statusEnding = statusEnding;
        this.signature = signature;
        this.statusRetract = statusRetract;
        this.regulationAuctionStatus = regulationAuctionStatus;
        this.assetStatus = assetStatus;
        this.auctionId = auctionId;
        this.winnerId = winnerId;
        this.filePathDeposit = filePathDeposit;
        this.winnerNth = winnerNth;
        this.assetCancelPlaying = assetCancelPlaying;
    }

    public RegulationDto(Date createDate, Date updateDate, Long createBy, Long updateBy,
                         Long regulationId, Long auctionReqId, Long auctionFormalityId,
                         String code, Date startTime, Integer numberOfRounds, Integer timePerRound,
                         Date startRegistrationDate, Date endRegistrationDate, Long auctionMethodId,
                         boolean historyStatus, int status, String reasonCancel, Long auctioneerId,
                         Date realStartTime, Date realEndTime, Integer auctionStatus,
                         Date endTime, Date paymentStartTime, Date paymentEndTime, boolean retractPrice, String auctionFormalityName, String auctionMethodName,
                         String filePathRegulation, String nameAccount, String filePathReport, String fileNameReport) {
        super(createDate, updateDate, createBy, updateBy, regulationId, auctionReqId, auctionFormalityId, code, startTime, numberOfRounds, timePerRound, startRegistrationDate, endRegistrationDate, auctionMethodId, historyStatus, status, reasonCancel, auctioneerId, realStartTime, realEndTime, auctionStatus, endTime, paymentStartTime, paymentEndTime, retractPrice);
        this.auctionFormalityName = auctionFormalityName;
        this.auctionMethodName = auctionMethodName;
        this.filePathRegulation = filePathRegulation;
        this.nameAccount = nameAccount;
        this.filePathReport = filePathReport;
        this.fileNameReport = fileNameReport;
    }
}
