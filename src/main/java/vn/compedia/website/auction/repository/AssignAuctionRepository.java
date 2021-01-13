package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.dto.assign_auction.AssignAuctionDto;
import vn.compedia.website.auction.model.Regulation;

import java.util.Date;
import java.util.List;

public interface AssignAuctionRepository extends CrudRepository<Regulation, Long>, AssignAuctionRepositoryCustom {
    List<Regulation> findAllByAuctioneerIdAndAuctionStatusInAndStartTimeIsBetween(Long auctioneerId, List<Integer> auctionStatus, Date startTime, Date endTime);
    List<Regulation> findAllByStatus(Integer status);
}
