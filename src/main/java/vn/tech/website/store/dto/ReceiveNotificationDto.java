package vn.tech.website.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.tech.website.store.model.ReceiveNotification;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveNotificationDto extends ReceiveNotification {
    private String receiverName;
    private String senderName;
    private String content;
    private String dateBeforeNow;
}
