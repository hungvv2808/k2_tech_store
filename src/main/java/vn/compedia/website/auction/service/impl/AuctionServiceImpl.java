package vn.compedia.website.auction.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.compedia.website.auction.model.Regulation;
import vn.compedia.website.auction.repository.AssetRepository;
import vn.compedia.website.auction.repository.RegulationRepository;
import vn.compedia.website.auction.service.AuctionService;
import vn.compedia.website.auction.service.PriceRoundService;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.util.DbConstant;

import java.util.Date;

@Service
public class AuctionServiceImpl implements AuctionService {
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private RegulationRepository regulationRepository;
    @Autowired
    private PriceRoundService priceRoundService;

    @Override
    @Transactional
    public void setupStart(AssetDtoQueue assetDtoQueue) {
        //change regulation
        if (assetDtoQueue.isFirstAsset()) {
            Regulation regulation = regulationRepository.findById(assetDtoQueue.getRegulationId()).orElse(null);
            if (regulation != null) {
                regulation.setAuctionStatus(DbConstant.REGULATION_AUCTION_STATUS_PLAYING);
                regulation.setRealStartTime(new Date());
                regulationRepository.saveAndFlush(regulation);
            }
        }
        //change status asset to active
        assetRepository.changeStatusAndStartTime(assetDtoQueue.getAssetId(), DbConstant.ASSET_STATUS_PLAYING, new Date());
        //create first record for price_round table
        assetDtoQueue.setCurrentPrice(assetDtoQueue.getStartingPrice());
        assetDtoQueue.setPriceRoundCurrent(priceRoundService.createNextRound(assetDtoQueue, 1)); //the first round is 1
    }
}
