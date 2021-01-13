package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiCountrySearchDto;
import vn.compedia.website.auction.model.api.ApiCountry;

import java.util.List;

public interface ApiCountryRepositoryCustom {
    List<ApiCountry> search(ApiCountrySearchDto searchDto);
    Long countSearch(ApiCountrySearchDto searchDto);
    void deleteAllRecords();
}
