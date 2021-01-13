package vn.compedia.website.auction.system.task.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bargain implements Serializable {
    private static final long serialVersionUID = -5688307443537915011L;

    private Long bidId;
    private Long priceRoundId;
    private Long accountId;
    private Long auctionRegisterId;
    private Long money;
    private Date createDate;
    private Long assetId;

    public Bargain(Long priceRoundId, Long accountId, Long auctionRegisterId, Long money) {
        this.priceRoundId = priceRoundId;
        this.accountId = accountId;
        this.auctionRegisterId = auctionRegisterId;
        this.money = money;
        this.createDate = new Date();
    }
}
