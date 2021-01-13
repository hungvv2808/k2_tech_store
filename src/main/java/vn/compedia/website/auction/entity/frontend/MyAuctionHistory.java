package vn.compedia.website.auction.entity.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.compedia.website.auction.dto.BaseResponseDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MyAuctionHistory extends BaseResponseDto {
    @JsonProperty("serial_number")
    private int serialNumber;
    private Long id;
    @JsonProperty("time_auction")
    private String timeAuction;
    @JsonProperty("asset_id")
    private Long assetId;
    @JsonProperty("asset_name")
    private String assetName;
    @JsonProperty("auction_status")
    private Integer auctionStatus;
    private String code;
    @JsonProperty("auction_form")
    private String auctionForm;
    @JsonProperty("auction_method")
    private String auctionMethod;
    private boolean type;
    private List<MyAsset> regulation;
    @JsonProperty("regulation_id")
    private Long regulationId;
    @JsonProperty("asset_status")
    private Integer assetStatus;
    @JsonProperty("payment_status")
    private Integer paymentStatus;
    @JsonProperty("asset_status_ending")
    private boolean assetStatusEnding;
    @JsonProperty("winner_id")
    private Long winnerId;
    @JsonProperty("note_user")
    private String noteUser;
}
