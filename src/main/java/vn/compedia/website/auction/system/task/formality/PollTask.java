package vn.compedia.website.auction.system.task.formality;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.compedia.website.auction.model.AuctionRegister;
import vn.compedia.website.auction.model.Bid;
import vn.compedia.website.auction.model.PriceRound;
import vn.compedia.website.auction.model.SystemConfig;
import vn.compedia.website.auction.repository.BidRepository;
import vn.compedia.website.auction.repository.PriceRoundRepository;
import vn.compedia.website.auction.service.AssetManagementService;
import vn.compedia.website.auction.service.AuctionRegisterService;
import vn.compedia.website.auction.service.PriceRoundService;
import vn.compedia.website.auction.system.socket.SocketUpdater;
import vn.compedia.website.auction.system.task.AuctionTask;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.system.task.helper.RedisHelper;
import vn.compedia.website.auction.system.util.SysConstant;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;

import javax.inject.Inject;
import javax.websocket.Session;
import java.util.*;

@Log4j2
@Component
public class PollTask {
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
    @Autowired
    private PriceRoundRepository priceRoundRepository;
    @Autowired
    private AuctionRegisterService auctionRegisterService;

    public void processing(AuctionTask auctionTask, AssetDtoQueue assetDtoQueue, Map<Long, SystemConfig> systemConfigList) {
        // STOP round/auction
        if (!assetDtoQueue.isInTimeConfirm() && new Date().compareTo(assetDtoQueue.getEndTimeRound()) >= 0) {
            assetDtoQueue.setLockBargain(true);
            // update highest price
            priceRoundService.updateHighestPrice(assetDtoQueue.getAssetId(), assetDtoQueue.getCurrentRound());
            // bid last
            Set<Long> accountDeposit = new HashSet<>();
            if (assetDtoQueue.getAccountIdDepositList().isEmpty()) {
                accountDeposit.add(0L);
            } else {
                accountDeposit = assetDtoQueue.getAccountIdDepositList();
            }
            List<Bid> bid = bidRepository.findAllByAssetIdAndAuctionRegisterIdNotInOrderByMoneyDescBidIdAsc(assetDtoQueue.getAssetId(), new ArrayList<>(accountDeposit));

            // auction failed
            if ((assetDtoQueue.getCurrentRound() == 1 && bid.isEmpty()) || assetDtoQueue.getWinner() == null) {
                log.info("Auction failed for asset = {}, reason = {}", assetDtoQueue.getAssetId(), bid.isEmpty() ? "There are no BID." : "The all-register were deposit.");
                assetManagementService.save(assetDtoQueue, DbConstant.ASSET_MANAGEMENT_ENDING_BAD);
                assetDtoQueue.setLockBargain(false);
                return;
            }

            // check current round is bad
            if (!bid.isEmpty()) {
                PriceRound priceRound = priceRoundRepository.findById(assetDtoQueue.getWinner().getPriceRoundId()).orElse(new PriceRound());
                // check all deposit
                boolean allDeposit = true;
                for (Long accountId : assetDtoQueue.getPriceBargained().keySet()) {
                    if (!assetDtoQueue.hasDeposition(accountId) || assetDtoQueue.hasDepositionBySystem(accountId)) {
                        allDeposit = false;
                        break;
                    }
                }
                // change to confirm
                if (priceRound.getNumberOfRound() < assetDtoQueue.getCurrentRound() || allDeposit) {
                    log.info("Auction success for asset = {}, reason = {}", assetDtoQueue.getAssetId(), "Result for prev round: " + priceRound.getNumberOfRound());
                    // Set confirm
                    commonTask.changeToConfirm(assetDtoQueue, systemConfigList);
                    assetDtoQueue.setLockBargain(false);
                    return;
                }
            }

            // create next round
            if (assetDtoQueue.getCurrentRound() < assetDtoQueue.getNumberOfRounds()) {
                // deposit
                int roundCurrent = assetDtoQueue.getCurrentRound();
                new Thread(() -> {
                    List<Long> accountBargained = new ArrayList<>(assetDtoQueue.getPriceBargained().keySet());
                    for (AuctionRegister auctionRegister : assetDtoQueue.getAuctionRegisterList()) {
                        if (!assetDtoQueue.hasDeposition(auctionRegister.getAccountId()) && !accountBargained.contains(auctionRegister.getAccountId())) {
                            // save to db
                            auctionRegisterService.depositBySystem(auctionRegister.getAuctionRegisterId(), "không tham gia trả giá một vòng (không trả giá vòng " + roundCurrent + ")");

                            assetDtoQueue.getAccountIdDepositSystem().add(auctionRegister.getAccountId());
                            //
                            auctionTask.onDeposition(assetDtoQueue.getAssetId(), auctionRegister.getAccountId());
                        }
                    }
                }).start();

                // create record
                assetDtoQueue.setCurrentRound(assetDtoQueue.getCurrentRound() + 1);
                PriceRound priceRound = priceRoundService.createNextRound(assetDtoQueue, assetDtoQueue.getCurrentRound());

                //
                assetDtoQueue.setPriceRoundCurrent(priceRound);
                assetDtoQueue.setEndTimeRound(DateUtil.plusMinute(new Date(), assetDtoQueue.getTimePerRound()));
                log.info("Changed to round: " + assetDtoQueue.getCurrentRound());
                // sync to client not deposit
                List<Long> accountDepositList = new ArrayList<>(assetDtoQueue.getAccountIdDepositList());
                List<Session> sessionList = assetDtoQueue.getSessionRegisterNotIn(accountDepositList);
                socketUpdater.updateScope(assetDtoQueue, SysConstant.ACTION_SHOW_POPUP_CHANGE_ROUND, sessionList);
                RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                        SysConstant.ACTION_SHOW_POPUP_CHANGE_ROUND,
                        accountDepositList,
                        true);
                // sync to client deposit
                List<Session> sessionDepositList = assetDtoQueue.getSessionList();
                socketUpdater.updateScope(assetDtoQueue, SysConstant.ACTION_UPDATE_SCOPE, sessionDepositList);
                RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_ASSET_CHANGE_ROUND, assetDtoQueue.getAssetId());
            } else {
                // Set confirm
                commonTask.changeToConfirm(assetDtoQueue, systemConfigList);
            }
            // reset list price
            assetDtoQueue.setPriceBargained(new TreeMap<>());
            assetDtoQueue.setLockBargain(false);
            return;
        }
        // DURING waiting confirm
        if (assetDtoQueue.isInTimeConfirm()) {
            commonTask.processingConfirm(assetDtoQueue, systemConfigList);
        }
    }
}
