package vn.compedia.website.auction.system.task.formality;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.compedia.website.auction.model.SystemConfig;
import vn.compedia.website.auction.repository.BidRepository;
import vn.compedia.website.auction.service.AssetManagementService;
import vn.compedia.website.auction.system.socket.SocketUpdater;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.system.task.dto.Bargain;
import vn.compedia.website.auction.system.task.helper.RedisHelper;
import vn.compedia.website.auction.system.util.SysConstant;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;

import javax.inject.Inject;
import javax.inject.Named;
import javax.websocket.Session;
import java.util.*;

@Log4j2
@Named
@Getter
@Component
public class CommonTask {
    @Inject
    private SocketUpdater socketUpdater;
    @Autowired
    private AssetManagementService assetManagementService;
    @Autowired
    private BidRepository bidRepository;

    @Value("${auction.random.time}")
    private Long timeRandom;
    private final int TH0 = 0; // 0-1 - round 2
    private final int TH1 = 1; // 0-n - make random - 1 time
    private final int TH2 = 2; // 1-1
    private final int TH3 = 3; // 1-0
    private final int TH4 = 4; // 1-n - make random - 1 time
    private final int TH5 = 5; // n-n - make random - maybe 2 times

    public void processingConfirm(AssetDtoQueue assetDtoQueue, Map<Long, SystemConfig> systemConfigList) {
        Date now = new Date();
        // if in time random

        // refuse
        if (assetDtoQueue.isRefuseWinner()) {
            // refuse confirm 1th
            if (assetDtoQueue.getRoundConfirm() == 1) {
                Map<Long, Bargain> bargain1th = assetDtoQueue.getAccountIdWinner1thList();
                Map<Long, Bargain> bargain2th = assetDtoQueue.getAccountIdWinner2thList();

                // bad
                if (Arrays.asList(TH0, TH1, TH3).contains(assetDtoQueue.getRandomCase())) {
                    assetManagementService.save(assetDtoQueue, DbConstant.ASSET_MANAGEMENT_ENDING_BAD);
                    return;
                }

                // change round
                Bargain bargain = new Bargain();
                if (assetDtoQueue.getRandomCase() == TH2) {
                    bargain = new ArrayList<>(bargain2th.values()).get(0);
                }
                if (assetDtoQueue.getRandomCase() == TH4) {
                    bargain = randomWinner(bargain2th);
                }
                if (assetDtoQueue.getRandomCase() == TH5 && bargain1th.size() == 2) {
                    for (Bargain bg : bargain1th.values()) {
                        if (!bg.getAccountId().equals(assetDtoQueue.getWinner1th().getAccountId())) {
                            bargain = bg;
                            break;
                        }
                    }
                }
                if (assetDtoQueue.getRandomCase() == TH5 && bargain1th.size() > 2) {
                    do {
                        bargain = randomWinner(bargain1th);
                    } while (bargain.getAccountId().equals(assetDtoQueue.getWinner1th().getAccountId()));
                }

                //
                assetDtoQueue.setTimeConfirmMinute(systemConfigList.get(DbConstant.SYSTEM_CONFIG_ID_WAITING_2TH).getValue().intValue());
                Date endTimeRound = DateUtil.plusMinute(now, assetDtoQueue.getTimeConfirmMinute());
                // make random
                if (assetDtoQueue.getRandomCase() == TH4
                        || (assetDtoQueue.getRandomCase() == TH5 && bargain1th.size() > 2)) {
                    endTimeRound = DateUtil.plusSecond(endTimeRound, timeRandom);
                    processingRandom(assetDtoQueue);
                }

                // update winner 2th in bid
                bidRepository.updateWinnerSn(bargain.getMoney(), bargain.getAuctionRegisterId(), DbConstant.BID_WINNER_SN_2TH);

                assetDtoQueue.setRoundConfirm(2);
                assetDtoQueue.setWinner(bargain);
                assetDtoQueue.setRefuseWinner(false);
                assetDtoQueue.setEndTimeRound(endTimeRound);

                // update scope
                if (!assetDtoQueue.isInTimeRandom()) {
                    // update winner
                    socketUpdater.updateScope(assetDtoQueue,
                            SysConstant.ACTION_SHOW_POPUP_AUCTION_WINNER,
                            assetDtoQueue.getSessionsByAccountId(bargain.getAccountId())
                    );
                    RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                            SysConstant.ACTION_SHOW_POPUP_AUCTION_WINNER,
                            Collections.singletonList(bargain.getAccountId()),
                            false);

                    List<Long> accountIdLost = new ArrayList<>(assetDtoQueue.getAccountOpportunityList().keySet());
                    accountIdLost.remove(bargain.getAccountId());
                    accountIdLost.remove(assetDtoQueue.getWinner().getAccountId());
                    if (!accountIdLost.isEmpty()) {
                        List<Session> sessionList = assetDtoQueue.getSessionsByAccountIdIn(accountIdLost);
                        if (TH2 != assetDtoQueue.getRandomCase()) {
                            // update lost
                            socketUpdater.updateScope(assetDtoQueue,
                                    SysConstant.ACTION_SHOW_POPUP_AUCTION_LOST,
                                    sessionList
                            );
                            RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                                    SysConstant.ACTION_SHOW_POPUP_AUCTION_LOST,
                                    accountIdLost,
                                    false);
                        } else {
                            socketUpdater.updateScope(assetDtoQueue,
                                    SysConstant.ACTION_UPDATE_SCOPE,
                                    sessionList
                            );
                            RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                                    SysConstant.ACTION_UPDATE_SCOPE,
                                    accountIdLost,
                                    false);
                        }
                    }
                } else {
                    // random
                    List<Long> accountOpportunity = new ArrayList<>(assetDtoQueue.getAccountOpportunityList().keySet());
                    List<Session> sessionList = assetDtoQueue.getSessionsByAccountIdIn(accountOpportunity);
                    socketUpdater.updateScope(assetDtoQueue, SysConstant.ACTION_SHOW_POPUP_AUCTION_RANDOM, sessionList);
                    RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                            SysConstant.ACTION_SHOW_POPUP_AUCTION_RANDOM,
                            accountOpportunity,
                            false);
                    bidRepository.updateWinnerNth(
                            new ArrayList<>(assetDtoQueue.getAccountOpportunityList().values()).get(0).getMoney(),
                            assetDtoQueue.getAuctionRegisterIdByAccountId(accountOpportunity),
                            DbConstant.BID_WINNER_NTH_2TH);
                }

                // update not joined
                socketUpdater.updateScope(assetDtoQueue);
                RedisHelper.syncSocketAll(assetDtoQueue.getAssetId(), SysConstant.ACTION_UPDATE_SCOPE);

                return;
            }

            // refuse confirm 2th
            if (assetDtoQueue.getRoundConfirm() == 2) {
                assetDtoQueue.setInTimeConfirm(false);
                // create record
                assetManagementService.save(assetDtoQueue, DbConstant.ASSET_MANAGEMENT_ENDING_BAD);
            }
        } else {
            // STOP random time
            if (assetDtoQueue.isInTimeRandom() && now.after(assetDtoQueue.getEndTimeRandom())) {
                assetDtoQueue.setInTimeRandom(false);
                // update scope
                // winner
                updateWinner(assetDtoQueue);
                // opportunity
                if (assetDtoQueue.getRandomCase() == TH5) {
                    Set<Long> accountId = new LinkedHashSet<>(assetDtoQueue.getAccountIdWinner1thList().keySet());
                    accountId.remove(assetDtoQueue.getWinner().getAccountId());
                    accountId.remove(assetDtoQueue.getWinner1th().getAccountId());
                    List<Long> accountOpportunity = new ArrayList<>(accountId);
                    List<Session> sessionNotWinner = assetDtoQueue.getSessionsByAccountIdIn(accountOpportunity);
                    if (assetDtoQueue.getRoundConfirm() == 1) {
                        // opportunity
                        socketUpdater.updateScope(assetDtoQueue, SysConstant.ACTION_SHOW_POPUP_AUCTION_OPPORTUNITY2, sessionNotWinner);
                        RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                                SysConstant.ACTION_SHOW_POPUP_AUCTION_OPPORTUNITY2,
                                accountOpportunity,
                                false);
                    } else {
                        // lost
                        socketUpdater.updateScope(assetDtoQueue, SysConstant.ACTION_SHOW_POPUP_AUCTION_LOST, sessionNotWinner);
                        RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                                SysConstant.ACTION_SHOW_POPUP_AUCTION_LOST,
                                accountOpportunity,
                                false);
                    }
                }
                // lost
                if (Arrays.asList(TH1, TH4).contains(assetDtoQueue.getRandomCase())) {
                    Set<Long> accountId = new LinkedHashSet<>(assetDtoQueue.getAccountIdWinner2thList().keySet());
                    accountId.remove(assetDtoQueue.getWinner().getAccountId());
                    List<Long> accountDepositByAuctioneer = new ArrayList<>(assetDtoQueue.getAccountIdDepositList());
                    accountDepositByAuctioneer.removeAll(assetDtoQueue.getAccountIdDepositSystem());
                    accountId.removeAll(accountDepositByAuctioneer);
                    List<Session> sessionLost = assetDtoQueue.getSessionsByAccountIdIn(new ArrayList<>(accountId));
                    socketUpdater.updateScope(assetDtoQueue, SysConstant.ACTION_SHOW_POPUP_AUCTION_LOST, sessionLost);
                    RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                            SysConstant.ACTION_SHOW_POPUP_AUCTION_LOST,
                            new ArrayList<>(accountId),
                            false);
                }

                // update scope
                socketUpdater.updateScope(assetDtoQueue);
                RedisHelper.syncSocketAll(assetDtoQueue.getAssetId(), SysConstant.ACTION_UPDATE_SCOPE);

                return;
            }
            // STOP waiting confirm
            if (assetDtoQueue.isAcceptWinner() || now.after(assetDtoQueue.getEndTimeRound())) {
                // create record
                assetManagementService.save(assetDtoQueue, DbConstant.ASSET_MANAGEMENT_ENDING_GOOD);
                // update lost
                if (TH2 == assetDtoQueue.getRandomCase()
                        || (Arrays.asList(TH1, TH4, TH5).contains(assetDtoQueue.getRandomCase()) && assetDtoQueue.getRoundConfirm() == 1)) {
                    List<Long> accountList = new ArrayList<>(assetDtoQueue.getAccountOpportunityList().keySet());
                    accountList.remove(assetDtoQueue.getWinner().getAccountId());
                    List<Long> accountDepositByAuctioneer = new ArrayList<>(assetDtoQueue.getAccountIdDepositList());
                    accountDepositByAuctioneer.removeAll(assetDtoQueue.getAccountIdDepositSystem());
                    accountList.removeAll(accountDepositByAuctioneer);
                    List<Session> sessionLost = assetDtoQueue.getSessionsByAccountIdIn(accountList);
                    socketUpdater.updateScope(assetDtoQueue, SysConstant.ACTION_SHOW_POPUP_AUCTION_LOST, sessionLost);
                    RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                            SysConstant.ACTION_SHOW_POPUP_AUCTION_LOST,
                            accountList,
                            false);
                }
            }
        }
    }

    public void processingRandom(AssetDtoQueue assetDtoQueue) {
        assetDtoQueue.setEndTimeRandom(DateUtil.plusSecond(new Date(), timeRandom));
        assetDtoQueue.setInTimeRandom(true);
    }

    public void changeToConfirm(AssetDtoQueue assetDtoQueue, Map<Long, SystemConfig> systemConfigList) {
        log.info("Ended last round: " + assetDtoQueue.getCurrentRound());

        // add account winner to list
        List<Bargain> bargain1thList = assetDtoQueue.winner1th();
        List<Bargain> bargain2thList = assetDtoQueue.winner2th();
        bargain1thList.forEach(e -> {
            if (!assetDtoQueue.hasDeposition(e.getAccountId())
                    || (assetDtoQueue.hasDepositionBySystem(e.getAccountId()) && assetDtoQueue.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_POLL)) {
                assetDtoQueue.getAccountIdWinner1thList().put(e.getAccountId(), e);
            }
        });
        bargain2thList.forEach(e -> {
            if (assetDtoQueue.getAccountIdWinner1thList().get(e.getAccountId()) == null) {
                if (!assetDtoQueue.hasDeposition(e.getAccountId())
                        || (assetDtoQueue.hasDepositionBySystem(e.getAccountId()) && assetDtoQueue.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_POLL)) {
                    assetDtoQueue.getAccountIdWinner2thList().put(e.getAccountId(), e);
                }
            }
        });

        // check end
        if (checkEndingBad(assetDtoQueue)) {
            assetManagementService.save(assetDtoQueue, DbConstant.ASSET_MANAGEMENT_ENDING_BAD);
            return;
        }

        // Set confirm
        Map<Long, Bargain> bargain1th = assetDtoQueue.getAccountIdWinner1thList();
        Map<Long, Bargain> bargain2th = assetDtoQueue.getAccountIdWinner2thList();

        // delay to random register
        if (bargain1th.size() == 0 && bargain2th.size() == 1) {
            assetDtoQueue.setRandomCase(TH0);
        }
        if (bargain1th.size() == 0 && bargain2th.size() > 1) {
            assetDtoQueue.setRandomCase(TH1); //
        }
        if (bargain1th.size() == 1 && bargain2th.size() == 1) {
            assetDtoQueue.setRandomCase(TH2);
        }
        if (bargain1th.size() == 1 && bargain2th.isEmpty()) {
            assetDtoQueue.setRandomCase(TH3); //
        }
        if (bargain1th.size() == 1 && bargain2th.size() > 1) {
            assetDtoQueue.setRandomCase(TH4); //
        }
        if (bargain1th.size() > 1) {
            assetDtoQueue.setRandomCase(TH5); //
        }

        // set time confirm
        assetDtoQueue.setTimeConfirmMinute(systemConfigList.get(DbConstant.SYSTEM_CONFIG_ID_WAITING_1TH).getValue().intValue());
        Date endTimeRound = DateUtil.plusMinute(assetDtoQueue.getEndTimeRound(), assetDtoQueue.getTimeConfirmMinute());
        if (Arrays.asList(TH1, TH4, TH5).contains(assetDtoQueue.getRandomCase())) {
            endTimeRound = DateUtil.plusSecond(endTimeRound, timeRandom);
        }
        assetDtoQueue.setEndTimeRound(endTimeRound);
        assetDtoQueue.setInTimeConfirm(true);
        assetDtoQueue.setRefuseWinner(false);

        // set winner random
        Bargain bargain;
        if (bargain1th.isEmpty()) {
            bargain = randomWinner(bargain2th);
            assetDtoQueue.setRoundConfirm(2); // round confirm 2th is 2
        } else {
            bargain = randomWinner(bargain1th);
            assetDtoQueue.setRoundConfirm(1); // round confirm 1th is 1
        }
        assetDtoQueue.setWinner(bargain);
        assetDtoQueue.setWinner1th(bargain);

        // update winner 1th in bid
        bidRepository.updateWinnerSn(bargain.getMoney(), bargain.getAuctionRegisterId(), DbConstant.BID_WINNER_SN_1TH);

        // set list opportunity
        if (assetDtoQueue.getRandomCase() == TH5) {
            for (Bargain bargainTemp : assetDtoQueue.getAccountIdWinner1thList().values()) {
                if (bargain.getAccountId().equals(bargainTemp.getAccountId())) {
                    assetDtoQueue.getAccountOpportunityList().put(bargainTemp.getAccountId(), bargainTemp);
                }
            }
        }

        // set in time random
        List<Long> accountRandom = new ArrayList<>();
        if (Arrays.asList(TH1, TH5).contains(assetDtoQueue.getRandomCase())) {
            processingRandom(assetDtoQueue);
            if (assetDtoQueue.getRandomCase() == TH1) {
                accountRandom = new ArrayList<>(assetDtoQueue.getAccountIdWinner2thList().keySet());
            }
            if (assetDtoQueue.getRandomCase() == TH5) {
                accountRandom = new ArrayList<>(assetDtoQueue.getAccountIdWinner1thList().keySet());
            }
            List<Session> sessionList = assetDtoQueue.getSessionsByAccountIdIn(accountRandom);
            socketUpdater.updateScope(assetDtoQueue, SysConstant.ACTION_SHOW_POPUP_AUCTION_RANDOM, sessionList);
            RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                    SysConstant.ACTION_SHOW_POPUP_AUCTION_RANDOM,
                    accountRandom,
                    false);
        }

        // set opportunity list
        if (Arrays.asList(TH1, TH2, TH4).contains(assetDtoQueue.getRandomCase())) {
            assetDtoQueue.setAccountOpportunityList(new LinkedHashMap<>(assetDtoQueue.getAccountIdWinner2thList()));
            if (assetDtoQueue.getRandomCase() == TH1) {
                assetDtoQueue.getAccountOpportunityList().remove(bargain.getAccountId());
            }
        }

        // UPDATE SCOPE
        // winner
        if (Arrays.asList(TH0, TH2, TH3, TH4).contains(assetDtoQueue.getRandomCase())) {
            updateWinner(assetDtoQueue);
        }

        // opportunity
        Set<Long> accountOpportunity = new LinkedHashSet<>();
        Long money = 0L;
        if (Arrays.asList(TH2, TH4).contains(assetDtoQueue.getRandomCase())) {
            accountOpportunity = new LinkedHashSet<>(assetDtoQueue.getAccountIdWinner2thList().keySet());
            assetDtoQueue.setAccountOpportunityList(new LinkedHashMap<>(assetDtoQueue.getAccountIdWinner2thList()));
            List<Session> sessionOpportunity = assetDtoQueue.getSessionsByAccountIdIn(new ArrayList<>(accountOpportunity));
            socketUpdater.updateScope(assetDtoQueue, SysConstant.ACTION_SHOW_POPUP_AUCTION_OPPORTUNITY1, sessionOpportunity);
            RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                    SysConstant.ACTION_SHOW_POPUP_AUCTION_OPPORTUNITY1,
                    new ArrayList<>(accountOpportunity),
                    false);
        }
        if (assetDtoQueue.getRandomCase() == TH5) {
            accountOpportunity = new LinkedHashSet<>(assetDtoQueue.getAccountIdWinner1thList().keySet());
            // set opportunity list
            assetDtoQueue.setAccountOpportunityList(new LinkedHashMap<>(assetDtoQueue.getAccountIdWinner1thList()));
            assetDtoQueue.getAccountOpportunityList().remove(bargain.getAccountId());
        }
        if (!assetDtoQueue.getAccountOpportunityList().isEmpty()) {
            // update list opportunity
            bidRepository.updateWinnerNth(
                    new ArrayList<>(assetDtoQueue.getAccountOpportunityList().values()).get(0).getMoney(),
                    assetDtoQueue.getAuctionRegisterIdByAccountId(new ArrayList<>(assetDtoQueue.getAccountOpportunityList().keySet())),
                    DbConstant.BID_WINNER_NTH_1TH);
        }

        // not winner / lost
        Set<Long> accountLost = new LinkedHashSet<>(assetDtoQueue.getAccountIdBargained());
        accountLost.remove(bargain.getAccountId());
        accountLost.removeAll(accountRandom);
        accountLost.removeAll(accountOpportunity);
        for (Long accountId : assetDtoQueue.getAccountIdDepositList()) {
            if ((assetDtoQueue.hasDeposition(accountId) && !assetDtoQueue.hasDepositionBySystem(accountId))
                    || (assetDtoQueue.hasDepositionBySystem(accountId) && assetDtoQueue.getAuctionFormalityId() != DbConstant.AUCTION_FORMALITY_ID_POLL)) {
                accountLost.remove(accountId);
            }
        }
        List<Session> sessionNotWinner = assetDtoQueue.getSessionsByAccountIdIn(new ArrayList<>(accountLost));
        socketUpdater.updateScope(assetDtoQueue, SysConstant.ACTION_SHOW_POPUP_AUCTION_LOST, sessionNotWinner);
        RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                SysConstant.ACTION_SHOW_POPUP_AUCTION_LOST,
                new ArrayList<>(accountLost),
                false);

        // not joined
        socketUpdater.updateScope(assetDtoQueue);
        RedisHelper.syncSocketAll(assetDtoQueue.getAssetId(), SysConstant.ACTION_UPDATE_SCOPE);
    }

    public Bargain randomWinner(Map<Long, Bargain> accountWinnerList) {
        Random random = new Random();
        List<Long> keys = new ArrayList<>(accountWinnerList.keySet());
        Long accountId = keys.get(random.nextInt(keys.size()));
        return accountWinnerList.get(accountId);
    }

    public boolean checkEndingBad(AssetDtoQueue assetDtoQueue) {
        return assetDtoQueue.getAccountIdWinner1thList().isEmpty()
                && assetDtoQueue.getAccountIdWinner2thList().isEmpty();
    }

    public boolean hasOpportunity(AssetDtoQueue assetDtoQueue, Long accountId) {
        if (Arrays.asList(TH1, TH2, TH4).contains(assetDtoQueue.getRandomCase())) {
            return assetDtoQueue.getWinner1th() != null
                    && !assetDtoQueue.getWinner1th().getAccountId().equals(accountId)
                    && assetDtoQueue.getWinner() != null
                    && !assetDtoQueue.getWinner().getAccountId().equals(accountId)
                    && assetDtoQueue.getAccountIdWinner2thList().containsKey(accountId);
        }
        if (TH5 == assetDtoQueue.getRandomCase()) {
            return assetDtoQueue.getWinner1th() != null
                    && !assetDtoQueue.getWinner1th().getAccountId().equals(accountId)
                    && assetDtoQueue.getWinner() != null
                    && !assetDtoQueue.getWinner().getAccountId().equals(accountId)
                    && assetDtoQueue.getAccountIdWinner1thList().containsKey(accountId);
        }
        return false;
    }

    public boolean hasWinner(AssetDtoQueue assetDtoQueue, Long accountId) {
        return assetDtoQueue != null
                && !assetDtoQueue.isInTimeRandom()
                && assetDtoQueue.isInTimeConfirm()
                && !assetDtoQueue.isAcceptWinner()
                && assetDtoQueue.getWinner() != null
                && assetDtoQueue.getWinner().getAccountId().equals(accountId);
    }

    public boolean hasOpportunity1(AssetDtoQueue assetDtoQueue, Long accountId) {
        return assetDtoQueue != null
                && !assetDtoQueue.isInTimeRandom()
                && assetDtoQueue.getRandomCase() != null
                && Arrays.asList(TH2, TH4).contains(assetDtoQueue.getRandomCase())
                && hasOpportunity(assetDtoQueue, accountId);
    }

    public boolean hasOpportunity2(AssetDtoQueue assetDtoQueue, Long accountId) {
        return assetDtoQueue != null
                && !assetDtoQueue.isInTimeRandom()
                && assetDtoQueue.getRandomCase() != null
                && TH5 == assetDtoQueue.getRandomCase()
                && hasOpportunity(assetDtoQueue, accountId);
    }

    public int countOpportunity(AssetDtoQueue assetDtoQueue) {
        if (assetDtoQueue == null || assetDtoQueue.getRandomCase() == null) {
            return 0;
        }
        int index = 1;
        if (assetDtoQueue.getRoundConfirm() != null
                && !Arrays.asList(TH1, TH4).contains(assetDtoQueue.getRandomCase())) {
            index = assetDtoQueue.getRoundConfirm();
        }
        if (Arrays.asList(TH1, TH4).contains(assetDtoQueue.getRandomCase())) {
            return assetDtoQueue.getAccountIdWinner2thList().size() - index;
        }
        if (TH5 == assetDtoQueue.getRandomCase()) {
            return assetDtoQueue.getAccountIdWinner1thList().size() - index;
        }
        return 0;
    }

    public String refuseWinner(AssetDtoQueue assetDtoQueue) {
        if (assetDtoQueue != null && assetDtoQueue.getRandomCase() != null && assetDtoQueue.getRoundConfirm() != null) {
            if (assetDtoQueue.isDeposit1th()) {
                return " do người trúng đấu giá đã bị đấu giá viên truất quyền";
            }

            if (assetDtoQueue.isRefuseWinner1th()) {
                return " do người trúng đấu giá đã từ chối";
            }
        }

        return null;
    }

    private void updateWinner(AssetDtoQueue assetDtoQueue) {
        List<Session> sessionWinner = assetDtoQueue.getSessionsByAccountId(assetDtoQueue.getWinner().getAccountId());
        socketUpdater.updateScope(assetDtoQueue, SysConstant.ACTION_SHOW_POPUP_AUCTION_WINNER, sessionWinner);
        RedisHelper.syncSocket(assetDtoQueue.getAssetId(),
                SysConstant.ACTION_SHOW_POPUP_AUCTION_WINNER,
                Collections.singletonList(assetDtoQueue.getWinner().getAccountId()),
                false);
    }
}
