package vn.compedia.website.auction.system.task.formality;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.compedia.website.auction.service.AssetManagementService;
import vn.compedia.website.auction.system.socket.SocketUpdater;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.system.task.helper.RedisHelper;
import vn.compedia.website.auction.system.util.SysConstant;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Log4j2
@Component
public class DirectDownTask {
    @Inject
    private SocketUpdater socketUpdater;
    @Autowired
    private AssetManagementService assetManagementService;

    public void processing(AssetDtoQueue assetDtoQueue) {
        if (assetDtoQueue.isInTimeConfirm()) {
            if (!assetDtoQueue.isRefuseWinner()) {
                // auction success
                if (assetDtoQueue.isAcceptWinner() || new Date().compareTo(assetDtoQueue.endTimeAcceptPrice()) >= 0) {
                    log.info("[AUCTION] asset success: {}", assetDtoQueue.getAssetId());
                    assetManagementService.save(assetDtoQueue, DbConstant.ASSET_MANAGEMENT_ENDING_GOOD);
                    // sync to loser
                    List<Long> accountIdList = new ArrayList<>();
                    accountIdList.add(assetDtoQueue.getWinner().getAccountId());
                    accountIdList.addAll(assetDtoQueue.getAccountIdDepositList());
                    socketUpdater.updateScope(assetDtoQueue,
                            SysConstant.ACTION_SHOW_POPUP_AUCTION_LOST,
                            assetDtoQueue.getSessionRegisterNotIn(accountIdList));
                    RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                            SysConstant.ACTION_SHOW_POPUP_AUCTION_LOST,
                            accountIdList,
                            true);
                }
            } else {
                // auction continue
                Long accountId = assetDtoQueue.getAccountIdWinner();
                assetDtoQueue.setEndTimeRound(getEndTime(assetDtoQueue));
                assetDtoQueue.setWinner(null);
                assetDtoQueue.setRefuseWinner(false);
                assetDtoQueue.setInTimeConfirm(false);
                new Thread(() -> {
                    try {
                        Thread.sleep(100);
                        // sync to other
                        List<Long> accountIdList = new ArrayList<>();
                        accountIdList.add(accountId);
                        accountIdList.addAll(assetDtoQueue.getAccountIdDepositList());
                        socketUpdater.updateScope(assetDtoQueue,
                                SysConstant.ACTION_SHOW_POPUP_AUCTION_OPPORTUNITY_D1,
                                assetDtoQueue.getSessionRegisterNotIn(accountIdList));
                        RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                                SysConstant.ACTION_SHOW_POPUP_AUCTION_OPPORTUNITY_D1,
                                accountIdList,
                                true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                // sync to client
                socketUpdater.updateScope(assetDtoQueue);
                RedisHelper.syncSocket(assetDtoQueue.getAssetId(), SysConstant.ACTION_UPDATE_SCOPE, new ArrayList<>(), true);
                // sync to winner
                socketUpdater.showDeposit(assetDtoQueue, accountId);
                RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                        SysConstant.ACTION_SHOW_MODAL_DEPOSIT,
                        Collections.singletonList(accountId),
                        false);
            }
        } else {
            if (new Date().compareTo(assetDtoQueue.getEndTimeRound()) >= 0) {
                if (assetDtoQueue.getCurrentRound() <= assetDtoQueue.getRoundTotalDown()) {
                    // next round
                    assetDtoQueue.setStartTimeRound(new Date());
                    assetDtoQueue.setEndTimeRound(DateUtil.plusMinute(
                            assetDtoQueue.getStartTimeRound(),
                            assetDtoQueue.getTimePerRound())
                    );
                    assetDtoQueue.setCurrentRound(assetDtoQueue.getCurrentRound() + 1);
                    assetDtoQueue.setCurrentPrice(assetDtoQueue.getCurrentPrice() - assetDtoQueue.getPriceStep());
                    // sync to client
                    socketUpdater.updateScope(assetDtoQueue);
                    RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                            SysConstant.ACTION_UPDATE_SCOPE,
                            new ArrayList<>(),
                            true);
                } else {
                    // end asset
                    assetManagementService.save(assetDtoQueue, DbConstant.ASSET_MANAGEMENT_ENDING_BAD);
                }
            }
        }
    }

    private Date getEndTime(AssetDtoQueue assetDtoQueue) {
        long timeNotProcessed = assetDtoQueue.getEndTimeRound().getTime() - assetDtoQueue.getTimeAcceptPrice().getTime();
        return new Date(new Date().getTime() + timeNotProcessed);
    }
}
