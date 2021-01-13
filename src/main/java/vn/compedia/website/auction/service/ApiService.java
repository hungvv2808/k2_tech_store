package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.*;

import java.util.List;

public interface ApiService {
    int uploadProvince(List<ProvinceApi> list);
    int uploadProvinceDb(List<Province> list);
    int uploadProvinceVersion(List<ProvinceVersion> list);
    int uploadDistrict(List<DistrictApi> list);
    int uploadDistrictDb(List<District> list);
    int uploadDistrictVersion(List<DistrictVersion> list);
    int uploadCommune(List<CommuneApi> list);
    int uploadCommuneDb(List<Commune> list);
    int uploadCommuneVersion(List<CommuneVersion> list);
    int uploadHistorySync(List<HistorySync> list);

    int uploadProvinceComeBack(List<ProvinceApi> list);
    int uploadDistrictComeBack(List<DistrictApi> list);
    int uploadCommuneComeBack(List<Commune> list);
}
