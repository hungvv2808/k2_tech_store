package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiJobSearchDto;
import vn.compedia.website.auction.model.api.ApiJob;

import java.util.List;

public interface ApiJobRepositoryCustom {
    List<ApiJob> search(ApiJobSearchDto apiJobSearchDto);
    Long countSearch(ApiJobSearchDto apiJobSearchDto);
    void deleteAllRecords();
}
