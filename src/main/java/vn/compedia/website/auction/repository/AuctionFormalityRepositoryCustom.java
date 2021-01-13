package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.manage.AuctionFormalitySearchDto;
import vn.compedia.website.auction.model.AuctionFormality;

import java.util.List;

public interface AuctionFormalityRepositoryCustom {
    List<AuctionFormality> search(AuctionFormalitySearchDto auctionFormalitySearchDto);

    Long countSearch(AuctionFormalitySearchDto auctionFormalitySearchDto);
}
