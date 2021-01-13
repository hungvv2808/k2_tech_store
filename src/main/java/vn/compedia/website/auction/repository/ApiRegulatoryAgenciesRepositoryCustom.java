package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiRegulatoryAgenciesSearchDto;
import vn.compedia.website.auction.model.api.ApiRegulatoryAgencies;

import java.util.List;

public interface ApiRegulatoryAgenciesRepositoryCustom {
    List<ApiRegulatoryAgencies> search(ApiRegulatoryAgenciesSearchDto searchDto);
    Long countSearch(ApiRegulatoryAgenciesSearchDto searchDto);
    void deleteAllRecords();
}
