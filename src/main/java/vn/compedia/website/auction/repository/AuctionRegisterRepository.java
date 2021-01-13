package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.compedia.website.auction.dto.auction.AuctionRegisterDto;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.Asset;
import vn.compedia.website.auction.model.AuctionRegister;

import java.util.Date;
import java.util.List;

public interface AuctionRegisterRepository extends JpaRepository<AuctionRegister, Long>, AuctionRegisterRepositoryCustom {

    AuctionRegister findAuctionRegisterByAuctionRegisterId(Long auctionRegisterId);

    List<AuctionRegister> findAllByAssetId(Long assetId);

    List<AuctionRegister> findAllByAssetIdAndStatus(Long assetId, Integer status);

    List<AuctionRegister> findAllByAssetIdAndStatusNotIn(Long assetId, List<Integer> statusList);

    List<AuctionRegister> findAllByRegulationIdAndStatus(Long regulationId, Integer status);

    List<AuctionRegister> findAllByRegulationIdAndStatusDeposit(Long regulationId, Integer statusDeposit);

    List<AuctionRegister> findAllByRegulationId(Long regulationId);

    List<AuctionRegister> findAuctionRegistersByAccountId(Long accountId);

    AuctionRegister findAuctionRegisterByAssetIdAndAccountId(Long assetId, Long accountId);

    @Query("select new vn.compedia.website.auction.dto.auction.AuctionRegisterDto(ar.statusRefund, ar.status, ass.status) from AuctionRegister ar, Asset ass where ar.accountId = :accountId and ar.assetId =:assetId")
    AuctionRegisterDto getAuctionRegisterByAccountIdAndAssetId(@Param("accountId")Long accountId,@Param("assetId")Long assetId);

    boolean existsAuctionRegisterByAccountIdAndAssetId(Long accountId, Long assetId);

    AuctionRegister findAllByAuctionRegisterId(Long auctionRegisterId);

    List<AuctionRegister> findAuctionRegistersByStatusAndStatusRefund(int status,Integer statusRefund);

    @Query("select r.regulationId from AuctionRegister r ")
    List<Long> findregulationId();

    @Query("select count(ar.auctionRegisterId) from AuctionRegister ar left join Asset a on ar.assetId = a.assetId where (a.status = :status or a.status = :end) and ar.statusRefund = :statusRefund ")
    Long findCount(@Param("status") int status,@Param("end") int end,@Param("statusRefund") Integer statusRefund);

    @Query("SELECT a FROM AuctionRegister ar, Asset a WHERE ar.assetId = a.assetId AND ar.accountId = :accountId AND ar.status = :registerStatus AND a.status IN (:assetStatus)")
    List<Asset> getAllAssetByAccountId(@Param("accountId") Long accountId, @Param("registerStatus") Integer registerStatus, @Param("assetStatus") List<Integer> assetStatusList);

    @Query("SELECT a FROM AuctionRegister ar, Asset a WHERE ar.assetId = a.assetId AND ar.accountId = :accountId AND a.status IN (:assetStatus)")
    List<Asset> getAllAssetByAccountId(@Param("accountId") Long accountId, @Param("assetStatus") List<Integer> assetStatusList);

    @Query("select ac from Account ac, AuctionRegister ar where ac.accountId = ar.accountId and ar.auctionRegisterId = :auctionRegisterId")
    Account findAccountByAuctioneerId(@Param("auctionRegisterId") Long auctionRegisterId);

    @Query("select ac from Account ac, AuctionRegister ar where ac.accountId = ar.accountId and ar.auctionRegisterId = :auctionRegisterId")
    Account findAccountByAuctionRegisterId(@Param("auctionRegisterId") Long auctionRegisterId);

    @Query("SELECT acc FROM Account acc, AuctionRegister ar WHERE acc.accountId = ar.accountId AND ar.assetId = :assetId AND ar.status = :status")
    List<Account> findAccountListByAssetIdAndStatus(@Param("assetId") Long assetId, @Param("status") Integer status);

    @Transactional
    @Modifying
    @Query("UPDATE AuctionRegister SET statusRefuseWin = :statusRefuseWin WHERE assetId = :assetId AND accountId = :accountId")
    void updateStatusRefuseWin(Long assetId, Long accountId, boolean statusRefuseWin);

    @Transactional
    @Modifying
    @Query("UPDATE AuctionRegister SET statusJoined = :statusJoined WHERE assetId = :assetId AND accountId IN (:accountId)")
    void updateStatusJoined(Long assetId, List<Long> accountId, boolean statusJoined);

    @Transactional
    @Modifying
    @Query("UPDATE AuctionRegister SET timeJoined = :timeJoined WHERE assetId = :assetId AND accountId = :accountId")
    void updateTimeJoined(Long assetId, Long accountId, Date timeJoined);

    Integer countByStatusAndStatusRefund(int status, int statusRefund);

    @Query(
            "SELECT count(ar.auctionRegisterId) " +
            "FROM AuctionRegister ar " +
            "INNER JOIN Asset a ON ar.assetId = a.assetId " +
            "INNER JOIN Regulation r ON r.regulationId = ar.regulationId " +
            "LEFT JOIN AssetManagement am ON am.assetId = ar.assetId " +
            "WHERE a.status IN (:statusList) " +
                    "AND ar.statusRefund = :statusRefund " +
                    "AND ar.statusRefuseWin = :statusRefuseWin " +
                    "AND (am.assetManagementId IS NULL OR am.auctionRegisterId <> ar.auctionRegisterId OR am.ending = false) " +
                    "AND ar.auctionRegisterId NOT IN (SELECT b.auctionRegisterId FROM Bid b WHERE b.assetId = ar.assetId AND b.statusRetract = true) " +
                    "AND ar.filePath IS NULL " +
                    "AND r.auctionStatus NOT IN (:auctionStatus) "
    )
    Integer countByAssetStatusAndStatusRefund(
            @Param("statusList") List<Integer> statusList,
            @Param("statusRefund") Integer statusRefund,
            @Param("statusRefuseWin") boolean statusRefuseWin,
            @Param("auctionStatus") List<Integer> auctionStatus);

    @Query("select ar from AuctionRegister ar where ar.regulationId = :regulationId and ar.statusDeposit = 1 and ar.status = 1 group by ar.accountId")
    List<AuctionRegister> findAuctionRegistersByRegulationId(@Param("regulationId") Long regulationId);

    @Query("select ar from AuctionRegister ar where ar.assetId = :assetId and ar.statusDeposit = 1 and ar.status = 1 group by ar.accountId")
    List<AuctionRegister> findAuctionRegistersByAssetIdForCancel(@Param("assetId") Long assetId);

    @Query("select ar from AuctionRegister ar where ar.assetId = :assetId and ar.statusDeposit = 1 and ar.status in (0,1) group by ar.accountId")
    List<AuctionRegister> findAuctionRegistersByAssetIdForCancel1(@Param("assetId") Long assetId);

    @Query("select ar from  AuctionRegister ar where ar.assetId = :assetId and ar.status = 1 and ar.statusRefund IN (1,2,3) ")
    List<AuctionRegister> findAuctionRegistersByAssetId(@Param("assetId") Long assetId);

    @Query("select ar from  AuctionRegister ar inner join Regulation r on r.regulationId = ar.regulationId where ar.assetId = :assetId and r.auctionStatus in (1,3,4)")
    List<AuctionRegister> getAuctionRegistersByAssetId(@Param("assetId") Long assetId);

    Integer countByAuctionRegisterId(Long auctionRegisterId);
}
