/*
 * Author: Thinh Hoang
 * Date: 09/2020
 * Company: Compedia Software
 * Email: thinhhv@compedia.vn
 * Personal Website: https://vnnib.com
 */

package vn.compedia.website.auction.system.task.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import vn.compedia.website.auction.model.Bid;
import vn.compedia.website.auction.system.config.RedisConfig;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.system.task.dto.Bargain;
import vn.compedia.website.auction.system.task.dto.RedisSyncAuction;
import vn.compedia.website.auction.system.task.dto.RedisSyncSocket;
import vn.compedia.website.auction.system.util.SysConstant;
import vn.compedia.website.auction.util.StringUtil;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Log4j2
@Named
@Component
public class RedisHelper {

    // REDIS
    public static RedissonClient redissonClient() {
        return RedisConfig.redissonClient;
    }

    public static void publish(Object object) {
        if (RedisConfig.PUBLISH != null) {
            RedisConfig.PUBLISH.publish(StringUtil.objectToByte(object));
        }
    }

    public static void ping() {
        publish("ping");
    }

    public static void syncSocketAuction(String action, Object object) {
        switch (action) {
            case SysConstant.SYNC_ACTION_ASSET:
                publish(new RedisSyncAuction(action, (AssetDtoQueue) object));
                break;
            case SysConstant.SYNC_ACTION_BARGAIN:
                publish(new RedisSyncAuction(action, (Bargain) object));
                break;
            case SysConstant.SYNC_ACTION_CANCEL_ASSET:
            case SysConstant.SYNC_ACTION_ASSET_CHANGE_ROUND:
            case SysConstant.SYNC_ACTION_ASSET_ENDED:
            case SysConstant.SYNC_ACTION_CANCEL_REGULATION:
            case SysConstant.SYNC_ACTION_SOCKET_ONLINE:
            case SysConstant.SYNC_ACTION_SOCKET_OFFLINE:
                publish(new RedisSyncAuction(action, (Long) object));
                break;
            case SysConstant.SYNC_ACTION_DEPOSIT:
            case SysConstant.SYNC_ACTION_ACCEPT_WINNER:
            case SysConstant.SYNC_ACTION_RETRACT_PRICE:
            case SysConstant.SYNC_ACTION_REFUSE_WINNER:
                List<Long> data = (List<Long>) object;
                publish(new RedisSyncAuction(action, data.get(0), data.get(1)));
                break;
            case SysConstant.SYNC_ACTION_SOCKET_PING:
                List<Long> objectList = new ArrayList<>((List<Long>) object);
                Long accountId = objectList.remove(0);
                publish(new RedisSyncAuction(action, accountId, objectList));
                break;
        }
    }

    public static void syncSocketAuction(String action, Long objectId, Object object) {
        switch (action) {
            case SysConstant.SYNC_ACTION_ACCEPT_PRICE:
                publish(new RedisSyncAuction(action, objectId, (Bid) object));
                break;
        }
    }

    public static void syncSocket(Long assetId, String action, List<Long> accountIdList, boolean accountIdNotIn) {
        publish(new RedisSyncSocket(assetId, SysConstant.TYPE_UPDATE_SCOPE, action, accountIdList, accountIdNotIn, false));
    }

    public static void syncSocket(Long assetId, String type, String action, List<Long> accountIdList, boolean accountIdNotIn) {
        publish(new RedisSyncSocket(assetId, type, action, accountIdList, accountIdNotIn, false));
    }

    public static void syncSocketAll(Long assetId, String action) {
        publish(new RedisSyncSocket(assetId, SysConstant.TYPE_UPDATE_SCOPE, action, new ArrayList<>(), true, true));
    }

    // --------
    // PREFIX
    // --------
    public static String getRegulationKey(Long regulationId) {
        return "R" + regulationId;
    }

    public static String getAssetKey(Long assetId) {
        return "A" + assetId;
    }

    public static String getCancelFirstAssetKey(Long regulationId) {
        return "CFA" + regulationId;
    }

    public static String getRegulationIdCancelKey() {
        return "RIC";
    }

    public static String getAssetIdCancelKey() {
        return "AIC";
    }

    // --------
    // PRIVATE
    // --------
    private static List<Long> getLongListByByte(byte[] json) {
        if (json == null || json.length < 1) {
            return new ArrayList<>();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Set<Long> temp = objectMapper.readValue(json, new TypeReference<Set<Long>>(){});
            if (temp == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(temp);
        } catch (Exception e) {
            log.error(e);
            return new ArrayList<>();
        }
    }

}
