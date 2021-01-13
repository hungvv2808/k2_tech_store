package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.PriceRound;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;

public interface PriceRoundService {
    // update highest price
    void updateHighestPrice(Long assetId, Integer numberOfRound);
    // create next round
    PriceRound createNextRound(AssetDtoQueue assetDtoQueue, Integer round);
}
