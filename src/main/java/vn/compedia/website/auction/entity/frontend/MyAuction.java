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
public class MyAuction extends BaseResponseDto {
    @JsonProperty("serial_number")
    private int serialNumber;
    private Long id;
    private String code;
    private Integer count;
    @JsonProperty("auction_formality_id")
    private Long auctionFormalityId;
    @JsonProperty("auction_formality")
    private String auctionFormality;
    @JsonProperty("auction_method_id")
    private Long auctionMethodId;
    @JsonProperty("auction_method")
    private String auctionMethod;
    @JsonProperty("time_start")
    private String timeStart;
    @JsonProperty("time_finish")
    private String timeFinish;
    @JsonProperty("time_remaining")
    private Long timeRemaining;
    @JsonProperty("time_remaining_string")
    private String timeRemainingString;
    private List<MyAsset> asset;
}
