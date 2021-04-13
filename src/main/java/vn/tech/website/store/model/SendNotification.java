package vn.tech.website.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "send_notification")
public class SendNotification extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "send_notification_id")
    private Long sendNotificationId;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "content")
    private String content;

    @Column(name = "status")
    private Integer status;
}
