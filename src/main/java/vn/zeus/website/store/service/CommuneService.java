package vn.zeus.website.store.service;

import vn.zeus.website.store.model.Commune;

public interface CommuneService {
    void deleteCommune(Long id);
    void deleteCommunesByDistrictId(Long id);
    void deleteById(Long id);
    void save(Commune commune);
    void deleteCommunebyCommuneId(Long id);
}
