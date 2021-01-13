package vn.compedia.website.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.util.DbConstant;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "auction_register")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionRegister extends BaseModel {
    private static final long serialVersionUID = 6822569875241615312L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_register_id")
    private Long auctionRegisterId;

    @Column(name = "code")
    private String code;

    @Column(name = "asset_id")
    private Long assetId;

    @Column(name = "regulation_id")
    private Long regulationId;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "status")
    private int status;

    @Column(name = "reason_deposition")
    private String reasonDeposition;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "reason_refuse")
    private String reasonRefuse;

    @Column(name = "status_deposit")
    private Integer statusDeposit = DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED;

    @Column(name = "auctioneer_id")
    private Long auctioneerId;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "create_by")
    private Long createBy;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "update_by")
    private Long updateBy;

    @Column(name = "status_refund")
    private Integer statusRefund;

    @Column(name = "status_refuse_win")
    private boolean statusRefuseWin;

    @Column(name = "time_joined")
    private Date timeJoined;

    @Column(name = "status_joined")
    private boolean statusJoined;

    public AuctionRegister(int status, Integer statusRefund) {
    }
}

