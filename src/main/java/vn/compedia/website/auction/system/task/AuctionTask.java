/*
 * Author: Thinh Hoang
 * Date: 09/2020
 * Company: Compedia Software
 * Email: thinhhv@compedia.vn
 * Personal Website: https://vnnib.com
 */

package vn.compedia.website.auction.system.task;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.auction.AssetSearchDto;
import vn.compedia.website.auction.dto.auction.BidSearchDto;
import vn.compedia.website.auction.exception.AuctionException;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.repository.AssetRepository;
import vn.compedia.website.auction.repository.AuctionRegisterRepository;
import vn.compedia.website.auction.repository.BidRepository;
import vn.compedia.website.auction.repository.RegulationRepository;
import vn.compedia.website.auction.service.AssetManagementService;
import vn.compedia.website.auction.service.AuctionRegisterService;
import vn.compedia.website.auction.service.AuctionService;
import vn.compedia.website.auction.service.RegulationService;
import vn.compedia.website.auction.system.socket.SocketUpdater;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.system.task.dto.Bargain;
import vn.compedia.website.auction.system.task.dto.RegulationDtoQueue;
import vn.compedia.website.auction.system.task.formality.DirectDownTask;
import vn.compedia.website.auction.system.task.formality.DirectUpTask;
import vn.compedia.website.auction.system.task.formality.PollTask;
import vn.compedia.website.auction.system.task.helper.RedisHelper;
import vn.compedia.website.auction.system.task.validation.AuctionValidate;
import vn.compedia.website.auction.system.util.SysConstant;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.EmailUtil;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.NumberFormat;
import java.util.*;

@Log4j2
@Named
@Component
public class AuctionTask {
    @Inject
    private PollTask pollTask;
    @Inject
    private DirectUpTask directUpTask;
    @Inject
    private DirectDownTask directDownTask;
    @Inject
    private AsyncTask asyncTask;
    @Inject
    private SocketUpdater socketUpdater;
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private RegulationRepository regulationRepository;
    @Autowired
    private RegulationService regulationService;
    @Autowired
    private AssetManagementService assetManagementService;
    @Autowired
    private AuctionRegisterService auctionRegisterService;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private BidRepository bidRepository;

    @Getter @Setter
    private Map<Long, SystemConfig> systemConfigList;
    @Getter @Setter
    private Map<Long, AssetDtoQueue> assetDtoList = new HashMap<>(); // assetId, AssetDtoQueue
    @Getter @Setter
    private Map<Long, RegulationDtoQueue> regulationDtoList = new HashMap<>(); // regulationId, AssetDtoQueue
    @Getter @Setter
    private Set<Long> assetIdCancelList = new LinkedHashSet<>();
    @Getter @Setter
    private List<Long> regulationIdCancelList = new ArrayList<>();

    @Value("${auction.price.max}")
    private String maxPriceBargain;
    @Value("${deploy.node.id}")
    private int nodeId;
    private final String ERROR_MESSAGE_HIDE = "Dữ liệu không đúng";

    // ACTIVATION THE AUCTION
    @Async("threadPoolTaskExecutor")
    public void activationAuction(RegulationDtoQueue obj) {
        // create thread to processing asset
        log.info("========PROCESSING auction = {}, sysConfig = {}", obj.getRegulationId(), Arrays.toString(systemConfigList.entrySet().toArray()));
        try {
            regulationDtoList.put(obj.getRegulationId(), obj);
            RegulationDtoQueue regulationDtoQueue = regulationDtoList.get(obj.getRegulationId());
            Queue<AssetDtoQueue> assetDtoQueues = regulationDtoQueue.getAssetDtoQueueList();
            boolean cancelRegulation = false;
            AssetDtoQueue assetDtoQueue;
            boolean cancelFirstAsset = false;

            while (!assetDtoQueues.isEmpty()) {

                assetDtoQueue = assetDtoQueues.peek();
                assetDtoList.putIfAbsent(assetDtoQueue.getAssetId(), assetDtoQueue);
                assetDtoQueue = assetDtoList.get(assetDtoQueue.getAssetId());
                assetDtoQueue.setNodeIdController(nodeId);
                RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_ASSET, assetDtoQueue);

                //
                if (cancelFirstAsset) {
                    assetDtoQueue.setFirstAsset(true);
                    cancelFirstAsset = false;
                }

                // check if cancel regulation
                if (assetDtoQueue.isCancelRegulation() || regulationIdCancelList.contains(assetDtoQueue.getRegulationId())) {
                    log.info("Auction failed for regulation = {}, reason = {}", assetDtoQueue.getRegulationId(), "The regulation has been canceled");
                    assetManagementService.save(assetDtoQueue, DbConstant.ASSET_MANAGEMENT_ENDING_BAD);
                    assetDtoQueue.setEnded(true);
                    regulationIdCancelList.remove(assetDtoQueue.getRegulationId());
                    cancelRegulation = true;
                    while (assetDtoQueues.peek() != null) {
                        assetDtoList.remove(assetDtoQueues.poll().getAssetId());
                    }
                    // sync to client
                    socketUpdater.updateScope(assetDtoQueue);
                    socketUpdater.updateScope(assetDtoQueue, SysConstant.ACTION_SHOW_POPUP_CANCEL_ASSET);
                    RedisHelper.syncSocket(assetDtoQueue.getAssetId(), SysConstant.ACTION_SHOW_POPUP_CANCEL_ASSET, new ArrayList<>(), true);
                    break;
                }

                if (assetIdCancelList.contains(assetDtoQueue.getAssetId())) {
                    log.info("Auction failed for asset = {}, reason = {}", assetDtoQueue.getAssetId(), "The asset has been canceled (before playing)");
                    // remove
                    assetIdCancelList.remove(assetDtoQueue.getAssetId());
                    assetDtoList.remove(assetDtoQueue.getAssetId());
                    cancelFirstAsset = assetDtoQueue.isFirstAsset();
                    assetDtoQueues.poll();
                    continue;
                }

                // check if cancel asset
                if (assetDtoQueue.isCancel()) {
                    log.info("Auction failed for asset = {}, reason = {}", assetDtoQueue.getAssetId(), "The asset has been canceled");
                    assetDtoQueue.setEnded(true);
                }

                if (new Date().after(assetDtoQueue.getStartTime())) {
                    // reload auction register
                    if (!assetDtoQueue.isInProcessing()) {
                        assetDtoQueue.setAuctionRegisterList(auctionRegisterRepository.findAllByAssetIdAndStatus(assetDtoQueue.getAssetId(), DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED));
                        for (AuctionRegister auctionRegister : assetDtoQueue.getAuctionRegisterList()) {
                            assetDtoQueue.getAccountIdNotifyList().add(auctionRegister.getAccountId());
                        }
                    }

                    if (assetDtoQueue.getAuctionRegisterList().size() < 1) {
                        log.info("Auction failed for asset = {}, reason = {}", assetDtoQueue.getAssetId(), "The asset no register.");
                        assetManagementService.save(assetDtoQueue, DbConstant.ASSET_MANAGEMENT_ENDING_BAD);
                        cancelFirstAsset = assetDtoQueue.isFirstAsset();
                    }
                }

                // remove asset ended
                if (assetDtoQueue.isEnded()) {
                    assetRepository.changeStatus(assetDtoQueue.getAssetId(), assetDtoQueue.isCancel() ? DbConstant.ASSET_STATUS_CANCELED : DbConstant.ASSET_STATUS_ENDED);

                    // remove asset in ram
                    executeEndedAsset(assetDtoQueue);

                    // remove other node
                    RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_ASSET_ENDED, assetDtoQueue.getAssetId());

                    // remove
                    assetDtoQueues.poll();
                    continue;
                }

                // start auction
                if (!assetDtoQueue.isInProcessing()) {
                    // waiting processing
                    if (new Date().after(assetDtoQueue.getStartTime())) {
                        start(assetDtoQueue);
                        continue;
                    }
                // continue
                } else {
                    processing(assetDtoQueue);
                }

                // sleep
                Thread.sleep(950);
            }
            // ending regulation
            regulationService.endedRegulation(
                    obj.getRegulationId(),
                    cancelRegulation ? DbConstant.REGULATION_AUCTION_STATUS_CANCELLED : DbConstant.REGULATION_AUCTION_STATUS_ENDED
            );
            // remove queue
            regulationDtoList.remove(obj.getRegulationId());

            log.info("========ENDED auction = {}", obj);
        } catch (InterruptedException e) {
            log.error("[AUCTION PROCESSING] cause error: ", e);
            e.printStackTrace();
        }
    }

    public void start(AssetDtoQueue assetDtoQueue) throws InterruptedException {

        // change database
        log.info("Changing status for assetId = {}", assetDtoQueue.getAssetId());
        auctionService.setupStart(assetDtoQueue);

        // Sync to client
        new Thread(() -> {
            asyncTask.updateScope(assetDtoQueue, true);
        }).start();

        // put to list asset
        AssetSearchDto assetSearchDto = new AssetSearchDto();
        assetSearchDto.setAssetId(assetDtoQueue.getAssetId());
        AssetDto assetDto = assetRepository.getAssetDtoByAssetId(assetSearchDto);
        assetDtoQueue.setStartTime(new Date());
        assetDtoQueue.setRealStartTime(new Date());
        assetDtoQueue.setStartTimeRound(assetDto.getStartTime());
        assetDtoQueue.setCurrentRound(1);
        assetDtoQueue.setCurrentPrice(assetDto.getStartingPrice());
        assetDtoQueue.setAuctionFormalityId(assetDto.getAuctionFormalityId());
        assetDtoQueue.setAuctionMethodId(assetDto.getAuctionMethodId());
        assetDtoQueue.setEndTimeRound(DateUtil.plusMinute(assetDtoQueue.getStartTime(), assetDto.getTimePerRound()));
        if (assetDtoQueue.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_DOWN) {
            assetDtoQueue.setRoundTotalDown((int) ((assetDtoQueue.getStartingPrice() - assetDtoQueue.getMinPrice()) / assetDtoQueue.getPriceStep()));
        }
        assetDtoList.replace(assetDtoQueue.getAssetId(), assetDtoQueue);

        // send mail users not accept
        sendMailToUsersNotAccept(assetDtoQueue);

        //change status to processing
        assetDtoQueue.setInProcessing(true);
    }

    public void processing(AssetDtoQueue assetDtoQueue) {
        // pollTask
        if (assetDtoQueue.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_POLL) {
            pollTask.processing(this, assetDtoQueue, systemConfigList);
        }
        // direct
        if (assetDtoQueue.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_DIRECT) {
            // up
            if (assetDtoQueue.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_UP) {
                directUpTask.processing(assetDtoQueue, systemConfigList);
            }
            // down
            if (assetDtoQueue.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_DOWN) {
                directDownTask.processing(assetDtoQueue);
            }
        }
    }

    public synchronized void onBargain(Long assetId, Long accountId, Bid bid) throws AuctionException {

        // validate
        validateBargain(assetId, accountId, bid);

        // update bargain
        Bargain bargain = updateBargain(bid, accountId);

        // onBargain
        bid.setCreateBy(accountId);
        bidRepository.save(bid);

        AssetDtoQueue assetDtoQueue = assetDtoList.get(assetId);
        bargain.setAssetId(assetDtoQueue.getAssetId());
        // update to other nodes
        RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_BARGAIN, bargain);

        // sync to client
        socketUpdater.updateScope(assetDtoList.get(assetId));
        // sync to other nodes
        RedisHelper.syncSocket(assetId, SysConstant.ACTION_UPDATE_SCOPE, new ArrayList<>(), true);

        // check joined
        if (!assetDtoQueue.getAccountJoined().containsKey(accountId)) {
            assetDtoQueue.getAccountJoined().put(accountId, assetDtoQueue.getStartTime());
            auctionRegisterRepository.updateStatusJoined(assetId, Collections.singletonList(accountId), DbConstant.AUCTION_REGISTER_STATUS_JOINED_JOIN);
            auctionRegisterRepository.updateTimeJoined(assetId, accountId, assetDtoQueue.getStartTime());
        }

    }

    public Bargain updateBargain(Bid bid, Long accountId) throws AuctionException {
        AssetDtoQueue assetDtoQueue = assetDtoList.get(bid.getAssetId());

        if (assetDtoQueue.isInTimeConfirm()) {
            throw new AuctionException("Đã hết thời gian đấu giá");
        }
        if (assetDtoQueue.getPriceRoundCurrent() != null
                && !assetDtoQueue.getPriceRoundCurrent().getPriceRoundId().equals(bid.getPriceRoundId())) {
            throw new AuctionException("Phiên đấu giá đã chuyển qua vòng tiếp theo");
        }

        //
        return updateBargainSort(assetDtoQueue, bid, accountId);
    }

    public Bargain updateBargainSort(AssetDtoQueue assetDtoQueue, Bid bid, Long accountId) {
        //
        assetDtoQueue.setCurrentPrice(bid.getMoney());
        assetDtoQueue.getPriceBargained().put(accountId, bid.getMoney());
        assetDtoQueue.addBargain(bid.getBidId(), bid.getPriceRoundId(), accountId, bid.getAuctionRegisterId(), bid.getMoney());
        Bargain lastBargain = assetDtoQueue.getBargainList().get(assetDtoQueue.getBargainList().size() - 1);
        if (assetDtoQueue.getWinner() == null || lastBargain.getMoney() > assetDtoQueue.getWinner().getMoney()) {
            assetDtoQueue.setWinner1th(lastBargain);
            assetDtoQueue.setWinner(lastBargain);
        }
        assetDtoQueue.getAccountIdBargained().add(accountId);

        // sort price bargain -> highest to lowest
        assetDtoQueue.getBargainList().sort(Comparator.comparing(Bargain::getMoney).thenComparing(Bargain::getCreateDate, Comparator.reverseOrder()));

        //
        return lastBargain;
    }

    public void cancelAsset(Long assetId) {
        AssetDtoQueue assetDtoQueue = assetDtoList.get(assetId);
        //
        if (assetDtoQueue == null) {
            assetIdCancelList.add(assetId);
        } else {
            assetDtoQueue.setCancel(true);
        }

        // update startTime
        Map<Long, Date> startTime = new LinkedHashMap<>();
        Long regulationId = null;
        List<Asset> assetList = assetRepository.findAssetsByAssetId(assetId);
        for (Asset asset : assetList) {
            regulationId = asset.getRegulationId();
            startTime.put(asset.getAssetId(), asset.getStartTime());
        }
        if (regulationId != null) {
            RegulationDtoQueue regulationDtoQueue = regulationDtoList.get(regulationId);
            if (regulationDtoQueue != null) {
                for (AssetDtoQueue temp : regulationDtoQueue.getAssetDtoQueueList()) {
                    temp.setStartTime(startTime.get(temp.getAssetId()));
                }
            }
        }
    }

    public void cancelRegulation(Long regulationId) {
        if (regulationDtoList.get(regulationId) == null) {
            if (!regulationIdCancelList.contains(regulationId)) {
                regulationIdCancelList.add(regulationId);
            }
            return;
        }

        for (AssetDtoQueue assetDtoQueue : assetDtoList.values()) {
            if (assetDtoQueue.getRegulationId().equals(regulationId)) {
                assetDtoQueue.setCancelRegulation(true);
            }
        }
    }

    public void onDeposition(Long assetId, Long accountId) {
        try {
            AssetDtoQueue assetDtoQueue = assetDtoList.get(assetId);
            if (assetDtoQueue == null) {
                return;
            }
            assetDtoQueue.deposition(accountId);

            if (assetDtoQueue.getNodeIdController() != nodeId) {
                RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_DEPOSIT, Arrays.asList(assetId, accountId));
                return;
            }


            // if all deposited
            if (AuctionValidate.isAllDeposited(assetDtoQueue)) {
                log.info("Auction failed for asset = {}, reason = {}", assetDtoQueue.getAssetId(), "All were deposited");
                // end
                assetManagementService.save(assetDtoQueue, DbConstant.ASSET_MANAGEMENT_ENDING_BAD);

                // show deposit
                socketUpdater.showDeposit(assetDtoQueue, accountId);
                // sync to other nodes
                RedisHelper.syncSocket(assetId, SysConstant.ACTION_SHOW_MODAL_DEPOSIT, Collections.singletonList(accountId), false);

                // update scope
                socketUpdater.updateScope(assetDtoQueue);
                // sync to other nodes
                RedisHelper.syncSocket(assetId, SysConstant.ACTION_UPDATE_SCOPE, new ArrayList<>(), true);
                return;
            }

            // if in time confirm
            if (assetDtoQueue.isInTimeConfirm() && assetDtoQueue.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_UP && assetDtoQueue.getWinner().getAccountId().equals(accountId)) {
                if (assetDtoQueue.getRoundConfirm() == 1) {
                    assetDtoQueue.setDeposit1th(true);
                    assetDtoQueue.setRefuseWinner(true);
                    socketUpdater.showDeposit(assetDtoQueue, accountId);
                    // sync to other nodes
                    RedisHelper.syncSocket(assetId, SysConstant.ACTION_SHOW_MODAL_DEPOSIT, Collections.singletonList(accountId), false);
                    try {
                        Thread.sleep(100);
                        socketUpdater.updateScope(assetDtoQueue);
                        // sync to other nodes
                        RedisHelper.syncSocket(assetId, SysConstant.ACTION_UPDATE_SCOPE, new ArrayList<>(), true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                if (assetDtoQueue.getRoundConfirm() == 2) {
                    log.info("Auction failed for asset = {}, reason = {}", assetDtoQueue.getAssetId(), "The winner 2th deposited");
                    assetDtoQueue.setWinner(null);
                    assetManagementService.save(assetDtoQueue, DbConstant.ASSET_MANAGEMENT_ENDING_BAD);

                    // sync
                    socketUpdater.showDeposit(assetDtoQueue, accountId);
                    // sync to other nodes
                    RedisHelper.syncSocket(assetDtoQueue.getAssetId(), SysConstant.ACTION_SHOW_MODAL_DEPOSIT, Collections.singletonList(accountId), false);
                    return;
                }
            }

            // continue direct down
            if (assetDtoQueue.isInTimeConfirm() && assetDtoQueue.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_DOWN) {
                assetDtoQueue.setRefuseWinner(true);
            }

            // sync to client
            if (!assetDtoQueue.isInTimeConfirm() || assetDtoQueue.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_DOWN || !assetDtoQueue.getWinner().getAccountId().equals(accountId)) {
                socketUpdater.showDeposit(assetDtoQueue, accountId);
                // sync to other nodes
                RedisHelper.syncSocket(assetDtoQueue.getAssetId(), SysConstant.ACTION_SHOW_MODAL_DEPOSIT, Collections.singletonList(accountId), false);
            }
        } catch (Exception e) {
            log.error("[Deposition] cause error: ", e);
        }
    }

    public void onAcceptWinner(Long assetId, Long accountId) throws AuctionException {
        AssetDtoQueue assetDtoQueue = assetDtoList.get(assetId);
        Bargain bargain = assetDtoQueue.getWinner();
        if (!AuctionValidate.isExists(assetDtoList, assetId) || (bargain != null && !bargain.getAccountId().equals(accountId))) {
            throw new AuctionException(ERROR_MESSAGE_HIDE);
        }
        if (assetDtoQueue.isAcceptWinner()) {
            log.info("[E-10]");
            throw new AuctionException("Bạn đã chấp nhận thắng cuộc rồi. Vui lòng đợi hệ thống xử lý.");
        }
        // check deposit
        if (assetDtoQueue.hasDeposition(accountId) && !assetDtoQueue.hasDepositionBySystem(accountId)) {
            log.info("[E-11]");
            throw new AuctionException("Bạn đã bị truất quyền");
        }

        //
        if (assetDtoQueue.getNodeIdController() != nodeId) {
            RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_ACCEPT_WINNER, Arrays.asList(assetId, accountId));
            return;
        }

        // changing state accept
        assetDtoQueue.setAcceptWinner(true);
    }

    public void onRefuseWinner(Long assetId, Long accountId) throws AuctionException {
        AssetDtoQueue assetDtoQueue = assetDtoList.get(assetId);
        if (!AuctionValidate.isExists(assetDtoList, assetId)
                || !assetDtoQueue.getWinner().getAccountId().equals(accountId)
                || (assetDtoQueue.hasDeposition(accountId) && !assetDtoQueue.hasDepositionBySystem(accountId))) {
            throw new AuctionException(ERROR_MESSAGE_HIDE);
        }
        if (assetDtoQueue.isAcceptWinner()) {
            log.info("[E-20]");
            throw new AuctionException("Bạn đã chấp nhận thắng cuộc rồi. Vui lòng đợi hệ thống xử lý.");
        }

        //
        if (assetDtoQueue.getNodeIdController() != nodeId) {
            RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_REFUSE_WINNER, Arrays.asList(assetId, accountId));
            return;
        }

        // save to db
        auctionRegisterRepository.updateStatusRefuseWin(assetId, accountId, DbConstant.AUCTION_REGISTER_STATUS_REFUSE_WIN_REFUSED);

        // changing state refuse
        assetDtoQueue.setRefuseWinner(true);
        assetDtoQueue.setRefuseWinner1th(true);
        assetDtoQueue.getAccountIdRefuseWinnerList().add(accountId);

        if (assetDtoQueue.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_DOWN) {
            // save to db
            auctionRegisterService.depositBySystem(assetDtoQueue.getWinner().getAuctionRegisterId(), "từ chối chấp nhận giá giảm xuống");
            assetDtoQueue.getAccountIdDepositSystem().add(accountId);
            // changing on Task
            onDeposition(assetId, accountId);
        }

        // ending if no register already
        if (assetDtoQueue.getAuctionMethodId() != DbConstant.AUCTION_METHOD_ID_DOWN && assetDtoQueue.getRoundConfirm() == 1 && assetDtoQueue.getBargain(2) == null) {
            log.info("Auction failed for asset = {}, reason = {}", assetDtoQueue.getAssetId(), "The winner refuse win, no longer winner.");
            assetDtoQueue.setInTimeConfirm(false);
            assetManagementService.save(assetDtoQueue, DbConstant.ASSET_MANAGEMENT_ENDING_BAD);
            return;
        }

        // remove account winner
        if (assetDtoQueue.getRoundConfirm() == 2) {
            assetDtoQueue.setWinner(null);
        }

        if (assetDtoQueue.getAuctionMethodId() != DbConstant.AUCTION_METHOD_ID_DOWN) {
            // sync to client
            new Thread(() -> {
                if (assetDtoQueue.getRoundConfirm() == 2) {
                    try {
                        Thread.sleep(1000);
                        socketUpdater.updateScope(assetDtoQueue);
                        // sync to other nodes
                        RedisHelper.syncSocket(assetId, SysConstant.ACTION_UPDATE_SCOPE, new ArrayList<>(), true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void onRetractPrice(Long assetId, Long accountId) throws AuctionException {
        AssetDtoQueue assetDtoQueue = assetDtoList.get(assetId);
        if (!AuctionValidate.isExists(assetDtoList, assetId)
                || !assetDtoQueue.isRetractPrice()
                || !assetDtoQueue.getWinner().getAccountId().equals(accountId)) {
            throw new AuctionException(ERROR_MESSAGE_HIDE);
        }

        //
        if (assetDtoQueue.getNodeIdController() != nodeId) {
            RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_RETRACT_PRICE, Arrays.asList(assetId, accountId));
            return;
        }

        // ending if no register already
//        if (assetDtoQueue.getAuctionRegisterList().size() <= 1) {
//            log.info("Auction failed for asset = {}, reason = {}", assetDtoQueue.getAssetId(), "The winner retract price, no longer winner.");
//            assetManagementService.save(assetDtoQueue, DbConstant.ASSET_MANAGEMENT_ENDING_BAD);
//            return;
//        }

        // change status retract bid
        bidRepository.changeStatus(DbConstant.BID_STATUS_RETRACT_TRUE, assetId, assetDtoQueue.getWinner().getAuctionRegisterId());

        // deposit
        auctionRegisterService.depositBySystem(assetDtoQueue.getWinner().getAuctionRegisterId(), "rút lại giá đã trả");
        assetDtoQueue.getAccountIdDepositSystem().add(accountId);
        onDeposition(assetId, accountId);

        // sync to client
        socketUpdater.updateScope(assetDtoQueue);
        // sync to other nodes
        RedisHelper.syncSocket(assetId, SysConstant.ACTION_UPDATE_SCOPE, new ArrayList<>(), true);
    }

    public synchronized void onAcceptPrice(Bid bid, Long accountId) throws AuctionException {
        AssetDtoQueue assetDtoQueue = assetDtoList.get(bid.getAssetId());
        AuctionRegister auctionRegister = auctionRegisterRepository.findAuctionRegisterByAssetIdAndAccountId(bid.getAssetId(), accountId);

        if (assetDtoQueue == null || auctionRegister == null) {
            throw new AuctionException(ERROR_MESSAGE_HIDE);
        }
        // check register accept
        if (auctionRegister.getStatus() != DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED) {
            throw new AuctionException("Bạn không có quyền trả giá cho cuộc đấu giá này");
        }
        //
        if (assetDtoQueue.isInTimeConfirm()) {
            if (assetDtoQueue.getAccountIdWinner().equals(accountId)) {
                throw new AuctionException("Yêu cầu đã được thực hiện");
            }
            throw new AuctionException("Một người khác đã chấp nhận giá trước bạn");
        }

        //
        if (assetDtoQueue.getNodeIdController() != nodeId) {
            RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_ACCEPT_PRICE, accountId, bid);
            return;
        }

        // setup price
        bid.setMoney(assetDtoQueue.getCurrentPrice());
        bid.setPriceRoundId(assetDtoQueue.getPriceRoundCurrent().getPriceRoundId());
        // update bargain
        updateBargain(bid, accountId);

        assetDtoQueue.setTimeAcceptPrice(new Date());
        assetDtoQueue.setTimeConfirmMinute(systemConfigList.get(DbConstant.SYSTEM_CONFIG_ID_WAITING_1TH).getValue().intValue());
//        assetDtoQueue.setWinner1th(assetDtoQueue.getBargain(1));
//        assetDtoQueue.setWinner(assetDtoQueue.getBargain(1));
        assetDtoQueue.setRoundConfirm(1);
        assetDtoQueue.setInTimeConfirm(true);
        // send to winner
        socketUpdater.updateScope(assetDtoQueue, SysConstant.ACTION_SHOW_POPUP_AUCTION_WINNER, assetDtoQueue.getSessionsByAccountIdIn(Collections.singletonList(accountId)));
        RedisHelper.syncSocket(assetDtoQueue.getAssetId(), SysConstant.ACTION_SHOW_POPUP_AUCTION_WINNER, Collections.singletonList(accountId), false);
        // send to other
        List<Long> accountIds = new ArrayList<>();
        accountIds.add(accountId);
        accountIds.addAll(assetDtoQueue.getAccountIdDepositList());
        socketUpdater.updateScope(assetDtoQueue, SysConstant.ACTION_SHOW_POPUP_AUCTION_OPPORTUNITY_D0, assetDtoQueue.getSessionRegisterNotIn(accountIds));
        RedisHelper.syncSocket(assetDtoQueue.getAssetId(), SysConstant.ACTION_SHOW_POPUP_AUCTION_OPPORTUNITY_D0, Collections.singletonList(accountId), true);
        // sync to client
        socketUpdater.updateScope(assetDtoQueue);
        // sync to other nodes
        RedisHelper.syncSocket(assetDtoQueue.getAssetId(), SysConstant.ACTION_UPDATE_SCOPE, new ArrayList<>(), true);
    }

    private void validateBargain(Long assetId, Long accountId, Bid bid) throws AuctionException {
        AssetDtoQueue assetDtoQueue = assetDtoList.get(assetId);

        if (assetDtoQueue != null && assetDtoQueue.isLockBargain()) {
            throw new AuctionException("Đã hết thời gian trả giá");
        }

        AssetSearchDto assetSearchDto = new AssetSearchDto();
        assetSearchDto.setAssetId(assetId);
        assetSearchDto.setAccountId(accountId);
        AssetDto assetDto = assetRepository.getAssetDtoByAssetId(assetSearchDto);
        if (assetDto == null) {
            log.info("Phiên đấu giá không tồn tại [ERR-001] / " + accountId);
            throw new AuctionException("Phiên đấu giá không tồn tại");
        }
        // status auction ended
        Regulation regulation = regulationRepository.findById(assetDto.getRegulationId()).orElse(new Regulation());
        if (assetDtoQueue == null || regulation.getAuctionStatus() != DbConstant.REGULATION_AUCTION_STATUS_PLAYING || assetDto.getStatus() != DbConstant.ASSET_STATUS_PLAYING) {
            log.info("Phiên đấu giá không tồn tại [ERR-002] / " + accountId);
            throw new AuctionException("Phiên đấu giá không tồn tại");
        }
        // joined or accepted
        AuctionRegister auctionRegister = auctionRegisterRepository.findAuctionRegisterByAssetIdAndAccountId(assetDto.getAssetId(), accountId);
        if (auctionRegister == null || auctionRegister.getStatus() != DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED) {
            throw new AuctionException(ERROR_MESSAGE_HIDE);
        }
        // time over bargain
        if (assetDtoQueue.isInTimeConfirm()) {
            throw new AuctionException("Đã hết thời gian trả giá");
        }
        // deposition
        if (auctionRegister.getStatusDeposit() == DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_REFUSE) {
            throw new AuctionException("Bạn đã bị truất quyền tham gia cuộc đấu giá này.");
        }
        // money
        if (bid.getMoney() == null || bid.getMoney() <= 0) {
            throw new AuctionException("Số tiền nhập vào không đúng.");
        }
        Long maxPrice = Long.parseLong(maxPriceBargain.replaceAll("\\.", ""));
        if (bid.getMoney() > maxPrice) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            String moneyString = formatter.format(maxPrice).substring(1);
            throw new AuctionException("Số tiền tối đa được trả giá là " + moneyString + " VNĐ");
        }
        if (regulation.getAuctionMethodId() != DbConstant.AUCTION_METHOD_ID_DOWN && bid.getMoney() < assetDto.getStartingPrice()) {
            throw new AuctionException("Số tiền trả giá phải bằng hoặc lớn hơn giá khởi điểm.");
        }
        if (regulation.getAuctionMethodId() != DbConstant.AUCTION_METHOD_ID_DOWN && (bid.getMoney() - assetDto.getStartingPrice()) % assetDto.getPriceStep() != 0) {
            throw new AuctionException("Số tiền nhập vào sai bước giá.");
        }
        // status auction processing
        BidSearchDto searchDto = new BidSearchDto();
        searchDto.setAssetId(assetDto.getAssetId());
        List<Bid> bidList = bidRepository.findAllBargain(searchDto);
        // bo phieu
        if (regulation.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_POLL && !bidList.isEmpty()) {
            if (bidList.stream().anyMatch(e -> auctionRegister.getAuctionRegisterId().equals(e.getAuctionRegisterId()) && e.getPriceRoundId().equals(bid.getPriceRoundId()))) {
                throw new AuctionException("Bạn đã trả giá cho tài sản trong vòng này rồi.");
            }
            if (assetDtoQueue.getPriceRoundCurrent().getStartingPrice() > bid.getMoney()) {
                throw new AuctionException("Số tiền trả giá phải bằng hoặc lớn hơn giá khởi điểm vòng hiện tại.");
            }
        }
        // truc tiep
        if (regulation.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_DIRECT && !bidList.isEmpty()) {
            // up
            if (regulation.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_UP) {
                Bargain bargain = assetDtoQueue.getBargain(1);
                if (bargain != null && bargain.getAccountId().equals(accountId)) {
                    throw new AuctionException("Bạn đã trả giá rồi.");
                }
                if (assetDtoQueue.getPriceRoundCurrent().getStartingPrice() > bid.getMoney()) {
                    throw new AuctionException("Số tiền trả giá phải bằng hoặc lớn hơn giá khởi điểm vòng hiện tại.");
                }
                if (bargain != null && bargain.getMoney() >= bid.getMoney()) {
                    throw new AuctionException("Số tiền trả giá phải lớn hơn giá hiện tại.");
                }
            }
            // down
            if (regulation.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_DOWN
                    && !bidList.isEmpty()) {
                throw new AuctionException("Bạn đã chậm mất rồi, có ai đó đã chấp nhận giá trước bạn.");
            }
        }
    }

    private void sendMailToUsersNotAccept(AssetDtoQueue assetDtoQueue) {
        new Thread(() -> {
            List<Account> accountList = auctionRegisterRepository.findAccountListByAssetIdAndStatus(assetDtoQueue.getAssetId(), DbConstant.AUCTION_REGISTER_STATUS_WAITING);
            for (Account account : accountList) {
                String name = account.isOrg() ? account.getOrgName() : account.getFullName();
                EmailUtil.getInstance().sendNotificationNotChecked(account.getEmail(), name, assetDtoQueue.getName(), assetDtoQueue.getRegulationCode());
            }
        }).start();
    }

    private void executeEndedAsset(AssetDtoQueue finalAssetDtoQueue) {
        new Thread(() -> {
            try {
                // update winner
                if (finalAssetDtoQueue.isAssetManagementEnding()) {
                    socketUpdater.updateScope(
                            finalAssetDtoQueue,
                            SysConstant.ACTION_SHOW_POPUP_AUCTION_WINNER_FINISH,
                            finalAssetDtoQueue.getSessionsByAccountId(finalAssetDtoQueue.getWinner().getAccountId())
                    );
                    RedisHelper.syncSocket(finalAssetDtoQueue.getAssetId(),
                            SysConstant.ACTION_SHOW_POPUP_AUCTION_WINNER_FINISH,
                            Collections.singletonList(finalAssetDtoQueue.getWinner().getAccountId()),
                            false);
                }
                // sync to client
                Thread.sleep(1000);
                if (finalAssetDtoQueue.isCancel()) {
                    // show popup cancel
                    socketUpdater.updateScope(finalAssetDtoQueue, SysConstant.ACTION_SHOW_POPUP_CANCEL_ASSET);
                    // sync to other nodes
                    RedisHelper.syncSocket(finalAssetDtoQueue.getAssetId(), SysConstant.ACTION_SHOW_POPUP_CANCEL_ASSET, new ArrayList<>(), true);
                } else {
                    asyncTask.updateScopeToAll(finalAssetDtoQueue);
                    RedisHelper.syncSocketAll(finalAssetDtoQueue.getAssetId(), SysConstant.ACTION_UPDATE_SCOPE);
                }
                // send mail
                List<Account> accountList = auctionRegisterRepository.findAccountListByAssetIdAndStatus(finalAssetDtoQueue.getAssetId(), DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED);
                for (Account account : accountList) {
                    EmailUtil.getInstance().sendResultAuction(
                            account.getEmail(),
                            account.isOrg() ? account.getOrgName() : account.getFullName(),
                            finalAssetDtoQueue.isAssetManagementEnding(),
                            finalAssetDtoQueue.getRegulationCode(),
                            finalAssetDtoQueue.getName(),
                            finalAssetDtoQueue.getStartTime(),
                            new Date(),
                            finalAssetDtoQueue.isAssetManagementEnding() ? finalAssetDtoQueue.getWinner().getMoney() : 0L,
                            finalAssetDtoQueue.isAssetManagementEnding() ? finalAssetDtoQueue.getWinner().getCreateDate() : new Date());
                }
                //
                Thread.sleep(4000);
                assetDtoList.remove(finalAssetDtoQueue.getAssetId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
