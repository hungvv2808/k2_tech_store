package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiCategoryPositionSearchDto;
import vn.compedia.website.auction.dto.api.ApiOfficialsSearchDto;
import vn.compedia.website.auction.model.api.ApiCategoryPosition;
import vn.compedia.website.auction.model.api.ApiOfficials;

import java.util.List;

public interface ApiOfficialsRepositoryCustom {
    List<ApiOfficials> search(ApiOfficialsSearchDto searchDto);
    Long countSearch(ApiOfficialsSearchDto searchDto);
    void deleteAllRecords();
}
