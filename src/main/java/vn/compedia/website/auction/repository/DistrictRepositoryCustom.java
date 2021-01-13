package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.base.DistrictDto;
import vn.compedia.website.auction.dto.base.DistrictSearchDto;

import java.util.List;

public interface DistrictRepositoryCustom {

    List<DistrictDto> search(DistrictSearchDto districtSearchDto);

    Long countSearch(DistrictSearchDto districtSearchDto);

    void deleteAllRecords();
}
