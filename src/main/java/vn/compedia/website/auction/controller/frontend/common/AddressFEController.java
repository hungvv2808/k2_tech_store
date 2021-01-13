package vn.compedia.website.auction.controller.frontend.common;


import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.frontend.AuthorizationFEController;
import vn.compedia.website.auction.dto.common.CityDistrictDto;
import vn.compedia.website.auction.dto.user.AccountDto;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.Commune;
import vn.compedia.website.auction.model.District;
import vn.compedia.website.auction.model.Province;
import vn.compedia.website.auction.repository.AccountRepository;
import vn.compedia.website.auction.repository.CommuneRepository;
import vn.compedia.website.auction.repository.DistrictRepository;
import vn.compedia.website.auction.repository.ProvinceRepository;

import javax.inject.Inject;
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
        return accountRepository.getNameAddress(accountId);
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
