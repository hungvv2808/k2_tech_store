package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiRegulatoryAgencySearchDto;
import vn.compedia.website.auction.model.api.ApiRegulatoryAgency;

import java.util.List;

public interface ApiRegulatoryAgencyRepositoryCustom {
    List<ApiRegulatoryAgency> search(ApiRegulatoryAgencySearchDto searchDto);
    Long countSearch(ApiRegulatoryAgencySearchDto searchDto);
    void deleteAllRecords();
}
