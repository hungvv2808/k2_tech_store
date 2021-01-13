package vn.compedia.website.auction.system.task.formality;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.compedia.website.auction.model.Bid;
import vn.compedia.website.auction.model.SystemConfig;
import vn.compedia.website.auction.repository.BidRepository;
import vn.compedia.website.auction.service.AssetManagementService;
import vn.compedia.website.auction.service.PriceRoundService;
import vn.compedia.website.auction.system.socket.SocketUpdater;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.system.task.dto.Bargain;
import vn.compedia.website.auction.util.DbConstant;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class DirectUpTask {
    @Inject
    private SocketUpdater socketUpdater;
    @Inject
    private CommonTask commonTask;
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private AssetManagementService assetManagementService;
    @Autowired
    private PriceRoundService priceRoundService;

    public void processing(AssetDtoQueue assetDtoQueue, Map<Long, SystemConfig> systemConfigList) {
        Date now = new Date();
        // STOP round/auction
        if (!assetDtoQueue.isInTimeConfirm() && now.compareTo(assetDtoQueue.getEndTimeRound()) >= 0) {
            // update highest price
            priceRoundService.updateHighestPrice(assetDtoQueue.getAssetId(), assetDtoQueue.getCurrentRound());
            // bid last
            List<Long> accountDeposit = new ArrayList<>();
            if (assetDtoQueue.getAccountIdDepositList().isEmpty()) {
                accountDeposit.add(0L);
            } else {
                accountDeposit = new ArrayList<>(assetDtoQueue.getAccountIdDepositList());
            }
            List<Bid> bid = bidRepository.findAllByAssetIdAndAuctionRegisterIdNotInOrderByMoneyDescBidIdAsc(assetDtoQueue.getAssetId(), accountDeposit);

            Bargain bargainWinner = assetDtoQueue.getBargain(1);
            // auction failed
            if (bid.isEmpty() || bargainWinner == null) {
                assetDtoQueue.setWinner(null);
                log.info("Auction failed for asset = {}, reason = {}", assetDtoQueue.getAssetId(), bid.isEmpty() ? "There are not BID." : "The all-register were deposit or the winner 2th kd-dk.");
                assetManagementService.save(assetDtoQueue, DbConstant.ASSET_MANAGEMENT_ENDING_BAD);
                return;
            }

            // Set confirm
            commonTask.changeToConfirm(assetDtoQueue, systemConfigList);

            return;
        }

        // DURING waiting confirm
        if (assetDtoQueue.isInTimeConfirm()) {
            commonTask.processingConfirm(assetDtoQueue, systemConfigList);
        }
    }
}
