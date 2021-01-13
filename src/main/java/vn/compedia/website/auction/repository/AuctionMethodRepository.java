package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.AuctionMethod;

import java.util.List;

public interface AuctionMethodRepository extends CrudRepository<AuctionMethod, Long>, AuctionMethodRepositoryCustom {
   AuctionMethod findByCode(String code);
   List<AuctionMethod> findAllByStatus(boolean status);
   AuctionMethod findAuctionMethodByAuctionMethodId(Long Id);
   @Query("select au from AuctionMethod au where au.auctionMethodId = 1 or au.auctionMethodId = 2 ")
   List<AuctionMethod> getAuctionMethodByAuctionMethodId();
}
