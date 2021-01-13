package vn.compedia.website.auction.service;

import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.system.task.dto.RegulationDtoQueue;

import java.util.List;
import java.util.Queue;

public interface AssetService {
    Queue<RegulationDtoQueue> findAllByCurrentTime();
    void save(List<AssetDto> assetDtoList);
}
