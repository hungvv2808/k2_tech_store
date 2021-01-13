package vn.compedia.website.auction.controller.admin.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.dto.common.CityDistrictDto;
import vn.compedia.website.auction.model.Commune;
import vn.compedia.website.auction.model.District;
import vn.compedia.website.auction.model.Province;
import vn.compedia.website.auction.repository.CommuneRepository;
import vn.compedia.website.auction.repository.DistrictRepository;
import vn.compedia.website.auction.repository.ProvinceRepository;

import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = "session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CityDistrictController implements Serializable {

    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private CommuneRepository communeRepository;

    private List<Province> listProvince;
    private List<District> listDistrict;
    private List<Commune> listCommune;
    private boolean checkDisable;

    private CityDistrictDto cityDistrictDto;

    public void resetAll() {
        checkDisable = false;
        cityDistrictDto = new CityDistrictDto();
        listProvince = (List<Province>) provinceRepository.findAll();
        onChangeCity();
    }

    public void resetAllValue() {
        cityDistrictDto = new CityDistrictDto();
        listDistrict = new ArrayList<>();
        resetCommune();
    }

    public void resetCommune() {
        listCommune = new ArrayList<>();
    }

    public void onChangeCity() {
        if (cityDistrictDto.getProvinceId() != null) {
            listDistrict = districtRepository.findDistrictsByProvinceId(cityDistrictDto.getProvinceId());
            listCommune = new ArrayList<>();
            cityDistrictDto = new CityDistrictDto();
        } else {
            resetAllValue();
        }
        onChangeDistrict();
    }

    public void onChangeDistrict() {
        if (cityDistrictDto.getDistrictId() != null) {
            listCommune = communeRepository.getCommuneByDistrictId(cityDistrictDto.getDistrictId());
        } else {
            resetCommune();
        }
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
