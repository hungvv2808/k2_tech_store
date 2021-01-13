package vn.compedia.website.auction.system.socket.push;


import lombok.Getter;
import lombok.Setter;
import vn.compedia.website.auction.system.socket.SocketServer;
import vn.compedia.website.auction.system.socket.dto.JsonResponseDto;
import vn.compedia.website.auction.system.util.SysConstant;

import javax.inject.Named;
import javax.websocket.Session;
import java.util.List;

@Named
@Getter
@Setter
public class NotifyPush {

    private List<Long> accountIds;
    private List<Session> sessions;
    private JsonResponseDto jsonResponseDto;

    public NotifyPush() {
        this.jsonResponseDto = new JsonResponseDto();
    }

    public NotifyPush(List<Long> accountIds) {
        this.accountIds = accountIds;
        this.jsonResponseDto = new JsonResponseDto();
    }

    public NotifyPush(JsonResponseDto jsonResponseDto) {
        this.jsonResponseDto = jsonResponseDto;
    }

    public NotifyPush(List<Long> accountIds, JsonResponseDto jsonResponseDto) {
        this.accountIds = accountIds;
        this.jsonResponseDto = jsonResponseDto;
    }

    public void showMessageToClients(Object data) {
        buildMessageResponseDto(data);
        sendToClients();
    }

    public void showDialogToClients(Object data) {
        buildDialogResponseDto(data);
        sendToClients();
    }

    public void notifyToClients(List<Long> accountIds, String type, String action, Object data) {
        this.accountIds = accountIds;
        buildResponseDto(type, action, data);
        sendToClients();
    }

    public void notifyToSessions(List<Session> sessions, String type, String action, Object data) {
        this.sessions = sessions;
        buildResponseDto(type, action, data);
        sendToSessions();
    }

    public void notifyToAll(String type, String action, Object data) {
        buildResponseDto(type, action, data);
        sendToAll();
    }

    public void showMessageToAll(Object data) {
        buildMessageResponseDto(data);
        sendToAll();
    }

    public void showDialogToAll(Object data) {
        buildDialogResponseDto(data);
        sendToAll();
    }

    private void buildMessageResponseDto(Object data) {
        JsonResponseDto jsonResponseDto = getJsonResponseDto();
        jsonResponseDto.setName(SysConstant.MESSAGE_GROWL_ID);
        jsonResponseDto.setAction(SysConstant.ACTION_MESSAGE);
        jsonResponseDto.setData(data);
    }

    private void buildDialogResponseDto(Object data) {
        JsonResponseDto jsonResponseDto = getJsonResponseDto();
        jsonResponseDto.setName(SysConstant.DIALOG_ID);
        jsonResponseDto.setAction(SysConstant.ACTION_DIALOG);
        jsonResponseDto.setData(data);
    }

    private void buildResponseDto(String type, String action, Object data) {
        JsonResponseDto jsonResponseDto = getJsonResponseDto();
        jsonResponseDto.setType(type);
        jsonResponseDto.setAction(action);
        jsonResponseDto.setData(data);
    }

    private void sendToClients() {
        SocketServer.sendToClients(accountIds, jsonResponseDto.buildObjectNode());
    }

    private void sendToSessions() {
        SocketServer.sendToSessions(sessions, jsonResponseDto.buildObjectNode());
    }

    private void sendToAll() {
        SocketServer.sendAll(jsonResponseDto.buildObjectNode());
    }
}
