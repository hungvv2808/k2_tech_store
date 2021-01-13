package vn.compedia.website.auction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.dto.BaseSearchDto;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuctionReqSearchDto extends BaseSearchDto {
    private Long auctionReqId;
    private Long accountId;
    private String assetName;
    private String assetDescription;
    private Integer status;
    protected Date createDate;
    protected Date updateDate;
    protected Long createBy;
    protected Long updateBy;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private Date dateTo;
    private Date dateFrom;
    private Long proviceId;
    private Long districtId;
    private Long communeId;
    private String fullNameNguoiDangKy;
}
