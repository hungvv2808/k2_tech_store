package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.AuctionFormality;

import java.util.List;

public interface AuctionFormalityRepository extends CrudRepository<AuctionFormality, Long>, AuctionFormalityRepositoryCustom {
    AuctionFormality findByCode(String code);
    List<AuctionFormality> findAllByStatus(boolean status);
    AuctionFormality findAuctionFormalityByAuctionFormalityId(Long Id);
    @Query("select au from AuctionFormality au where au.auctionFormalityId = 1 or au.auctionFormalityId = 2 ")
    List<AuctionFormality> getAuctionFormalityByAuctionFormalityId();
}
