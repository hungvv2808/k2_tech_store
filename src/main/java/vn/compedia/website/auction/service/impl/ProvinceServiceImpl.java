package vn.compedia.website.auction.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.compedia.website.auction.model.Province;
import vn.compedia.website.auction.model.ProvinceApi;
import vn.compedia.website.auction.repository.ProvinceApiRepository;
import vn.compedia.website.auction.repository.ProvinceRepository;
import vn.compedia.website.auction.service.ProvinceService;

import javax.transaction.Transactional;

@Service
public class ProvinceServiceImpl implements ProvinceService {

    @Autowired
    protected ProvinceRepository provinceRepository;
    @Autowired
    protected ProvinceApiRepository provinceApiRepository;

    @Override
    @Transactional
    public void deleteProvince(Long id) {
        provinceRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void save(Province province) {
        provinceRepository.save(province);
    }

    @Override
    @Transactional
    public void save(ProvinceApi provinceApi) {
        provinceApiRepository.save(provinceApi);
    }
}
