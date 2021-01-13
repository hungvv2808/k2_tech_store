package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiReligionSearchDto;
import vn.compedia.website.auction.model.api.ApiReligion;

import java.util.List;

public interface ApiReligionRepositoryCustom {
    List<ApiReligion> search(ApiReligionSearchDto apiReligionSearchDto);
    Long countSearch(ApiReligionSearchDto apiReligionSearchDto);
    void deleteAllRecords();
}
