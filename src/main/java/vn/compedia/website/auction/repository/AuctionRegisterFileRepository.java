package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.AuctionRegisterFile;

public interface AuctionRegisterFileRepository extends CrudRepository<AuctionRegisterFile, Long>, AuctionRegisterFileRepositoryCustom {

}
