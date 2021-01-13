package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.auction.AssetSearchDto;
import vn.compedia.website.auction.dto.request.AuctionReqDto;
import vn.compedia.website.auction.dto.request.AuctionReqSearchDto;

import java.math.BigInteger;
import java.util.List;

public interface AuctionReqRepositoryCustom {
    List<AuctionReqDto> search(AuctionReqSearchDto searchDto);
    BigInteger countSearch(AuctionReqSearchDto searchDto);
    BigInteger getTotalAuctionReqContract(AssetSearchDto searchDto);
}
