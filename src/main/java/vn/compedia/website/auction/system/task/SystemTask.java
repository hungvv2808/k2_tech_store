/*
 * Author: Thinh Hoang
 * Date: 09/2020
 * Company: Compedia Software
 * Email: thinhhv@compedia.vn
 * Personal Website: https://vnnib.com
 */

package vn.compedia.website.auction.system.task;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import vn.compedia.website.auction.model.SystemConfig;
import vn.compedia.website.auction.repository.SystemConfigRepository;
import vn.compedia.website.auction.service.AssetService;
import vn.compedia.website.auction.system.config.RedisConfig;
import vn.compedia.website.auction.system.socket.SocketServer;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.system.task.dto.RegulationDtoQueue;
import vn.compedia.website.auction.system.task.helper.RedisHelper;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;

import java.util.*;

@Log4j2
@Configuration
@EnableScheduling
public class SystemTask {
    @Autowired
    private AssetService assetService;
    @Autowired
    private SystemConfigRepository systemConfigRepository;
    @Autowired
    private AuctionTask auctionTask;

    @Value("${deploy.node.id}")
    private int nodeId;
    @Value("${deploy.node.total}")
    private int nodeTotal;
    @Value("${deploy.state}")
    private boolean nodeDeploy;

    @Getter
    private Queue<RegulationDtoQueue> regulationDtoQueues;
    private final int NODE_TIMEOUT = 5; // seconds

    public SystemTask() {
        this.regulationDtoQueues = new PriorityQueue<>();
    }

    // LOAD ASSETS LIST FROM DATABASE
    @Scheduled(cron = "${schedule.cron.expression.scan}")
    public void scheduleLoadAssetTask() {
        Queue<RegulationDtoQueue> temp = assetService.findAllByCurrentTime();
        Map<Long, SystemConfig> systemConfigList = new HashMap<>();
        List<Long> regulationIdList = new ArrayList<>();
        List<Long> assetIdList = new ArrayList<>();

        // reload system config
        if (!temp.isEmpty()) {
            systemConfigRepository.findAll().forEach(e -> systemConfigList.put(e.getSystemConfigId(), e));
            auctionTask.setSystemConfigList(systemConfigList);
        }

        // add to queue
        while (!temp.isEmpty()) {
            putToQueue(temp, systemConfigList, regulationIdList, assetIdList);
        }

        // clean trash
        if (!regulationIdList.isEmpty()) {
            auctionTask.getRegulationIdCancelList().removeIf(e -> !regulationIdList.contains(e));
        }
    }

    // ACTIVATION AUCTION WITH ASSETS LIST IN QUEUE
    @Scheduled(cron = "${schedule.cron.expression.execute}")
    public void scheduleAuctionTask() {

        // check timeout other nodes
        if (nodeDeploy && new Date().compareTo(DateUtil.plusSecond(RedisConfig.PING, NODE_TIMEOUT)) >= 0
            && (new Date().getTime() - RedisConfig.PING.getTime()) < NODE_TIMEOUT * 2 * 1000) {
            SocketServer.getSESSIONS().clear();
            SocketServer.getONLINE_ACCOUNT().clear();
            SocketServer.getONLINE().clear();
        }

        // ping subscribe
        RedisHelper.ping();

        // execute
        while (checkActivation()) {
            log.info("[Auction|Activation] nodeId: {}", nodeId);
            // async task
            auctionTask.activationAuction(Objects.requireNonNull(regulationDtoQueues.poll()));
        }
    }

    private boolean checkActivation() {
        RegulationDtoQueue regulationDtoQueue = regulationDtoQueues.peek();
        boolean playing = !regulationDtoQueues.isEmpty() && regulationDtoQueue.getStartTime().compareTo(new Date()) <= 0;
        if (!nodeDeploy) {
            return playing;
        }
        if (playing) {
            if ((regulationDtoQueue.getRegulationId() % nodeTotal) == (nodeId - 1)
                    || new Date().compareTo(DateUtil.plusSecond(RedisConfig.PING, NODE_TIMEOUT)) >= 0) {
                return true;
            } else {
                // remove if not processing
                log.info("[Auction|NotActivation] regulation: {}", regulationDtoQueue.getRegulationId());
                regulationDtoQueues.poll();
            }
        }
        return false;
    }

    private void putToQueue(Queue<RegulationDtoQueue> temp, Map<Long, SystemConfig> systemConfigList, List<Long> regulationIdList, List<Long> assetIdList) {
        RegulationDtoQueue regulationDtoQueue = temp.poll();
        if (!regulationDtoQueues.contains(regulationDtoQueue)) {
            SystemConfig systemConfig = systemConfigList.get(DbConstant.SYSTEM_CONFIG_ID_DEPOSITION);
            int timeDeposit = 0;
            if (systemConfig != null) {
                timeDeposit = systemConfig.getValue().intValue();
            }
            regulationDtoQueue.setTimeOverJoined(DateUtil.plusMinute(regulationDtoQueue.getStartTime(), timeDeposit));
            //put to queue
            regulationDtoQueues.offer(regulationDtoQueue);
            log.info("Added a regulation to queue = {}", regulationDtoQueue.getRegulationId());
        }
        // for clean trash
        regulationIdList.add(regulationDtoQueue.getRegulationId());
        for (AssetDtoQueue assetDtoQueue : regulationDtoQueue.getAssetDtoQueueList()) {
            assetIdList.add(assetDtoQueue.getAssetId());
        }
        auctionTask.getAssetIdCancelList().removeIf(e -> !assetIdList.contains(e));
    }
}
