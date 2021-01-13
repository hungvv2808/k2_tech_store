package vn.compedia.website.auction.dto.assign_auction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.model.Regulation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignAuctionDto extends Regulation {
    private String orgName;
    private String auctioneerName;
    private String peopleWAsset;
    private String auctionMethodName;
    private String auctionFormalityName;
    private boolean org;
    private Long roleId;
    private String filePath;
    private Integer regulationStatus;
    private Integer auctioneerStatus;
    private Integer peopleWAssetStatus;
    private String assetName;
}
