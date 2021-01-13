package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiEducationSearchDto;
import vn.compedia.website.auction.dto.api.ApiRegularSearchDto;
import vn.compedia.website.auction.model.api.ApiEducation;
import vn.compedia.website.auction.model.api.ApiRegular;

import java.util.List;

public interface ApiEducationRepositoryCustom {
    List<ApiEducation> search(ApiEducationSearchDto searchDto);
    Long countSearch(ApiEducationSearchDto searchDto);
    void deleteAllRecords();
}
