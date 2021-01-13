package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.auction.AuctionRegisterSearchDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;

import java.util.List;

public interface AuctionHistoryRepositoryCustom {
    List<RegulationDto> search(AuctionRegisterSearchDto auctionRegisterSearchDto);
    Long countSearch(AuctionRegisterSearchDto auctionRegisterSearchDto);
}
