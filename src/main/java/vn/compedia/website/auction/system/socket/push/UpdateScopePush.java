package vn.compedia.website.auction.system.socket.push;


import lombok.Getter;
import lombok.Setter;
import vn.compedia.website.auction.system.socket.SocketServer;
import vn.compedia.website.auction.system.socket.dto.JsonResponseDto;

import javax.inject.Named;
import javax.websocket.Session;
import java.util.List;

@Named
@Getter
@Setter
public class UpdateScopePush {

    private List<Session> sessions;
    private JsonResponseDto jsonResponseDto;

    public UpdateScopePush() {
        this.jsonResponseDto = new JsonResponseDto();
    }

    public UpdateScopePush(List<Session> sessions) {
        this.sessions = sessions;
        this.jsonResponseDto = new JsonResponseDto();
    }

    public UpdateScopePush(JsonResponseDto jsonResponseDto) {
        this.jsonResponseDto = jsonResponseDto;
    }

    public UpdateScopePush(List<Session> sessions, JsonResponseDto jsonResponseDto) {
        this.sessions = sessions;
        this.jsonResponseDto = jsonResponseDto;
    }

    public void updateToSessions(String scopeId, Object data) {
        buildResponseDto(data);
        sendToSessions();
    }

    public void updateToSessions(Object data) {
        buildResponseDto(data);
        sendToSessions();
    }

    private void buildResponseDto(Object data) {
        getJsonResponseDto().setData(data);
    }

    private void sendToSessions() {
        SocketServer.sendToSessions(sessions, jsonResponseDto.buildObjectNode());
    }
}
