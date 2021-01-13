package vn.compedia.website.auction.system.socket;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.server.standard.SpringConfigurator;
import vn.compedia.website.auction.dto.user.AccountDto;
import vn.compedia.website.auction.system.task.helper.RedisHelper;
import vn.compedia.website.auction.system.util.SysConstant;
import vn.compedia.website.auction.util.StringUtil;

import javax.inject.Inject;
import javax.inject.Named;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.*;

@Log4j2
@Named
@ServerEndpoint(value = "/connection", configurator = SpringConfigurator.class)
public class SocketServer {
    @Inject
    private SocketUpdater socketUpdater;

    @Getter
    private static final Set<Session> SESSIONS = new HashSet<>();
    @Getter
    private static final Map<Long, Set<Session>> ONLINE = new LinkedHashMap<>();
    @Getter
    private static final Set<Long> ONLINE_ACCOUNT = new HashSet<>();

    @OnOpen
    public void onOpen(Session session) {
        try {
            SESSIONS.add(session);
            socketUpdater.addToAsset(null, session, true);
            AccountDto accountDto = StringUtil.getAccountDto(session);
            if (accountDto != null) {
                if (!ONLINE.containsKey(accountDto.getAccountId())) {
                    ONLINE.put(accountDto.getAccountId(), new LinkedHashSet<>());
                }
                ONLINE.get(accountDto.getAccountId()).add(session);
                RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_SOCKET_ONLINE, accountDto.getAccountId());
            }
            log.info("[Socket|Open] = {}", session);
        } catch (Exception e) {
            log.error(e);
        }
    }

    @OnClose
    public void onClose(Session session) {
        try {
            SESSIONS.remove(session);
            socketUpdater.removeFromAsset(session);
            AccountDto accountDto = StringUtil.getAccountDto(session);
            if (accountDto != null) {
                ONLINE.get(accountDto.getAccountId()).remove(session);
                if (ONLINE.get(accountDto.getAccountId()).size() < 1) {
                    ONLINE.remove(accountDto.getAccountId());
                }
                RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_SOCKET_OFFLINE, accountDto.getAccountId());
            }
            log.info("[Socket|Close] = {}", session);
        } catch (Exception e) {
            log.error(e);
        }
    }

    @OnError
    public void onError(Session session, Throwable e) {
        //log.error("[Socket|Error] cause error: ", e);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        //
        SESSIONS.add(session);
        // ping for update session to asset
        AccountDto accountDto = StringUtil.getAccountDto(session);
        try {
            if (StringUtils.isBlank(message)) {
                return;
            }
            long assetId = Long.parseLong(message.split(" ")[1]);
            socketUpdater.addToAssetFromAssetId(assetId, session);

            if (accountDto != null) {
                if (!ONLINE.containsKey(accountDto.getAccountId())) {
                    ONLINE.put(accountDto.getAccountId(), new LinkedHashSet<>());
                }
                ONLINE.get(accountDto.getAccountId()).add(session);
                //
                List<Long> data = new ArrayList<>(accountDto.getSessionAsset().keySet());
                data.add(0, accountDto.getAccountId());
                RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_SOCKET_PING, data);
            }

        } catch (Exception e) {
            log.error("[Socket|onMessage] cause error: ", e);
            if (accountDto != null) {
                log.error("cause error by account: {}", accountDto.getAccountId());
            }
        }
    }

    public static void sendToSessions(List<Session> sessions, Object object) {
        String json = StringUtil.toJson(object);
        for (Session session : sessions) {
            sendToSession(session, json);
        }
    }

    private synchronized static void sendToSession(Session session, String json) {
        if (session.isOpen()) {
            try {
                Thread.sleep(10);
                session.getAsyncRemote().sendText(json);
                log.info("[Socket|SendToSession] = {}", json);
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    public synchronized static void sendToClients(List<Long> accountIds, Object object) {
        String json = StringUtil.toJson(object);
        synchronized (SESSIONS) {
            for (Session session : SESSIONS) {
                AccountDto accountDto = StringUtil.getAccountDto(session);
                if (session.isOpen() && accountDto != null && accountIds.contains(accountDto.getAccountId())) {
                    session.getAsyncRemote().sendText(json);
                    log.info("[Socket|SendToClient] = {}", json);
                }
            }
        }
    }

    public synchronized static void sendAll(Object object) {
        String json = StringUtil.toJson(object);
        synchronized (SESSIONS) {
            for (Session session : SESSIONS) {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(json);
                    log.info("[Socket|SendToAll] = {}", json);
                }
            }
        }
    }

    public static boolean isOnline(Long accountId) {
        return ONLINE.containsKey(accountId) || ONLINE_ACCOUNT.contains(accountId);
    }
}
