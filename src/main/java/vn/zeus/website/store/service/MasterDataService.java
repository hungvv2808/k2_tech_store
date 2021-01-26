package vn.zeus.website.store.service;

import vn.zeus.website.store.model.Commune;
import vn.zeus.website.store.model.District;
import vn.zeus.website.store.model.Province;

import java.util.List;

public interface MasterDataService {
    List<Province> getAllProvince();
    List<District> getAllDistrictByCode(String code);
    List<Commune> getAllCommuneByDistrictId(Long districtId);
}
