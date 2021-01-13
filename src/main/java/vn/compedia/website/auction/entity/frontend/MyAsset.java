package vn.compedia.website.auction.entity.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class MyAsset {
    private Long id;
    private String name;
    @JsonProperty("auction_formality_id")
    private Long auctionFormalityId;
    @JsonProperty("auction_method_id")
    private Long auctionMethodId;
    @JsonProperty("count_now_id")
    private Long countNowId;
    @JsonProperty("count_now")
    private Integer countNow;
    @JsonProperty("start_price")
    private Long startPrice;
    @JsonProperty("step_price")
    private Long stepPrice;
    private Long price;
    private Integer status;
    @JsonProperty("auction_register_id")
    private Long auctionRegisterId;
    @JsonProperty("auction_register_status")
    private Integer auctionRegisterStatus;
    @JsonProperty("is_winner")
    private Boolean winner;
    @JsonProperty("price_winner")
    private Long priceWinner;
    @JsonProperty("highest_price")
    private Long highestPrice;
    @JsonProperty("retract_price")
    private boolean retractPrice;
    @JsonProperty("asset_playing")
    private MyAssetQueue myAssetQueue;
    @JsonProperty("check_time")
    private boolean checkTime;

    // set data regulation
    @JsonProperty("regulation_id")
    private Long regulationId;
    private String code;
    @JsonProperty("start_time")
    private String startTime;
    private boolean type;
    @JsonProperty("auction_formality_name")
    private String auctionFormalityName;
    @JsonProperty("auction_method_name")
    private String auctionMethodName;
    @JsonProperty("asset_id")
    private Long assetId;
    @JsonProperty("status_refund")
    private Integer statusRefund;

    @JsonProperty("auction_status")
    private Integer auctionStatus;
    @JsonProperty("status_deposit")
    private Integer statusDeposit;
    @JsonProperty("status_refuse_win")
    private boolean statusRefuseWin;
    @JsonProperty("status_joined")
    private boolean statusJoined;
    @JsonProperty("status_ending")
    private boolean statusEnding;
    @JsonProperty("status_retract")
    private Boolean statusRetract;
    @JsonProperty("regulation_auction_status")
    private Integer regulationAuctionStatus;
    @JsonProperty("asset_status")
    private Integer assetStatus;

    private Integer signature;

    @JsonProperty("auction_id")
    private Long auctionId;
    @JsonProperty("winner_id")
    private Long winnerId;
    @JsonProperty("status_user")
    private String statusUser;
    @JsonProperty("status_payment")
    private String statusPayment;

    public MyAsset(Long assetId, String name) {
        this.id = assetId;
        this.name = name;
    }

    public MyAsset(Long regulationId, String code, String startTime, boolean type, Long auctionFormalityId, String auctionFormalityName, Long auctionMethodId, String auctionMethodName, Long assetId, Integer statusRefund, Integer auctionStatus, Integer statusDeposit, boolean statusRefuseWin, boolean statusJoined, boolean statusEnding, Integer signature, Boolean statusRetract, Integer regulationAuctionStatus, Integer assetStatus, Long auctionId, Long winnerId, String statusUser, String statusPayment) {
        this.regulationId = regulationId;
        this.code = code;
        this.startTime = startTime;
        this.type = type;
        this.auctionFormalityId = auctionFormalityId;
        this.auctionFormalityName = auctionFormalityName;
        this.auctionMethodId = auctionMethodId;
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
        this.statusUser = statusUser;
        this.statusPayment = statusPayment;
    }

    public MyAsset(Long assetId, String name, Long auctionFormalityId, Long auctionMethodId, Long priceRoundId, Integer currentRound, Long startingPrice, Long priceStep, Long price, Integer status, Long auctionRegisterId, Integer auctionRegisterStatus, boolean assetManagementEnding, boolean assetManagementEndingFinal, Long highestPrice, boolean retractPrice, MyAssetQueue buildAssetPlaying, boolean checkTime, Integer statusRefund) {
        this.id = assetId;
        this.name = name;
        this.auctionFormalityId = auctionFormalityId;
        this.auctionMethodId = auctionMethodId;
        this.countNowId = priceRoundId;
        this.countNow = currentRound;
        this.startPrice = startingPrice;
        this.stepPrice = priceStep;
        this.price = price;
        this.status = status;
        this.auctionRegisterId = auctionRegisterId;
        this.auctionRegisterStatus = auctionRegisterStatus;
        this.winner = assetManagementEnding;
        this.statusEnding = assetManagementEndingFinal;
        this.highestPrice = highestPrice;
        this.retractPrice = retractPrice;
        this.myAssetQueue = buildAssetPlaying;
        this.checkTime = checkTime;
        this.statusRefund = statusRefund;
    }
}
