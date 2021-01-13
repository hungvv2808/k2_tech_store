/*
 * Author: Thinh Hoang
 * Date: 09/2020
 * Company: Compedia Software
 * Email: thinhhv@compedia.vn
 * Personal Website: https://vnnib.com
 */

package vn.compedia.website.auction.system.task;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.compedia.website.auction.model.Asset;
import vn.compedia.website.auction.system.socket.SocketServer;
import vn.compedia.website.auction.system.socket.push.NotifyPush;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.system.task.helper.RedisHelper;
import vn.compedia.website.auction.system.util.SysConstant;

import javax.inject.Inject;
import javax.inject.Named;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Named
@Component
public class AsyncTask {
    @Inject
    private NotifyPush notifyPush;

    @Value("${deploy.node.id}")
    private int nodeId;

    public void updateScope(AssetDtoQueue assetDtoQueue, boolean sendToAll) {
        // update all
        if (sendToAll) {
            updateScopeToAll(assetDtoQueue);
        } else {
            // sync to other nodes
            if (assetDtoQueue.getNodeIdController() == nodeId) {
                RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_ASSET, assetDtoQueue);
            }
        }
        // account join
        new Thread(() -> {
            try {
                if (assetDtoQueue.getNodeIdController() == nodeId) {
                    Thread.sleep(2000);
                }
                //
                Asset asset = new Asset();
                List<Session> sessions = new ArrayList<>(assetDtoQueue.getSessionRegister());
                BeanUtils.copyProperties(assetDtoQueue, asset);
                log.info("Sync to {} client", sessions.size());
                notifyPush.notifyToSessions(sessions, SysConstant.TYPE_ASSET_DETAIL, SysConstant.ACTION_SHOW_CONFIRM, asset);
                //
                if (assetDtoQueue.getNodeIdController() == nodeId) {
                    RedisHelper.syncSocket(assetDtoQueue.getAssetId(), SysConstant.TYPE_ASSET_DETAIL, SysConstant.ACTION_SHOW_CONFIRM, new ArrayList<>(), true);
                }
            } catch (InterruptedException e) {
                log.error("[AUCTION PROCESSING] cause error: ", e);
                e.printStackTrace();
            }
        }).start();
    }

    public void updateScopeToAll(AssetDtoQueue assetDtoQueue) {
        new Thread(() -> {
            // sync to other nodes
            if (assetDtoQueue.getNodeIdController() == nodeId) {
                RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_ASSET, assetDtoQueue);
            }
            Asset asset = new Asset();
            BeanUtils.copyProperties(assetDtoQueue, asset);
            log.info("Sync to {} client", SocketServer.getSESSIONS().size());
            notifyPush.notifyToAll(SysConstant.TYPE_UPDATE_SCOPE, SysConstant.ACTION_UPDATE_SCOPE, asset);
            //
            if (assetDtoQueue.getNodeIdController() == nodeId) {
                RedisHelper.syncSocketAll(assetDtoQueue.getAssetId(), SysConstant.ACTION_UPDATE_SCOPE);
            }
        }).start();
    }
}