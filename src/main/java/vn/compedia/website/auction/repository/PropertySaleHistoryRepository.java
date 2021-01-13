package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.AuctionReq;

public interface PropertySaleHistoryRepository extends CrudRepository<AuctionReq, Long>, PropertySaleHistoryRepositoryCustom {
}
