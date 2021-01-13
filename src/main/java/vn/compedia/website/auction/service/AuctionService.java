package vn.compedia.website.auction.service;

import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;

public interface AuctionService {
    //start an auction
    void setupStart(AssetDtoQueue assetDtoQueue);
}
