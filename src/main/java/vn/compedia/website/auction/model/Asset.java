package vn.compedia.website.auction.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.system.listener.AssetListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "asset")
@EntityListeners(AssetListener.class)
public class Asset extends BaseModel {
    private static final long serialVersionUID = 7408505334968652311L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_id")
    private Long assetId;

    @Column(name = "regulation_id")
    private Long regulationId;

    @Column(name = "name")
    private String name;

    @Column(name = "starting_price")
    private Long startingPrice;

    @Column(name = "price_step")
    private Long priceStep;

    @Column(name = "description")
    @JsonIgnore
    private String description;

    @Column(name = "deposit")
    private Long deposit;

    @Column(name = "status")
    private Integer status;

    @Column(name = "min_price")
    private Long minPrice;

    @Column(name = "serial_number")
    private Integer numericalOrder;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "type_asset_id")
    private Long typeAssetId;

    @Column(name = "is_cancel_playing")
    private boolean cancelPlaying;

    public Asset(Long assetId, Long regulationId, String name, Long startingPrice, Long priceStep, String description, Long deposit, Integer status, Long minPrice, Integer numericalOrder, Date startTime, Long typeAssetId) {
        this.assetId = assetId;
        this.regulationId = regulationId;
        this.name = name;
        this.startingPrice = startingPrice;
        this.priceStep = priceStep;
        this.description = description;
        this.deposit = deposit;
        this.status = status;
        this.minPrice = minPrice;
        this.numericalOrder = numericalOrder;
        this.startTime = startTime;
        this.typeAssetId = typeAssetId;
    }

    public Asset(Long assetId, String name, Long startingPrice, Long priceStep, Integer status) {
        this.assetId = assetId;
        this.name = name;
        this.startingPrice = startingPrice;
        this.priceStep = priceStep;
        this.status = status;
    }

    public Asset(Long assetId, Long regulationId, String name, Long startingPrice, Long priceStep) {
        this.assetId = assetId;
        this.regulationId = regulationId;
        this.name = name;
        this.startingPrice = startingPrice;
        this.priceStep = priceStep;
    }

    public Asset(Long assetId, String name) {
        this.assetId = assetId;
        this.name = name;
    }

    public Asset(Long assetId, String name, Integer status) {
        this.assetId = assetId;
        this.name = name;
        this.status = status;
    }
    public Asset(Long assetId, int status, Date startTime){
        this.assetId = assetId;
        this.status = status;
        this.startTime = startTime;
    }
}
