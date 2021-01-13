package vn.compedia.website.auction.dto.auction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.model.PriceRound;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PriceRoundDto extends PriceRound {
    List<BidDto> bidDtoList;
    private Date currentTime;
    private Date startTime;
    private Integer timePerRound;

    public PriceRoundDto(Long priceRoundId, Long regulationId, Long assetId, Integer numberOfRound, Long startingPrice, Long highestPrice, Long auctionRegisterId) {
        super(priceRoundId, regulationId, assetId, numberOfRound, startingPrice, highestPrice, auctionRegisterId);
    }

    public PriceRoundDto(Long priceRoundId, Long regulationId, Long assetId, Integer numberOfRound, Long startingPrice, Long highestPrice, Long auctionRegisterId, List<BidDto> bidDtoList) {
        super(priceRoundId, regulationId, assetId, numberOfRound, startingPrice, highestPrice, auctionRegisterId);
        this.bidDtoList = bidDtoList;
    }
}
