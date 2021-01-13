package vn.compedia.website.auction.dto.auction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.dto.BaseSearchDto;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BidSearchDto extends BaseSearchDto {
    private Long bidId;
    private Long priceRoundId;
    private Long auctionRegisterId;
    private Long money;
    private Long assetId;
    private String fullName;
    private Long highestPrice;
    private Long numberOfRound;
    private Long maxNumberOfRound;
}
