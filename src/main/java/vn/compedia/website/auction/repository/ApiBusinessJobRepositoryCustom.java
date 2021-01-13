package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiBusinessJobSearchDto;
import vn.compedia.website.auction.model.api.ApiBusinessJob;

import java.util.List;

public interface ApiBusinessJobRepositoryCustom {
    List<ApiBusinessJob> search(ApiBusinessJobSearchDto searchDto);
    Long countSearch(ApiBusinessJobSearchDto searchDto);
    void deleteAllRecords();
}
