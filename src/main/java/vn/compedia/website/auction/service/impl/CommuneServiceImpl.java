package vn.compedia.website.auction.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.compedia.website.auction.model.Commune;
import vn.compedia.website.auction.model.CommuneApi;
import vn.compedia.website.auction.repository.CommuneApiRepository;
import vn.compedia.website.auction.repository.CommuneRepository;
import vn.compedia.website.auction.service.CommuneService;

import javax.transaction.Transactional;

@Service
public class CommuneServiceImpl implements CommuneService {
    @Autowired
    protected CommuneRepository communeRepository;

    @Autowired
    protected CommuneApiRepository communeApiRepository;

    @Override
    @Transactional
    public void deleteCommune(Long id) {
        communeRepository.deleteCommunesByProvinceId(id);
    }

    @Override
    @Transactional
    public void deleteCommunesByDistrictId(Long id) {
        communeRepository.deleteCommunesByDistrictId(id);
    }


    @Override
    @Transactional
    public void deleteById(Long id) {
        communeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void save(Commune commune) {
        communeRepository.save(commune);
    }


    @Override
    @Transactional
    public void save(CommuneApi communeApi) {
        communeApiRepository.save(communeApi);
    }

    @Override
    @Transactional
    public void deleteCommunebyCommuneId(Long id) {
        communeRepository.deleteById(id);
    }

}
