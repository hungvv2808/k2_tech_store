package vn.compedia.website.auction.dto.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.compedia.website.auction.model.District;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DistrictDto extends District {
    private static final long serialVersionUID = 3084707787010033176L;

    private String provinceName;
    private List<District> districtList;

    public DistrictDto() {
    }

    public District getParent() {
        District dt = new District();
        dt.setDistrictId(getDistrictId());
        dt.setProvinceId(getProvinceId());
        dt.setCode(getCode());
        dt.setName(getName());
        dt.setStatus(getStatus());
        return dt;
    }
}
