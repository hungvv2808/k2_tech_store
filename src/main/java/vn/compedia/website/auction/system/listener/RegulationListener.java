package vn.compedia.website.auction.system.listener;

import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

public class RegulationListener {

    @PostUpdate
    @PostRemove
    public void onChange(Object data) {

    }
}
