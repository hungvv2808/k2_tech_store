package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.base.CommuneDto;
import vn.compedia.website.auction.dto.base.CommuneSearchDto;
import vn.compedia.website.auction.model.Commune;

import java.util.List;

public interface CommuneRepositoryCustom {
    List<CommuneDto> search(CommuneSearchDto communeSearchDto);

    Long countSearch(CommuneSearchDto communeSearchDto);

    void deleteAllRecords();
}
