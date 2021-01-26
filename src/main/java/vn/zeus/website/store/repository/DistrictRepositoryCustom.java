package vn.zeus.website.store.repository;

import vn.zeus.website.store.dto.base.DistrictDto;
import vn.zeus.website.store.dto.base.DistrictSearchDto;

import java.util.List;

public interface DistrictRepositoryCustom {

    List<DistrictDto> search(DistrictSearchDto districtSearchDto);

    Long countSearch(DistrictSearchDto districtSearchDto);

    void deleteAllRecords();
}
