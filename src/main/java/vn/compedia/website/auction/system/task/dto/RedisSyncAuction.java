/*
 * Author: Thinh Hoang
 * Date: 09/2020
 * Company: Compedia Software
 * Email: thinhhv@compedia.vn
 * Personal Website: https://vnnib.com
 */

package vn.compedia.website.auction.system.task.dto;

import lombok.Getter;
import lombok.Setter;
import vn.compedia.website.auction.model.Bid;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class RedisSyncAuction implements Serializable {
    private static final long serialVersionUID = -7387377016468772895L;

    private String action;
    private AssetDtoQueue assetDtoQueue;
    private Bargain bargain;
    private Bid bid;
    private Long objectId;
    private Long objectId2;
    private List<Long> objectList;

    public RedisSyncAuction(String action, AssetDtoQueue assetDtoQueue) {
        this.action = action;
        this.assetDtoQueue = assetDtoQueue;
    }

    public RedisSyncAuction(String action, Bargain bargain) {
        this.action = action;
        this.bargain = bargain;
    }

    public RedisSyncAuction(String action, Long accountId, Bid bid) {
        this.action = action;
        this.objectId = accountId;
        this.bid = bid;
    }

    public RedisSyncAuction(String action, Long objectId) {
        this.action = action;
        this.objectId = objectId;
    }

    public RedisSyncAuction(String action, Long objectId, Long objectId2) {
        this.action = action;
        this.objectId = objectId;
        this.objectId2 = objectId2;
    }

    public RedisSyncAuction(String action, Long accountId, List<Long> objectList) {
        this.action = action;
        this.objectId = accountId;
        this.objectList = objectList;
    }
}
