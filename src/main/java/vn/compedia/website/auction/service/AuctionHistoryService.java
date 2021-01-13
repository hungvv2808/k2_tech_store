package vn.compedia.website.auction.service;

import vn.compedia.website.auction.dto.auction.AuctionRegisterSearchDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;

import java.util.List;

public interface AuctionHistoryService {
    List<RegulationDto> search(AuctionRegisterSearchDto auctionRegisterSearchDto);
    Long countSearch(AuctionRegisterSearchDto auctionRegisterSearchDto);
}
