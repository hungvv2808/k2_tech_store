package vn.compedia.website.auction.controller.admin;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.system.socket.SocketServer;
import vn.compedia.website.auction.system.socket.push.NotifyPush;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

@Getter
@Setter
@Named
@Scope(value = "session")
public class NotifyRealtimeController extends BaseController {
    @Inject
    private NotifyPush notifyPush;

    private Long accountId;
    private String content;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
        }
    }

    public void onSave() {
        notifyPush.showDialogToAll(content);
        FacesUtil.addSuccessMessage(Constant.ERROR_GROWL_ID, "Đã gửi thành công");
    }

    public int sizeSession() {
        return SocketServer.getSESSIONS().size();
    }

    @Override
    protected EScope getMenuId() {
        return EScope.PUBLIC;
    }
}
