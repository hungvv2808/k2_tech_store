package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiNationSearchDto;
import vn.compedia.website.auction.dto.api.ApiRegularSearchDto;
import vn.compedia.website.auction.model.api.ApiNation;
import vn.compedia.website.auction.model.api.ApiRegular;

import java.util.List;

public interface ApiRegularRepositoryCustom {
    List<ApiRegular> search(ApiRegularSearchDto searchDto);
    Long countSearch(ApiRegularSearchDto searchDto);
    void deleteAllRecords();
}
