package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.model.Asset;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long>, AssetRepositoryCustom {

    @Query("select ass from Asset ass, AuctionRegister ar where ass.regulationId = :regulationId and ass.assetId = ar.assetId and ar.accountId = :accountId")
    List<Asset> findAssetsByRegulationId(@Param("regulationId") Long regulationId, @Param("accountId") Long accountId);

    List<Asset> findAssetsByRegulationId(Long regulationId);

    @Query("select ass from Asset ass where ass.regulationId = (select a.regulationId from Asset a where a.assetId = :assetId)")
    List<Asset> findAssetsByAssetId(Long assetId);

    List<Asset> findAssetsByRegulationIdAndStatus(Long regulationId, int status);

    @Query("select ass from Asset ass where ass.regulationId = :regulationId and ass.status in (:statusList)")
    List<Asset> findAssetsByRegulationIdAndStatusList(@Param("regulationId") Long regulationId, @Param("statusList") int[] statusList);

    Asset findAssetByAssetId(Long assetId);

    List<Asset> getAllByRegulationId(Long regulationId);

    @Query("select new vn.compedia.website.auction.dto.auction.AssetDto(r.assetId, r.regulationId, r.name, r.startingPrice," +
                     "r.priceStep, r.description, r.deposit, r.status, r.minPrice, r.numericalOrder,r.startTime,rg.startRegistrationDate,r.typeAssetId) from Asset r ,Regulation rg where r.regulationId = rg.regulationId and r.assetId = :assetId")
    AssetDto findAssetByAssetIdWithParam(@Param("assetId") Long assetId);

    @Query("select new vn.compedia.website.auction.dto.auction.AssetDto(r.assetId, r.regulationId, r.name, r.startingPrice, "+
            "r.priceStep, r.description, r.deposit, r.status, r.minPrice, r.numericalOrder,r.startTime,r.typeAssetId, ty.name) from Asset r , TypeAsset ty where r.typeAssetId=ty.typeAssetId and r.regulationId = :regulationId order by r.assetId")
    List<AssetDto> getByRegulationId(@Param("regulationId") Long regulationId);

    @Query("SELECT a FROM Asset a INNER JOIN Regulation r ON a.regulationId = r.regulationId WHERE r.auctioneerId = :auctioneerId AND a.status IN (:assetStatus)")
    List<Asset> getAssetByAuctioneerIdAndAssetStatus(@Param("auctioneerId") Long auctioneerId, @Param("assetStatus") List<Integer> assetStatus);

    @Query("SELECT new vn.compedia.website.auction.dto.auction.AssetDto(a.assetId,a.name,r.code) FROM Asset a INNER JOIN Regulation r ON a.regulationId = r.regulationId WHERE r.status <> 0 order by a.assetId desc ")
    List<AssetDto> getAllName();

    @Transactional
    @Modifying
    @Query("UPDATE Asset a SET a.status = :status, a.cancelPlaying = :cancelPlaying WHERE a.assetId = :assetId")
    void changeStatus(@Param("assetId") Long assetId, @Param("status") Integer status, @Param("cancelPlaying") boolean cancelPlaying);

    @Transactional
    @Modifying
    @Query("UPDATE Asset a SET a.status = :status WHERE a.assetId = :assetId")
    void changeStatus(@Param("assetId") Long assetId, @Param("status") Integer status);

    @Transactional
    @Modifying
    @Query("UPDATE Asset a SET a.status = :status, a.startTime = :startTime WHERE a.assetId = :assetId")
    void changeStatusAndStartTime(@Param("assetId") Long assetId, @Param("status") Integer status, @Param("startTime") Date startTime);

    @Query("SELECT new vn.compedia.website.auction.dto.auction.AssetDto(a.assetId, a.status, am.ending, a.startTime) from Asset a left join AssetManagement am on a.assetId = am.assetId")
    List<AssetDto> findEndedStatus();

    @Query("SELECT a.name from Asset a where a.assetId = :assetId")
    String getNameByAssetId(@Param("assetId") Long id);

    @Query("select r.typeAssetId from Asset r ")
    List<Long> findTypeAssetId();

    @Query("select ar.auctionRegisterId from Asset a, AuctionRegister ar where a.assetId = ar.assetId and a.regulationId = ar.regulationId and a.assetId = :assetId and a.regulationId = :regulationId and ar.accountId = :accountId")
    Long getAuctionRegisterIdByAsset(@Param("assetId") Long assetId, @Param("regulationId") Long regulationId, @Param("accountId") Long accountId);

    @Query("select a.deposit from Asset a where a.assetId = :assetId")
    Long getDepositByAssetId(@Param("assetId") Long assetId);
}
