package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.AuctionRequest;

public interface AuctionRequestRepository extends CrudRepository<AuctionRequest, Long>, AuctionRequestRepositoryCustom {
}
