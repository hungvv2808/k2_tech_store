package vn.compedia.website.auction.dto.auction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.dto.BaseSearchDto;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetSearchDto extends BaseSearchDto {
    private Long assetId;
    private Long regulationId;
    private String name;
    private Long startingPrice;
    private Long priceStep;
    private String description;
    private Long deposit;
    private List<Integer> status;
    private Integer assetStatus;
    private String filePath;
    private String imagePath;
    private Date createDate;
    private Date startTime;
    private Date fromTime;
    private Date toTime;
    private Long fromPrice;
    private Long toPrice;
    private Long numberOfRound;
    private Integer timePerRound;
    private Integer regulationStatus;
    private List<Integer> auctionStatus;
    private Long accountId;
    private boolean historyStatus;
    private Long auctionFormalityId;
    private Long auctionMethodId;
    private Long typeAssetId;
    private Long auctioneerId;
    private Long startingPriceStart;
    private Long startingPriceEnd;
}
