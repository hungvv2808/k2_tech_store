package vn.compedia.website.auction.dto.export;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.dto.auction.PriceRoundDto;
import vn.compedia.website.auction.model.Bid;
import vn.compedia.website.auction.model.Regulation;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExportExcelDto extends Regulation {

    public ExportExcelDto(Long regulationId, String code, Long auctioneerId, Long auctionFormalityId, Long auctionMethodId, Date startTime, Date realEndTime, String fullNameAuctioneer, Long assetId, String assetName, Long priceRoundId, Long startingPrice, Long highestPrice, Long bidId, Long auctionRegisterId, Long registerAccountId, String fullNameRegister, Date createDate, Long money) {
        super(regulationId, code, auctioneerId, auctionFormalityId, auctionMethodId, startTime, realEndTime);
        this.fullNameAuctioneer = fullNameAuctioneer;
        this.assetId = assetId;
        this.assetName = assetName;
        this.priceRoundId = priceRoundId;
        this.startingPrice = startingPrice;
        this.highestPrice = highestPrice;
        this.bidId = bidId;
        this.auctionRegisterId = auctionRegisterId;
        this.registerAccountId = registerAccountId;
        this.fullNameRegister = fullNameRegister;
        this.createDate = createDate;
        this.money = money;
    }

    private String fullNameAuctioneer;
    private Long assetId;
    private String assetName;
    private Long priceRoundId;
    private Long startingPrice;
    private Long highestPrice;
    private Long bidId;
    private Long auctionRegisterId;
    private Long registerAccountId;
    private String fullNameRegister;

    //Thời gian trả giá
    private Date createDate;
    private Long money;

    private List<PriceRoundDto> priceRoundDtoList;

    private List<Bid> bidList;
}
