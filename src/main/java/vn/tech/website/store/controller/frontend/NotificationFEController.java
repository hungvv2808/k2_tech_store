package vn.tech.website.store.controller.frontend;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.dto.NotificationSearchDto;
import vn.tech.website.store.dto.ReceiveNotificationDto;
import vn.tech.website.store.repository.ReceiveNotificationRepository;
import vn.tech.website.store.util.Constant;
import vn.tech.website.store.util.DbConstant;
import vn.tech.website.store.util.StringUtil;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.util.List;

@Getter
@Setter
@Named
@Scope(value = "session")
public class NotificationFEController extends BaseFEController {
    @Autowired
    private ReceiveNotificationRepository receiveNotificationRepository;

    private List<ReceiveNotificationDto> notificationDtoList;
    private NotificationSearchDto searchDto;
    private Long countRecord;
    private String notificationJson;
    private Long notificationLastId;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            resetAll();
        }
    }

    public void resetAll() {
        searchDto = new NotificationSearchDto();
        searchDto.setAccountId(getAuthorizationFEController().getAccountDto().getAccountId());
        searchDto.setPageSize(Constant.NOTIFICATION_PAGE_SIZE);
        // check: false -> normal, true -> socket
        notificationDtoList = receiveNotificationRepository.search(searchDto);
        notificationLastId = !notificationDtoList.isEmpty() ? notificationDtoList.get(notificationDtoList.size() - 1).getReceiveNotificationId() : null;
        //searchDto.setCheck(false);
        countRecord = receiveNotificationRepository.countRecordNotSeen(searchDto);
        notificationJson = StringUtil.toJson(notificationDtoList);
    }

    public void seenAllNotification() {
        receiveNotificationRepository.changeStatusBell(getAuthorizationFEController().getAccountDto().getAccountId(), DbConstant.RNOTIFICATION_STATUS_BELL_SEEN);
        resetAll();
    }

    @Override
    protected String getMenuId() {
        return null;
    }
}
