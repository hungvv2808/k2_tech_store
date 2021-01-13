package vn.compedia.website.auction.entity.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MyPriceRoundHistory {
    private String time;
    @JsonProperty("current_round")
    private Integer currentRound;
    @JsonProperty("highest_price")
    private Long highestPrice;
    @JsonProperty("code")
    private String code;
    @JsonProperty("money")
    private Long money;

    public MyPriceRoundHistory(String time, Integer currentRound, Long highestPrice) {
        this.time = time;
        this.currentRound = currentRound;
        this.highestPrice = highestPrice;
    }

    public MyPriceRoundHistory(String time, String code, Long money) {
        this.time = time;
        this.code = code;
        this.money = money;
    }
}
