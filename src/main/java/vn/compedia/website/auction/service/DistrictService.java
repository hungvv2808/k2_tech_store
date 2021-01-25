package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.District;

public interface DistrictService {
    void deleteDistrict(Long id);
    void deleteById (Long id);
    void save(District district);
    void deleteDistrictByDistrictId(Long id);
}
