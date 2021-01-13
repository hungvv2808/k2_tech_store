package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.District;
import vn.compedia.website.auction.model.DistrictApi;
import vn.compedia.website.auction.model.Province;

public interface DistrictService {
    void deleteDistrict(Long id);
    void deleteById (Long id);
    void save(District district);
    void save(DistrictApi districtApi);
    void deleteDistrictByDistrictId(Long id);
}
