package vn.compedia.website.auction.repository;


import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.auction.AssetSearchDto;
import vn.compedia.website.auction.entity.DashboardDoughnut;

import java.math.BigInteger;
import java.util.List;

public interface AssetRepositoryCustom {
    List<AssetDto> search(AssetSearchDto assetSearchDto);
    Long countSearch(AssetSearchDto assetSearchDto);
    List<AssetDto> searchForGuestUser(AssetSearchDto assetSearchDto);
    BigInteger countSearchForGuest(AssetSearchDto assetSearchDto);
    List<AssetDto> getAssetForFE(Long regulationId, Long accountId);
    AssetDto searchDetailForGuestUser(AssetSearchDto assetSearchDto);
    // asset processing
    AssetDto getAssetDtoByAssetId(AssetSearchDto assetSearchDto);
    // dashboardDoughnut
    DashboardDoughnut dashboard();
    List<AssetDto> searchForReportAuction(Long regulationId);
}
