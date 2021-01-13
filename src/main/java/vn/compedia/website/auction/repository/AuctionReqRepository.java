package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.compedia.website.auction.model.AuctionReq;

import java.util.List;

public interface AuctionReqRepository extends CrudRepository<AuctionReq, Long>,AuctionReqRepositoryCustom {

    AuctionReq findAuctionReqByAuctionReqId(Long id);

    List<AuctionReq> findAuctionReqsByStatus(int status);

    @Query("select au.assetDescription from AuctionReq au where au.auctionReqId = :auctionReqId")
    String findMoTaByAuctionReqId(@Param("auctionReqId") Long auctionReqId);

    @Query("select au.accountId from AuctionReq au, Regulation re where au.auctionReqId = re.auctionReqId and re.regulationId = :regulationId")
    Long findAccountIdByRegulationId(@Param("regulationId") Long regulationId);

    Integer countByStatus(int status);

    @Query("select ac.roleId from AuctionReq au, Account ac where au.auctionReqId = :auctionReqId and au.createBy = ac.accountId")
    Integer getRoleIdAuctionReq(@Param("auctionReqId") Long auctionReqId);
}
