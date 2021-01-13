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
public class RunTheAuctionDto extends Regulation {
    private String filePath;
    private String reasonFilePath;
    private String auctionMethodName;
    private String auctionFormalityName;
    private String assetName;
}
