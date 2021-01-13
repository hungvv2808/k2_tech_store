package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiNationSearchDto;
import vn.compedia.website.auction.model.api.ApiNation;

import java.util.List;

public interface ApiNationRepositoryCustom {
    List<ApiNation> search(ApiNationSearchDto searchDto);
    Long countSearch(ApiNationSearchDto searchDto);
    void deleteAllRecords();
}
