package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.auction.AssetSearchDto;
import vn.compedia.website.auction.dto.report.ReportAssetDto;

import java.util.List;

public interface ReportRepository {
    List<ReportAssetDto> search(AssetSearchDto searchDto);
    Long countSearch(AssetSearchDto searchDto);
    List<ReportAssetDto> searchForExport(AssetSearchDto searchDto);
}
