package vn.compedia.website.auction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.model.AuctionReq;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuctionReqDto extends AuctionReq {
    private String fullNameNguoiDangKy;
    private String fullName;
    private String orgName;
    private String email;
    private String phone;
    private String address;
    private Long proviceId;
    private Long districtId;
    private Long communeId;
    private String nameProvice;
    private String nameDistrict;
    private String nameCommune;
    private boolean org;

    public AuctionReqDto(Date createDate, Date updateDate, Long createBy, Long updateBy, Long auctionReqId, Long accountId,
                         String assetName, String assetDescription, int status, String fullName, String email, String phone, String address) {
        super(createDate, updateDate, createBy, updateBy, auctionReqId, accountId, assetName, assetDescription, status);
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public AuctionReq getParent() {
        AuctionReq  ar = new AuctionReq();
        ar.setAuctionReqId(getAuctionReqId());
        ar.setAccountId(getAccountId());
        ar.setAssetName(getAssetName());
        ar.setAssetDescription(getAssetDescription());
        ar.setStatus(getStatus());
        return ar;
    }
}
