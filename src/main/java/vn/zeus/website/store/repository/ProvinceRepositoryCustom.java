package vn.zeus.website.store.repository;

import vn.zeus.website.store.dto.base.ProvinceSearchDto;
import vn.zeus.website.store.model.Province;

import java.util.List;

public interface ProvinceRepositoryCustom {
    List<Province> search(ProvinceSearchDto provinceSearchDto);

    Long countSearch(ProvinceSearchDto provinceSearchDto);

    void deleteAllRecords();

}
