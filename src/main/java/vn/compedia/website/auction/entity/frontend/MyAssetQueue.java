package vn.compedia.website.auction.entity.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyAssetQueue {
    @JsonProperty("is_start")
    private boolean start;
    @JsonProperty("current_price")
    private Long currentPrice;
    @JsonProperty("price_bargained")
    private Long priceBargained;
    @JsonProperty("end_time_round")
    private Long endTimeRound;
    @JsonProperty("end_time_round_string")
    private String endTimeRoundString;
    @JsonProperty("is_winner")
    private boolean winner;
    @JsonProperty("price_winner")
    private Long priceWinner;
    @JsonProperty("is_deposit")
    private boolean deposit;
    @JsonProperty("in_time_confirm")
    private boolean inTimeConfirm;
    @JsonProperty("round_confirm")
    private Integer roundConfirm;
    @JsonProperty("show_bargain")
    private boolean showBargain;
    @JsonProperty("ended")
    private boolean ended;
}
