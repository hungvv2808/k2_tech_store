package vn.compedia.website.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "auction_req")
public class AuctionReq extends BaseModel {
    private static final long serialVersionUID = -779779414123156071L;

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

    public AuctionReq(Date createDate, Date updateDate, Long createBy, Long updateBy, Long auctionReqId, Long accountId, String assetName, String assetDescription, int status) {
        super(createDate, updateDate, createBy, updateBy);
        this.auctionReqId = auctionReqId;
        this.accountId = accountId;
        this.assetName = assetName;
        this.assetDescription = assetDescription;
        this.status = status;
    }
}

