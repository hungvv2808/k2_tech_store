package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.compedia.website.auction.dto.auction.BidDto;
import vn.compedia.website.auction.model.Bid;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long>, BidRepositoryCustom {
    @Query("select new vn.compedia.website.auction.dto.auction.BidDto(b.time, b.bidId, b.priceRoundId, b.auctionRegisterId, " +
            "b.assetId, b.money, ac.fullName, ar.code) from Bid b, Account ac, AuctionRegister ar where b.auctionRegisterId = " +
            "ar.auctionRegisterId and ar.accountId = ac.accountId and b.priceRoundId=:priceRoundId order by b.money asc ")
    List<BidDto> loadAllBidDtoByPriceRoundId(@Param("priceRoundId") Long priceRoundId);

    @Query("select new vn.compedia.website.auction.dto.auction.BidDto(b.createDate, b.bidId, b.priceRoundId, b.auctionRegisterId, " +
            "b.assetId, b.money, ac.fullName, b.winnerSn) from Bid b, Account ac, AuctionRegister ar where b.auctionRegisterId = ar.auctionRegisterId" +
            " and ar.accountId = ac.accountId and b.assetId = ar.assetId and ar.statusDeposit = 1 and b.assetId = :assetId order by b.priceRoundId desc, b.money desc, b.createDate asc")
    List<BidDto> findBidsByAssetId(@Param("assetId") Long assetId);

    @Query("select new vn.compedia.website.auction.dto.auction.BidDto(b.createDate, b.bidId, b.priceRoundId, b.auctionRegisterId, " +
            "b.assetId, b.money, case when ac.org = true then ac.orgName else ac.fullName end, b.winnerSn) from Bid b, Account ac, AuctionRegister ar where b.auctionRegisterId = ar.auctionRegisterId" +
            " and ar.accountId = ac.accountId and b.assetId = ar.assetId and ar.statusDeposit = 1 and ar.statusRefuseWin = false and b.winnerNth is not null and b.assetId = :assetId order by b.winnerNth")
    List<BidDto> loadBidWinnerByAssetId(@Param("assetId") Long assetId);

    Bid findFirstByAssetIdOrderByMoneyDescBidIdAsc(Long assetId);
    List<Bid> findAllByAssetIdAndAuctionRegisterIdNotInOrderByMoneyDescBidIdAsc(Long assetId, List<Long> auctionRegisterId);
    List<Bid> findAllByAssetId(Long assetId);
    List<Bid> findBidsByAssetIdAndAuctionRegisterIdOrderByBidIdAsc(Long assetId, Long auctionRegisterId);
    @Transactional
    void deleteAllByAssetId(Long assetId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE bid b SET b.status_retract = :statusRetract " +
            "WHERE b.asset_id = :assetId " +
            "AND b.auction_register_id = :auctionRegisterId " +
            "AND b.bid_id = (SELECT * FROM (SELECT b1.bid_id FROM bid b1 WHERE b1.auction_register_id = :auctionRegisterId AND b1.asset_id = :assetId ORDER BY b1.bid_id DESC LIMIT 1) T)", nativeQuery = true)
    void changeStatus(@Param("statusRetract") boolean statusRetract, @Param("assetId") Long assetId, @Param("auctionRegisterId") Long auctionRegisterId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE bid b SET b.winner_sn = :winnerSn WHERE b.money = :money AND b.auction_register_id = :auctionRegisterId", nativeQuery = true)
    void updateWinnerSn(@Param("money") Long money, @Param("auctionRegisterId") Long auctionRegisterId, @Param("winnerSn") Integer winnerSn);

    @Transactional
    @Modifying
    @Query(value = "UPDATE bid b SET b.winner_nth = :winnerNth WHERE b.money = :money AND b.auction_register_id IN (:auctionRegisterIdList)", nativeQuery = true)
    void updateWinnerNth(@Param("money") Long money, @Param("auctionRegisterIdList") List<Long> auctionRegisterIdList, @Param("winnerNth") Integer winnerNth);

}
