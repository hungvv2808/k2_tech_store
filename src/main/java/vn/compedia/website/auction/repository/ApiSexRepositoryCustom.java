package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiSexSearchDto;
import vn.compedia.website.auction.model.api.ApiSex;

import java.util.List;

public interface ApiSexRepositoryCustom {
    List<ApiSex> search(ApiSexSearchDto searchDto);
    Long countSearch(ApiSexSearchDto searchDto);
    void deleteAllRecords();
}
