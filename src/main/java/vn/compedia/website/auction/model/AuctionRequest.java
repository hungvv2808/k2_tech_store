package vn.compedia.website.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auction_req")
public class AuctionRequest extends BaseModel {
    private static final long serialVersionUID = 1267796812587486605L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_req_id")
    private Long auctionReqId;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "asset_description")
    private String assetDescription;

    @Column(name = "status")
    private int status;
}
