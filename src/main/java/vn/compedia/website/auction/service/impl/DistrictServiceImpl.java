package vn.compedia.website.auction.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.compedia.website.auction.model.District;
import vn.compedia.website.auction.model.DistrictApi;
import vn.compedia.website.auction.repository.DistrictApiRepository;
import vn.compedia.website.auction.repository.DistrictRepository;
import vn.compedia.website.auction.service.DistrictService;

import javax.transaction.Transactional;

@Service
public class DistrictServiceImpl implements DistrictService {
    @Autowired
    protected DistrictRepository districtRepository;

    @Autowired
    protected DistrictApiRepository districtApiRepository;

    @Override
    @Transactional
    public void deleteDistrict(Long id) {
        districtRepository.deleteDistrictsByProvinceId(id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        districtRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void save(District district) {
        districtRepository.save(district);
    }

    @Override
    @Transactional
    public void save(DistrictApi districtApi) {
        districtApiRepository.save(districtApi);
    }

    @Override
    @Transactional
    public void deleteDistrictByDistrictId(Long id) {
        districtRepository.deleteById(id);
    }
}
