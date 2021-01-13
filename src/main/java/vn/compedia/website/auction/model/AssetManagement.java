package vn.compedia.website.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="asset_management")
public class AssetManagement extends BaseModel {
    private static final long serialVersionUID = 2133144946104144269L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="asset_management_id")
    private Long assetManagementId;
    @Column(name="auction_register_id")
    private Long auctionRegisterId;
    @Column(name="money")
    private Long money;
    @Column(name="asset_id")
    private Long assetId;
    @Column(name="start_time")
    private Date startTime;
    @Column(name="end_time")
    private Date endTime;
    @Column(name= "ending")
    private boolean ending;
    @Column (name = "number_of_update_result")
    private Integer numberOfUpdateResult = 0;
}
