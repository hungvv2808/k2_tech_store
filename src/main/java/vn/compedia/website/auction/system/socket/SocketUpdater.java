/*
 * Author: Thinh Hoang
 * Date: 09/2020
 * Company: Compedia Software
 * Email: thinhhv@compedia.vn
 * Personal Website: https://vnnib.com
 */

package vn.compedia.website.auction.system.socket;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.compedia.website.auction.dto.user.AccountDto;
import vn.compedia.website.auction.model.Asset;
import vn.compedia.website.auction.repository.AssetRepository;
import vn.compedia.website.auction.repository.AuctionRegisterRepository;
import vn.compedia.website.auction.system.socket.push.NotifyPush;
import vn.compedia.website.auction.system.task.AuctionTask;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.system.task.helper.RedisHelper;
import vn.compedia.website.auction.system.util.SysConstant;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.StringUtil;

import javax.inject.Inject;
import javax.inject.Named;
import javax.websocket.Session;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Named
@Component
public class SocketUpdater {
    @Inject
    private AuctionTask auctionTask;
    @Inject
    private NotifyPush notifyPush;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private AssetRepository assetRepository;

    @Value("${deploy.node.id}")
    private int nodeId;

    public synchronized void updateScope(AssetDtoQueue assetDtoQueue) {
        updateScope(assetDtoQueue, SysConstant.ACTION_UPDATE_SCOPE);
    }

    public void updateScope(AssetDtoQueue assetDtoQueue, String action) {
        updateScopeNotIn(assetDtoQueue, action, new ArrayList<>());
    }

    public void updateScopeNotIn(AssetDtoQueue assetDtoQueue, String action, List<Session> notInSessionList) {
        if (assetDtoQueue == null || assetDtoQueue.getSessionList() == null) {
            return;
        }
        // sync to other nodes
        if (assetDtoQueue.getNodeIdController() == nodeId) {
            RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_ASSET, assetDtoQueue);
        }
        // sync socket
        List<Session> sessionList = assetDtoQueue.getSessionList()
                .stream()
                .filter(e -> !notInSessionList.contains(e))
                .collect(Collectors.toList());
        updateScope(assetDtoQueue, action, sessionList);
    }

    public synchronized void updateScope(AssetDtoQueue assetDtoQueue, String action, List<Session> sessionList) {
        if (assetDtoQueue == null || assetDtoQueue.getSessionList() == null) {
            return;
        }
        // sync to other nodes
        if (assetDtoQueue.getNodeIdController() == nodeId) {
            RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_ASSET, assetDtoQueue);
        }
        // sync socket
        Asset asset = new Asset();
        asset.setAssetId(assetDtoQueue.getAssetId());
        log.info("Sync to {} client", sessionList.size());
        notifyPush.notifyToSessions(sessionList, SysConstant.TYPE_UPDATE_SCOPE, action, asset);
    }

    public void updateEndedToClients(Object data) {
        notifyPush.notifyToAll(SysConstant.TYPE_UPDATE_SCOPE, SysConstant.ACTION_UPDATE_ENDED, data);
    }

    public void showDeposit(AssetDtoQueue assetDtoQueue, Long accountId) {
        // update to user deposit
        Set<Session> ses = assetDtoQueue.getSESSIONS().get(accountId);
        if (ses != null) {
            updateScope(assetDtoQueue, SysConstant.ACTION_SHOW_MODAL_DEPOSIT, new ArrayList<>(ses));
        }
        // UPDATE TO GUEST
        try {
            Thread.sleep(1000);
            List<Session> sessionGuest = assetDtoQueue.getSessionRegisterNotIn(Collections.singletonList(accountId));
            sessionGuest.addAll(assetDtoQueue.getGuestSESSIONS().values());
            updateScope(assetDtoQueue, SysConstant.ACTION_UPDATE_SCOPE, sessionGuest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * onOpen:  true    -> get list asset playing
     *          false   -> get list asset from accountDto
     */
    public void addToAsset(Long assetId, Session session, boolean onOpen) {
        List<Asset> assetList;
        AccountDto accountDto = StringUtil.getAccountDto(session);
        if (accountDto == null) {
            return;
        }
        // get asset list
        if (onOpen) {
            assetList = getAssetList(accountDto);
        } else {
            if (accountDto.getSessionAsset() == null) {
                assetList = new ArrayList<>();
            } else {
                assetList = new ArrayList<>(accountDto.getSessionAsset().values());
            }
        }
        // check overtime
        checkOvertime(accountDto.getAccountId(), assetList);
        // add to asset
        if (!assetList.isEmpty()) {
            addToAsset(session, assetList);
        }
        // view
        if (assetId != null && assetId > 0) {
            AssetDtoQueue assetDtoQueue = auctionTask.getAssetDtoList().get(assetId);
            if (assetDtoQueue != null) {
                assetDtoQueue.getSESSIONS().putIfAbsent(accountDto.getAccountId(), new HashSet<>());
                assetDtoQueue.getSESSIONS().get(accountDto.getAccountId()).add(session);
            }
        }
    }

    public void addToAsset(Session session, List<Asset> assetList) {
        AccountDto accountDto = StringUtil.getAccountDto(session);
        if (accountDto != null) {
            for (Asset asset : assetList) {
                AssetDtoQueue assetDtoQueue = auctionTask.getAssetDtoList().get(asset.getAssetId());
                if (assetDtoQueue != null) {
                    assetDtoQueue.getSESSIONS().putIfAbsent(accountDto.getAccountId(), new HashSet<>());
                    assetDtoQueue.getSESSIONS().get(accountDto.getAccountId()).add(session);
                }
            }
        }
        // check overtime joined -> deposit
    }

    public void addToAssetFromAssetId(Long assetId, Session session) {
        AccountDto accountDto = StringUtil.getAccountDto(session);
        if (accountDto != null) {
            addToAsset(assetId, session, false);
            return;
        }
        AssetDtoQueue assetDtoQueue = auctionTask.getAssetDtoList().get(assetId);
        if (assetDtoQueue != null) {
            assetDtoQueue.getGuestSESSIONS().put(session.getId(), session);
        }
    }

    public void removeFromAsset(Session session) {
        AccountDto accountDto = StringUtil.getAccountDto(session);
        if (accountDto == null) {
            for (AssetDtoQueue assetDtoQueue : auctionTask.getAssetDtoList().values()) {
                assetDtoQueue.getGuestSESSIONS().remove(session.getId());
            }
            return;
        }
        // remove in session list
        for (AssetDtoQueue assetDtoQueue : auctionTask.getAssetDtoList().values()) {
            if (assetDtoQueue != null) {
                AssetDtoQueue temp = auctionTask.getAssetDtoList().get(assetDtoQueue.getAssetId());
                if (temp.getSESSIONS().get(accountDto.getAccountId()) == null) {
                    continue;
                }
                temp.getSESSIONS().get(accountDto.getAccountId()).remove(session);
                if (temp.getSESSIONS().get(accountDto.getAccountId()).isEmpty()) {
                    temp.getSESSIONS().remove(accountDto.getAccountId());
                }
                log.info("[Socket|RemoveSessionFromAsset] = {}", session);
            }
        }
        // remove in guest list
        for (AssetDtoQueue assetDtoQueue : auctionTask.getAssetDtoList().values()) {
            assetDtoQueue.getGuestSESSIONS().remove(session.getId());
        }
    }

    public void updateJoined(Long assetId, Long accountId) {
        Date now = new Date();
        AssetDtoQueue assetDtoQueue = auctionTask.getAssetDtoList().get(assetId);
        if (assetDtoQueue != null
                && assetDtoQueue.isInProcessing()
                && !assetDtoQueue.getAccountJoined().containsKey(accountId)) {
            auctionRegisterRepository.updateTimeJoined(assetDtoQueue.getAssetId(), accountId, now);
            auctionRegisterRepository.updateStatusJoined(assetDtoQueue.getAssetId(), Collections.singletonList(accountId), DbConstant.AUCTION_REGISTER_STATUS_JOINED_JOIN);
            assetDtoQueue.getAccountJoined().put(accountId, now);
        }
    }

    private List<Asset> getAssetList(AccountDto accountDto) {
        if (accountDto.getRoleId() == DbConstant.ROLE_ID_USER) {
            return auctionRegisterRepository.getAllAssetByAccountId(
                    accountDto.getAccountId(),
                    DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED,
                    Collections.singletonList(DbConstant.ASSET_STATUS_PLAYING)
            );
        } else {
            return assetRepository.getAssetByAuctioneerIdAndAssetStatus(
                    accountDto.getAccountId(),
                    Collections.singletonList(DbConstant.ASSET_STATUS_PLAYING)
            );
        }
    }

    private void checkOvertime(Long accountId, List<Asset> assetList) {
        Asset assetTemp = new Asset();
        for (Asset asset : assetList) {
            if (asset.getAssetId().equals(assetTemp.getAssetId())) {
                continue;
            }
            // save temp
            BeanUtils.copyProperties(asset, assetTemp);
            // check over
//            RegulationDtoQueue regulationDtoQueue = auctionTask.getRegulationDtoList().get(asset.getRegulationId());
//            if (regulationDtoQueue != null) {
//                if (!regulationDtoQueue.isOvertimeJoined()) {
//                    regulationDtoQueue.addAccountJoined(accountId);
//                }
//            }
            // add to account joined
            updateJoined(asset.getAssetId(), accountId);
        }
    }
}
