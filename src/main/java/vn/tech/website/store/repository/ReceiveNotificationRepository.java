package vn.tech.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.tech.website.store.model.ReceiveNotification;

public interface ReceiveNotificationRepository extends CrudRepository<ReceiveNotification, Long> {
}
