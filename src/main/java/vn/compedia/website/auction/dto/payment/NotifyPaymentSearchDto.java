package vn.compedia.website.auction.dto.payment;

import lombok.Getter;
import lombok.Setter;
import vn.compedia.website.auction.dto.BaseSearchDto;

import java.util.Date;

@Getter
@Setter
public class NotifyPaymentSearchDto extends BaseSearchDto {
    private Long auctionRegisterId;
    private Long auctionRegisterCode;
    private Long accountId;
    private Long assetId;
    private String assetName;
    private Long regulationId;
    private String regulationCode;
    private Integer status;
    private Date fromDate;
    private Date toDate;
}
