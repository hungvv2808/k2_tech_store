package vn.compedia.website.auction.dto.auction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.compedia.website.auction.dto.BaseSearchDto;

@Getter
@Setter
@AllArgsConstructor
public class PriceRoundSearchDto extends BaseSearchDto {
    private Long assetId;
    private boolean stateEnd; // true -> only get price updated

    public PriceRoundSearchDto() {

    }
}
