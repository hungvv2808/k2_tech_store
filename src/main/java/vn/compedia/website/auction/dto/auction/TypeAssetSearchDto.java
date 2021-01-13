package vn.compedia.website.auction.dto.auction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.dto.BaseSearchDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeAssetSearchDto extends BaseSearchDto {
    private Long typeAssetId;
    private String code;
    private String name;
    private Boolean status;
}
