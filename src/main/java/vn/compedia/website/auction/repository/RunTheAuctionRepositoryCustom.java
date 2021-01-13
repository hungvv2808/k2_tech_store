package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.assign_auction.RunTheAuctionDto;
import vn.compedia.website.auction.dto.assign_auction.RunTheAuctionSearchDto;

import java.util.List;

public interface RunTheAuctionRepositoryCustom {
    List<RunTheAuctionDto> search(RunTheAuctionSearchDto runTheAuctionSearchDto);
    Long countSearch(RunTheAuctionSearchDto runTheAuctionSearchDto);
}
