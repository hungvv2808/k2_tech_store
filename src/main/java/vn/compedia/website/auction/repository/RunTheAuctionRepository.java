package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.Regulation;

public interface RunTheAuctionRepository extends CrudRepository<Regulation, Long>, RunTheAuctionRepositoryCustom {

}
