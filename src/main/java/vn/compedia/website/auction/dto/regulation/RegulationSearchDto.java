package vn.compedia.website.auction.dto.regulation;

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
public class RegulationSearchDto extends BaseSearchDto {

    private Long accountId;
    private Long regulationId;
    private Integer status;
    private Integer auctionStatus;
    private String code;
    private String nameAsset;
    private String nameAum;
    private String nameAuf;
    private Date startTime;
    private Date dateTo;
    private Date dateFrom;
    private Long auctionReqId;

    private Date toDate;
    private Date fromDate;
    private Long auctionFormalityId;
    private Long auctionMethodId;

    private Integer auctionStatusEnded;
    private Integer auctionStatusCanceled;
}
