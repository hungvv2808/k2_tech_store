package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.compedia.website.auction.model.AssetManagement;

public interface AssetManagementRepository extends JpaRepository<AssetManagement,Long>,AssetManagementRepositoryCustom  {
    AssetManagement findAssetManagementByAssetId(Long assetId);
    AssetManagement findAssetManagementByAuctionRegisterIdAndEnding(Long auctionRegisterId, boolean ending);
}
