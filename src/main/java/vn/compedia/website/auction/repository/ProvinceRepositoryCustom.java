package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.base.ProvinceSearchDto;
import vn.compedia.website.auction.dto.common.CityDistrictDto;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.Province;

import java.util.List;

public interface ProvinceRepositoryCustom {
    List<Province> search(ProvinceSearchDto provinceSearchDto);

    Long countSearch(ProvinceSearchDto provinceSearchDto);

    void deleteAllRecords();

}
