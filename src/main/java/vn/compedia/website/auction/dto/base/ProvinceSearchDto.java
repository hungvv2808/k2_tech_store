package vn.compedia.website.auction.dto.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.dto.BaseSearchDto;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceSearchDto extends BaseSearchDto {
    private static final long serialVersionUID = -4686548255447797976L;

    private Long provinceId;
    private String code;
    private String name;
    private Long status;

}
