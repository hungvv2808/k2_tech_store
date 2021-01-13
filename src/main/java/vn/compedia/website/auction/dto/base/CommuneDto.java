package vn.compedia.website.auction.dto.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.model.Commune;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommuneDto extends Commune {
    private static final long serialVersionUID = 2155807344162308530L;

    private String districtName;
    private String provinceName;



    public Commune getParent() {
        Commune dt = new Commune();
        dt.setCommuneId(getCommuneId());
        dt.setDistrictId(getDistrictId());
        dt.setProvinceId(getProvinceId());
        dt.setCode(getCode());
        dt.setName(getName());
        dt.setStatus(getStatus());

        return dt;
    }

}
