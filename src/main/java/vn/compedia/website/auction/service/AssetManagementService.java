package vn.compedia.website.auction.service;

import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;

public interface AssetManagementService {
    // end auction
    void save(AssetDtoQueue assetDtoQueue, boolean ending);
}
