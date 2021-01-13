package vn.compedia.website.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "price_round")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceRound extends BaseModel {
    private static final long serialVersionUID = 6125386845589639085L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_round_id")
    private Long priceRoundId;

    @Column(name = "regulation_id")
    private Long regulationId;

    @Column(name = "asset_id")
    private Long assetId;

    @Column(name = "number_of_round")
    private Integer numberOfRound;

    @Column(name = "starting_price")
    private Long startingPrice;

    @Column(name = "highest_price")
    private Long highestPrice;

    @Column(name = "auction_register_id")
    private Long auctionRegisterId;
}
