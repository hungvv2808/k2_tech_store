package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiNationSearchDto;
import vn.compedia.website.auction.dto.api.HistorySyncSearchDto;
import vn.compedia.website.auction.model.HistorySync;

import java.util.List;

public interface HistorySyncRepositoryCustom {
    List<HistorySync> search(HistorySyncSearchDto searchDto);
    Long countSearch(HistorySyncSearchDto searchDto);
}
