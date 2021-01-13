/*
 * Author: Thinh Hoang
 * Date: 09/2020
 * Company: Compedia Software
 * Email: thinhhv@compedia.vn
 * Personal Website: https://vnnib.com
 */

package vn.compedia.website.auction.system.config;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import vn.compedia.website.auction.model.Asset;
import vn.compedia.website.auction.model.Bid;
import vn.compedia.website.auction.system.socket.SocketServer;
import vn.compedia.website.auction.system.socket.SocketUpdater;
import vn.compedia.website.auction.system.socket.push.NotifyPush;
import vn.compedia.website.auction.system.task.AsyncTask;
import vn.compedia.website.auction.system.task.AuctionTask;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.system.task.dto.Bargain;
import vn.compedia.website.auction.system.task.dto.RedisSyncAuction;
import vn.compedia.website.auction.system.task.dto.RedisSyncSocket;
import vn.compedia.website.auction.system.util.SysConstant;
import vn.compedia.website.auction.util.StringUtil;

import javax.inject.Inject;
import javax.websocket.Session;
import java.util.*;

@Log4j2
@Configuration
public class RedisConfig {
    @Inject @Lazy
    private AuctionTask auctionTask;
    @Inject @Lazy
    private SocketUpdater socketUpdater;
    @Inject @Lazy
    private AsyncTask asyncTask;
    @Inject @Lazy
    private NotifyPush notifyPush;

    @Value("${redis.config.host}")
    private String redisHost;
    @Value("${redis.config.port}")
    private int redisPort;
    @Value("${redis.config.password}")
    private String redisPassword;
    @Value("${redis.config.timeout}")
    private int redisTimeout;
    @Value("${deploy.redis.publish}")
    private String redisPublish;
    @Value("${deploy.redis.subscribe}")
    private String redisSubscribe;

    // redis
    public static RedissonClient redissonClient;
    public static Date PING = new Date();
    public static RTopic PUBLISH;
    public static RTopic SUBSCRIBE;

    // final
    public static String PING_MSG = "ping";

    @Bean
    public RedissonClient connect() {
        log.info("[Redis] Connecting to server...");
        try {
            // config
            redissonClient = Redisson.create(config());
            // connect
            log.info("[Redis] Connected: ");

            // subscribe init
            subscribe();

            // publish init
            publish();

        } catch (Exception e) {
            log.error("[Redis] Connect failed!");
            log.error(e);
        }
        return redissonClient;
    }

    private Config config() {
        Config config = new Config();
        config.useSingleServer().setAddress(String.format(
                "redis://%s:%s", redisHost, redisPort
        ));
        config.useSingleServer().setTimeout(redisTimeout);
        if (StringUtils.isNotEmpty(redisPassword)) {
            config.useSingleServer().setPassword(redisPassword);
        }
        return config;
    }

    public void publish() {
        PUBLISH = redissonClient.getTopic(redisPublish);
    }

    public void subscribe() {
        log.info("[Redis] Ready for subscribe...");
        SUBSCRIBE = redissonClient.getTopic(redisSubscribe);
        SUBSCRIBE.addListener(Object.class, (channel, msg) -> {
            Object object = StringUtil.byteToObject((byte[]) msg);

            // ping init
            if (object instanceof String) {
                if (!PING_MSG.equals(object)) {
                    log.info("[Redis] Received a packet: " + object);
                }
                return;
            }

            // sync client
            if (object instanceof RedisSyncSocket) {
                executeSyncClient((RedisSyncSocket) object);
                return;
            }

            // sync auction nodes
            if (object instanceof RedisSyncAuction) {
                executeSyncAuction((RedisSyncAuction) object);
            }
        });
        // checking subscribe
        new Thread(() -> {
            RTopic rTopic = redissonClient.getTopic(redisSubscribe);
            rTopic.publish(StringUtil.objectToByte("Publish successfully!"));
        }).start();
    }

    private void executeSyncAuction(RedisSyncAuction redisSyncAuction) {
        if (!Arrays.asList(
                SysConstant.SYNC_ACTION_ASSET,
                SysConstant.SYNC_ACTION_SOCKET_PING
        ).contains(redisSyncAuction.getAction())) {
            log.info("[Redis|SyncAuction] received a action: {}", redisSyncAuction.getAction());
        }
        AssetDtoQueue assetDtoQueue = redisSyncAuction.getAssetDtoQueue();
        switch (redisSyncAuction.getAction()) {
            case SysConstant.SYNC_ACTION_ASSET:
                if (auctionTask.getAssetDtoList() == null) {
                    auctionTask.setAssetDtoList(new LinkedHashMap<>());
                }
                AssetDtoQueue old = auctionTask.getAssetDtoList().get(assetDtoQueue.getAssetId());
                if (old == null) {
                    auctionTask.getAssetDtoList().put(assetDtoQueue.getAssetId(), assetDtoQueue);
                    old = assetDtoQueue;
                } else {
                    BeanUtils.copyProperties(assetDtoQueue, old,
                            "SESSIONS", "guestSESSIONS", "priceBargained", "bargainList", "accountJoined", "accountIdDepositList",
                            "accountIdDepositSystem", "accountIdDepositShowed", "accountIdBargained", "accountIdRefuseWinnerList");
                    old.setFirstAsset(old.isFirstAsset() || assetDtoQueue.isFirstAsset());
                    old.setInProcessing(old.isInProcessing() || assetDtoQueue.isInProcessing());
                    old.setEnded(old.isEnded() || assetDtoQueue.isEnded());
                    old.setCancel(old.isCancel() || assetDtoQueue.isCancel());
                    old.setCancelRegulation(old.isCancelRegulation() || assetDtoQueue.isCancelRegulation());
                }

                if (old.getSESSIONS() == null || old.getGuestSESSIONS() == null) {
                    old.setSESSIONS(new LinkedHashMap<>());
                    old.setGuestSESSIONS(new LinkedHashMap<>());
                }

                break;
            case SysConstant.SYNC_ACTION_DEPOSIT:
                auctionTask.onDeposition(redisSyncAuction.getObjectId(), redisSyncAuction.getObjectId2());
                break;
            case SysConstant.SYNC_ACTION_BARGAIN:
                Bargain bargain = redisSyncAuction.getBargain();
                AssetDtoQueue oldAssetBargain = auctionTask.getAssetDtoList().get(bargain.getAssetId());
                Bid bid = new Bid(
                        bargain.getPriceRoundId(),
                        bargain.getAuctionRegisterId(),
                        oldAssetBargain.getAssetId(),
                        bargain.getMoney(),
                        bargain.getCreateDate()
                );
                auctionTask.updateBargainSort(oldAssetBargain, bid, bargain.getAccountId());
                // sync to client
                socketUpdater.updateScope(oldAssetBargain);
                break;
            case SysConstant.SYNC_ACTION_CANCEL_ASSET:
                auctionTask.cancelAsset(redisSyncAuction.getObjectId());
                break;
            case SysConstant.SYNC_ACTION_ASSET_CHANGE_ROUND:
                AssetDtoQueue ass = auctionTask.getAssetDtoList().get(redisSyncAuction.getObjectId());
                List<Session> sessionDepositList = ass.getSessionList();
                socketUpdater.updateScope(ass, SysConstant.ACTION_UPDATE_SCOPE, sessionDepositList);
                break;
            case SysConstant.SYNC_ACTION_ASSET_ENDED:
                auctionTask.getAssetDtoList().remove(redisSyncAuction.getObjectId());
                break;
            case SysConstant.SYNC_ACTION_CANCEL_REGULATION:
                auctionTask.cancelRegulation(redisSyncAuction.getObjectId());
                break;
            case SysConstant.SYNC_ACTION_SOCKET_PING:
                List<Long> assetIdList = redisSyncAuction.getObjectList();
                for (Long assetId : assetIdList) {
                    socketUpdater.updateJoined(assetId, redisSyncAuction.getObjectId());
                }
                break;
            case SysConstant.SYNC_ACTION_SOCKET_ONLINE:
                SocketServer.getONLINE_ACCOUNT().add(redisSyncAuction.getObjectId());
                break;
            case SysConstant.SYNC_ACTION_SOCKET_OFFLINE:
                SocketServer.getONLINE_ACCOUNT().remove(redisSyncAuction.getObjectId());
                break;
            case SysConstant.SYNC_ACTION_RETRACT_PRICE:
                try {
                    auctionTask.onRetractPrice(redisSyncAuction.getObjectId(), redisSyncAuction.getObjectId2());
                } catch (Exception e) {
                    log.error(e);
                }
                break;
            case SysConstant.SYNC_ACTION_ACCEPT_PRICE:
                try {
                    auctionTask.onAcceptPrice(redisSyncAuction.getBid(), redisSyncAuction.getObjectId());
                } catch (Exception e) {
                    log.error(e);
                }
                break;
            case SysConstant.SYNC_ACTION_REFUSE_WINNER:
                try {
                    auctionTask.onRefuseWinner(redisSyncAuction.getObjectId(), redisSyncAuction.getObjectId2());
                } catch (Exception e) {
                    log.error(e);
                }
                break;
            case SysConstant.SYNC_ACTION_ACCEPT_WINNER:
                try {
                    auctionTask.onAcceptWinner(redisSyncAuction.getObjectId(), redisSyncAuction.getObjectId2());
                } catch (Exception e) {
                    log.error(e);
                }
                break;
        }
    }

    private void executeSyncClient(RedisSyncSocket redisSyncSocket) {
        log.info("[Redis|SyncClient] received a action: {}", redisSyncSocket.getAction());

        // public regulation
        if (redisSyncSocket.getAssetId() == 0L) {
            Asset asset = new Asset();
            asset.setAssetId(0L);
            log.info("Sync to {} client", SocketServer.getSESSIONS().size());
            notifyPush.notifyToAll(SysConstant.TYPE_UPDATE_SCOPE, SysConstant.ACTION_UPDATE_SCOPE, asset);
        }

        AssetDtoQueue assetDtoQueue = auctionTask.getAssetDtoList().get(redisSyncSocket.getAssetId());
        if (assetDtoQueue != null) {
            // sleep
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // clear bargain if change round
            if (redisSyncSocket.getAction().equals(SysConstant.ACTION_SHOW_POPUP_CHANGE_ROUND)) {
                assetDtoQueue.setPriceBargained(new TreeMap<>());
            }
            // sync all
            if (redisSyncSocket.isSendToAll()) {
                asyncTask.updateScopeToAll(assetDtoQueue);
                return;
            }
            // sync not in
            if (redisSyncSocket.isAccountIdNotIn()) {
                switch (redisSyncSocket.getType()) {
                    case SysConstant.TYPE_ASSET_DETAIL:
                        asyncTask.updateScope(assetDtoQueue, true);
                        break;
                    default:
                        socketUpdater.updateScope(assetDtoQueue, redisSyncSocket.getAction(), assetDtoQueue.getSessionRegisterNotIn(redisSyncSocket.getAccountIdList()));
                }
                return;
            }
            // sync in
            socketUpdater.updateScope(assetDtoQueue, redisSyncSocket.getAction(), assetDtoQueue.getSessionsByAccountIdIn(redisSyncSocket.getAccountIdList()));
        }
    }
}
