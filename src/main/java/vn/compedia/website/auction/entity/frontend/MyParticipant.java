package vn.compedia.website.auction.entity.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MyParticipant {
    @JsonProperty("auction_register_id")
    private Long auctionRegisterId;
    private String code;
    @JsonProperty("role_id")
    private Long roleId;
    private String notes;

}
