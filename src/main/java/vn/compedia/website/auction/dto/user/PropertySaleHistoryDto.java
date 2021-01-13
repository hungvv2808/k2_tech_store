package vn.compedia.website.auction.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertySaleHistoryDto {
    // auction_register (thoi gian mua), auction req (thoi gian ban)
    private Long auctionId;
    private Date auctionDate;
    private Long auctionAccountId;
    private Integer auctionStatus;
    private Integer statusDeposit;
    private Integer statusRefund;
    private boolean statusRefuseWin;
    private boolean statusJoined;
    // 0/false: mua, 1/true: ban
    private boolean type;
    // asset
    private Long assetId;
    private String assetName;
    private Integer assetStatus;
    // regulation (ten quy che, so vong, thoi gian bat dau, thoi gian ket thuc, thoi gian moi vong)
    private Long regulationId;
    private String regulationCode;
    private Integer regulationNumberOfRounds;
    private Date regulationStartTime;
    private Date regulationEndTime;
    private Integer timePerRound;
    private List<RegulationDto> regulationDtoList;
    // regulation file (duong dan file quy che)
    private Long regulationFileId;
    private String filePath;
    // auction formality (hinh thuc)
    private Long auctionFormalityId;
    private String auctionFormalityName;
    // auction method (phuong thuc)
    private Long auctionMethodId;
    private String auctionMethodName;
    // asset_management
    private boolean assetManagementStatusEnding;
    // asset_management final
    private boolean assetManagementStatusEndingAll;

    // list detail
    private List<AssetDto> assetDtoList;

    // payment status
    private Integer paymentStatus;

    private Long winnerId;

    public PropertySaleHistoryDto(String regulationCode, Integer regulationNumberOfRounds, Date regulationStartTime, Date regulationEndTime, Integer timePerRound, String auctionFormalityName, List<AssetDto> assetDtoList) {
        this.regulationCode = regulationCode;
        this.regulationNumberOfRounds = regulationNumberOfRounds;
        this.regulationStartTime = regulationStartTime;
        this.regulationEndTime = regulationEndTime;
        this.timePerRound = timePerRound;
        this.auctionFormalityName = auctionFormalityName;
        this.assetDtoList = assetDtoList;
    }
}
