package vn.compedia.website.auction.dto.auction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.dto.BaseSearchDto;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuctionRegisterSearchDto extends BaseSearchDto {
    private String keyword;
    private String fullNameDauGiaVien;
    private String fullNameNguoiDangKy;
    private Long regulationId;
    private Long accountId;
    private String fullName;
    private String email;
    private String phone;
    private Date startDateSearch;
    private Date endDateSearch;
    private String code;
    private String name;
    private Integer status;
    private Integer statusDeposit;
    private List<Integer> statusList;
    private Long auctionMethodId;
    private Long auctionFormalityId;
    private Long assetId;
    private String regulationCode;
    private Integer statusRefund;
    private Integer assetStatusCancel;
    private Integer assetStatus;
    private boolean auctionRegisterRefundPrice;
    //filter
    private Integer regulationStatus;
    private List<Integer> regulationAuctionStatusList;
}
