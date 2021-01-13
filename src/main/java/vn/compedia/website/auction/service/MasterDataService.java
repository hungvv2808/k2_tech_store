package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.Commune;
import vn.compedia.website.auction.model.District;
import vn.compedia.website.auction.model.Province;

import java.util.List;

public interface MasterDataService {
    List<Province> getAllProvince();
    List<District> getAllDistrictByCode(String code);
    List<Commune> getAllCommuneByDistrictId(Long districtId);
}
