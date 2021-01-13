package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.Commune;
import vn.compedia.website.auction.model.CommuneApi;
import vn.compedia.website.auction.model.Province;

public interface CommuneService {
    void deleteCommune(Long id);
    void deleteCommunesByDistrictId(Long id);
    void deleteById(Long id);
    void save(Commune commune);
    void save(CommuneApi communeApi);
    void deleteCommunebyCommuneId(Long id);
}
