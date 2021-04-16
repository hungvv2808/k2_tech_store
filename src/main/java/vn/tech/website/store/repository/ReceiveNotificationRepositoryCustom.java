package vn.tech.website.store.repository;

import vn.tech.website.store.dto.NotificationSearchDto;
import vn.tech.website.store.dto.ReceiveNotificationDto;

import java.util.List;

public interface ReceiveNotificationRepositoryCustom {
    List<ReceiveNotificationDto> search(NotificationSearchDto searchDto);

    Long countRecordNotSeen(NotificationSearchDto searchDto);

}
