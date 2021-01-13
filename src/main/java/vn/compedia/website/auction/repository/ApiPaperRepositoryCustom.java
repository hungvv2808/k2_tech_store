package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiNationSearchDto;
import vn.compedia.website.auction.dto.api.ApiPaperSearchDto;
import vn.compedia.website.auction.model.api.ApiNation;
import vn.compedia.website.auction.model.api.ApiPaper;

import java.util.List;

public interface ApiPaperRepositoryCustom {
    List<ApiPaper> search(ApiPaperSearchDto searchDto);
    Long countSearch(ApiPaperSearchDto searchDto);
    void deleteAllRecords();
}
