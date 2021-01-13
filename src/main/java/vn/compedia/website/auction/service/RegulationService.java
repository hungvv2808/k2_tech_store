package vn.compedia.website.auction.service;

import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.model.Regulation;

import java.util.List;

public interface RegulationService {
    void save(Regulation regulation, List<String> fileList, List<AssetDto> assetDtoList);
    void deleteRegulation(Long regulationId);
    void saveSingleFile(Regulation regulation, String fileName, String filePath, List<AssetDto> assetDtoList);
    void endedRegulation(Long regulationId, Integer auctionStatus);
}
