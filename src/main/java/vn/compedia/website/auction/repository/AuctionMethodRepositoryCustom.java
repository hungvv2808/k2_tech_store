package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.manage.AuctionMethodSearchDto;
import vn.compedia.website.auction.model.AuctionMethod;

import java.util.List;

public interface AuctionMethodRepositoryCustom {
    List<AuctionMethod> search(AuctionMethodSearchDto auctionMethodSearchDto);

    Long countSearch(AuctionMethodSearchDto auctionMethodSearchDto);
}
