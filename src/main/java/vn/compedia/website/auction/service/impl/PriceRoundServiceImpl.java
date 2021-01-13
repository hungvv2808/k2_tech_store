package vn.compedia.website.auction.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.compedia.website.auction.model.Bid;
import vn.compedia.website.auction.model.PriceRound;
import vn.compedia.website.auction.repository.BidRepository;
import vn.compedia.website.auction.repository.PriceRoundRepository;
import vn.compedia.website.auction.service.PriceRoundService;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;

import java.util.Date;

@Service
public class PriceRoundServiceImpl implements PriceRoundService {
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private PriceRoundRepository priceRoundRepository;

    @Override
    @Transactional
    public void updateHighestPrice(Long assetId, Integer numberOfRound) {
        Bid bidHighestPrice = bidRepository.findFirstByAssetIdOrderByMoneyDescBidIdAsc(assetId);
        if (bidHighestPrice != null) {
            priceRoundRepository.updateHighestPrice(bidHighestPrice.getPriceRoundId(), bidHighestPrice.getAuctionRegisterId(), bidHighestPrice.getMoney(), new Date());
        }
    }

    @Override
    @Transactional
    public PriceRound createNextRound(AssetDtoQueue assetDtoQueue, Integer round) {
        PriceRound priceRound = new PriceRound();
        priceRound.setAssetId(assetDtoQueue.getAssetId());
        priceRound.setRegulationId(assetDtoQueue.getRegulationId());
        if (round == 1) {
            priceRound.setStartingPrice(assetDtoQueue.getCurrentPrice());
        } else {
            priceRound.setStartingPrice(assetDtoQueue.getWinner().getMoney() + assetDtoQueue.getPriceStep());
        }
        priceRound.setHighestPrice(priceRound.getStartingPrice());
        priceRound.setNumberOfRound(round);
        return priceRoundRepository.saveAndFlush(priceRound);
    }
}
