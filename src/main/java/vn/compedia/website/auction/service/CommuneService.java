package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.Commune;

public interface CommuneService {
    void deleteCommune(Long id);
    void deleteCommunesByDistrictId(Long id);
    void deleteById(Long id);
    void save(Commune commune);
    void deleteCommunebyCommuneId(Long id);
}
