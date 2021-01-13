package vn.compedia.website.auction.repository;


import vn.compedia.website.auction.dto.auction.BidDto;
import vn.compedia.website.auction.dto.auction.BidSearchDto;
import vn.compedia.website.auction.dto.export.ExportExcelDto;
import vn.compedia.website.auction.model.Bid;

import java.util.List;

public interface BidRepositoryCustom {
    List<BidDto> search(BidSearchDto bidSearchDto);
    Long countSearch(BidSearchDto bidSearchDto);
    List<Bid> findAllBargain(BidSearchDto searchDto);
    List<ExportExcelDto> getHistoryBargain(Long regulationId);
    List<BidDto> searchForHistoryFormalityDirectUp(BidSearchDto bidSearchDto);
    Long countSearchForHistoryFormalityDirectUp(BidSearchDto bidSearchDto);
}
