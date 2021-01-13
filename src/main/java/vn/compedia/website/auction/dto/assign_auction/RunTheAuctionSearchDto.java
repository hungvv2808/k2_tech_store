package vn.compedia.website.auction.dto.assign_auction;

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
public class RunTheAuctionSearchDto extends BaseSearchDto {
    private Long regulationId;
    private Long auctionRedId;
    private Long auctionMethodId;
    private Long auctionFormalityId;
    private Long auctioneerId;
    private Long createBy;
    private Long updateBy;
    private Integer numberOfRounds;
    private int historyStatus;
    private int status;
    private double timePerRound;
    private Date startTime;
    private Date startRegistrationDate;
    private Date endRegistrationDate;
    private Date realStartTime;
    private Date realEndTime;
    private Date timeStart;
    private Date timeEnd;
    private Long createDate;
    private Date updateDate;
    private String code;
    private Integer auctionStatus;
}
