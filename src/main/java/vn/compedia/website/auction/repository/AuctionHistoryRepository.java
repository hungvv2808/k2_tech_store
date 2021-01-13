package vn.compedia.website.auction.repository;


import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.AuctionRegister;

public interface AuctionHistoryRepository extends CrudRepository<AuctionRegister, Long>, AuctionHistoryRepositoryCustom {

}
