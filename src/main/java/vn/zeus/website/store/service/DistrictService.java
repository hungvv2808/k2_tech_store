package vn.zeus.website.store.service;

import vn.zeus.website.store.model.District;

public interface DistrictService {
    void deleteDistrict(Long id);
    void deleteById (Long id);
    void save(District district);
    void deleteDistrictByDistrictId(Long id);
}
