package vn.compedia.website.auction.system.listener;

import lombok.extern.log4j.Log4j2;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

@Log4j2
public class SystemActivityListener {

    @PostPersist
    public void onCreate(Object data) {
        log.info("[AUDIT] About to create a entity: {}", data);
    }

    @PostUpdate
    public void onUpdate(Object data) {
        log.info("[AUDIT] About to update a entity: {}", data);
    }

    @PostRemove
    public void onRemove(Object data) {
        log.info("[AUDIT] About to remove a entity: {}", data);
    }
}
