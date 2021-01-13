package vn.compedia.website.auction.dto.auction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.model.Bid;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BidDto extends Bid {
    private String fullName;
    private Long highestPrice;
    private int statusDeposit;
    private Long numberOfRound;
    private String name;
    private String code;
    private Long accountId;

    public BidDto(Date createDate, Long bidId, Long priceRoundId, Long auctionRegisterId, Long assetId, Long money, String fullName, Integer winnerSn) {
        super(createDate, bidId, priceRoundId, auctionRegisterId, assetId, money, winnerSn);
        this.fullName = fullName;
    }

    public BidDto(Date time, Long bidId, Long priceRoundId, Long auctionRegisterId, Long assetId, Long money, String fullName, String code) {
        super(time, bidId, priceRoundId, auctionRegisterId, assetId, money);
        this.fullName = fullName;
        this.code = code;
    }
}
