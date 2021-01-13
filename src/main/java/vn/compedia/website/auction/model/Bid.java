package vn.compedia.website.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "bid")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bid extends BaseModel {
    private static final long serialVersionUID = -1370515418212538784L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id")
    private Long bidId;

    @Column(name = "price_round_id")
    private Long priceRoundId;

    @Column(name = "auction_register_id")
    private Long auctionRegisterId;

    @Column(name = "asset_id")
    private Long assetId;

    @Column(name = "money")
    private Long money;

    @Column(name = "status_retract")
    private boolean statusRetract;

    @Column(name = "winner_sn")
    private Integer winnerSn;

    @Column(name = "winner_nth")
    private Integer winnerNth;

    @Column(name = "time")
    private Date time;

    public Bid(Long priceRoundId, Long auctionRegisterId, Long assetId, Long money, Date time) {
        this.priceRoundId = priceRoundId;
        this.auctionRegisterId = auctionRegisterId;
        this.assetId = assetId;
        this.money = money;
        this.time = time;
    }

    public Bid(Date time, Long bidId, Long priceRoundId, Long auctionRegisterId, Long assetId, Long money) {
        this.time = time;
        this.bidId = bidId;
        this.priceRoundId = priceRoundId;
        this.auctionRegisterId = auctionRegisterId;
        this.assetId = assetId;
        this.money = money;
    }

    public Bid(Date createDate, Long bidId, Long priceRoundId, Long auctionRegisterId, Long assetId, Long money, Integer winnerSn) {
        super(createDate);
        this.bidId = bidId;
        this.priceRoundId = priceRoundId;
        this.auctionRegisterId = auctionRegisterId;
        this.assetId = assetId;
        this.money = money;
        this.winnerSn = winnerSn;
    }
}
