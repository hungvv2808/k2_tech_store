package vn.compedia.website.auction.system.listener;

import vn.compedia.website.auction.model.Bid;

import javax.persistence.PostPersist;

public class BidListener {

    @PostPersist
    public void onCreate(Object data) {
        Bid bid = (Bid) data;
        Long accountId = bid.getCreateBy();

    }
}
