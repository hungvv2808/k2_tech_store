package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.assign_auction.AssignAuctionDto;
import vn.compedia.website.auction.dto.assign_auction.AssignAuctionSearchDto;

import java.math.BigInteger;
import java.util.List;

public interface AssignAuctionRepositoryCustom {
    List<AssignAuctionDto> search(AssignAuctionSearchDto assignAuctionSearchDto);
    BigInteger countSearch(AssignAuctionSearchDto assignAuctionSearchDto);
}
