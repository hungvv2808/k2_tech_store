package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiCategoryPositionSearchDto;
import vn.compedia.website.auction.model.api.ApiCategoryPosition;

import java.util.List;

public interface ApiCategoryPositionRepositoryCustom {
    List<ApiCategoryPosition> search(ApiCategoryPositionSearchDto searchDto);
    Long countSearch(ApiCategoryPositionSearchDto searchDto);
    void deleteAllRecords();
}
