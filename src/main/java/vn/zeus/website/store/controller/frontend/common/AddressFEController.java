package vn.zeus.website.store.controller.frontend.common;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.zeus.website.store.dto.common.CityDistrictDto;
import vn.zeus.website.store.dto.user.AccountDto;
import vn.zeus.website.store.model.Commune;
import vn.zeus.website.store.model.District;
import vn.zeus.website.store.model.Province;
import vn.zeus.website.store.repository.AccountRepository;
import vn.zeus.website.store.repository.CommuneRepository;
import vn.zeus.website.store.repository.DistrictRepository;
import vn.zeus.website.store.repository.ProvinceRepository;

import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@Getter
@Setter
@Scope(value = "session")
public class AddressFEController implements Serializable {
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private CommuneRepository communeRepository;
    @Autowired
    private AccountRepository accountRepository;

    private List<Province> listProvince;
    private List<District> listDistrict;
    private List<Commune> listCommune;

    private CityDistrictDto cityDistrictDto = new CityDistrictDto();

    public void resetAll() {
        cityDistrictDto = new CityDistrictDto();
        listProvince = (List<Province>) provinceRepository.findAll();
        onChangeCity();
    }

    public void onChangeCity() {
        if (cityDistrictDto.getProvinceId() != null) {
            listDistrict = districtRepository.findDistrictsByProvinceId(cityDistrictDto.getProvinceId());
            listCommune = new ArrayList<>();
            cityDistrictDto = new CityDistrictDto();
        } else {
            listDistrict = new ArrayList<>();
            cityDistrictDto = new CityDistrictDto();
            listCommune = new ArrayList<>();
        }
        onChangeDistrict();
    }

    public void onChangeDistrict() {
        if (cityDistrictDto.getDistrictId() != null) {
            listCommune = communeRepository.getCommuneByDistrictId(cityDistrictDto.getDistrictId());
        } else {
            listCommune = new ArrayList<>();
        }
    }

    public AccountDto getAddressForAccount(Long accountId) {
        return null;
    }

    public void loadData(){
        if (cityDistrictDto.getProvinceId() != null) {
            listDistrict = districtRepository.findDistrictsByProvinceId(cityDistrictDto.getProvinceId());
        }
        if (cityDistrictDto.getDistrictId() != null) {
            listCommune = communeRepository.getCommuneByDistrictId(cityDistrictDto.getDistrictId());
        }
    }

    public String upperCaseFirstChar(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
