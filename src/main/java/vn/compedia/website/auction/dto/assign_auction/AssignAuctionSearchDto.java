package vn.compedia.website.auction.dto.assign_auction;

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
public class AssignAuctionSearchDto extends BaseSearchDto {
    private Long regulationId;
    private Long auctionMethodId;
    private Long auctioneerId;
    private Long auctionFormalityId;
    private Date startTime;
    private String code;
    private String peopleWAsset;
    private Integer status;
    private Integer auctionStatus;
    private List<Integer> auctionStatusList;
    private List<Integer> regulationStatus;
    private Integer accountStatus;
    private String nullAuctioneerId;
}
