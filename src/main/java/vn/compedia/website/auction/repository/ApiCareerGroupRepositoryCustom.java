package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiCareerGroupSearchDto;
import vn.compedia.website.auction.model.api.ApiCareerGroup;

import java.util.List;

public interface ApiCareerGroupRepositoryCustom {
    List<ApiCareerGroup> search(ApiCareerGroupSearchDto apiCareerGroupSearchDto);
    Long countSearch(ApiCareerGroupSearchDto apiCareerGroupSearchDto);
    void deleteAllRecords();
}
