package vn.zeus.website.store.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.zeus.website.store.model.District;
import vn.zeus.website.store.repository.DistrictRepository;
import vn.zeus.website.store.service.DistrictService;

import javax.transaction.Transactional;

@Service
public class DistrictServiceImpl implements DistrictService {
    @Autowired
    protected DistrictRepository districtRepository;

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
    public void deleteDistrictByDistrictId(Long id) {
        districtRepository.deleteById(id);
    }
}
