package vn.compedia.website.auction.dto.auction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.model.AuctionRegister;
import vn.compedia.website.auction.model.AuctionRegisterFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuctionRegisterDto extends AuctionRegister {
    private String fullNameNguoiDangKy;
    private String fullNameDauGiaVien;
    private String email;
    private String phone;
    private Date startTime;
    private String name;
    private Long startingPrice;
    private Long priceStep;
    private String filePath;
    private Integer numberOfRounds;
    private Integer timePerRound;
    private Integer assetStatus;
    private Integer auctionRegister;
    private String cancerReason;
    private String regulationCode;
    private Integer auctionStatus;
    private Long auctionMethodId;
    private Long auctionFormalityId;
    private Long roleId;
    private List<String> assetImage;
    private Integer serialNumber;
    private boolean onlineStatus;
    private boolean ending;
    private boolean statusRetract;
    private boolean inConfirm; // is winner
    private boolean refuseWinner; // is refuse winner
    private String shortNameAsset;
    private String notes;
    private boolean isWinnerFirst;
    private String orgPhone;
    private boolean isOrg;
    private List<AuctionRegisterFile> auctionRegisterFileList;
    private String regulationFilePath;
    private Integer winnerSn; // stt winner
    private Long assetManagementWinnerId; // asset_management: auction_register_id
    private boolean assetManagementEndingFinal; // asset_management: ending final
    private Long assetManagementId; // asset_management: exists
    private boolean assetCancelPlaying;
    private String imageCardIdFrontPath;
    private String imageCardIdBackPath;
    private Map<String, String> accuracyCompanyFile; // file_path/file_name

    //Notify payment
    private Long amountReserve;
    private String filePathReserve;
    private String fileNameReserve;

    private Date regulationPaymentEndTime;

    public AuctionRegisterDto(String fullNameNguoiDangKy, String fullNameDauGiaVien, String email, String phone, Date startTime, String name, Long startingPrice, Long priceStep) {
        this.fullNameNguoiDangKy = fullNameNguoiDangKy;
        this.fullNameDauGiaVien = fullNameDauGiaVien;
        this.email = email;
        this.phone = phone;
        this.startTime = startTime;
        this.name = name;
        this.startingPrice = startingPrice;
        this.priceStep = priceStep;
    }

    public AuctionRegisterDto( int status, Integer statusRefund, Integer assetStatus) {
        super(status,statusRefund);
        this.assetStatus = assetStatus;
    }

    public AuctionRegister getParent() {
        AuctionRegister au = new AuctionRegister();
        au.setAuctionRegisterId(getAuctionRegisterId());
        au.setAccountId(getAccountId());
        au.setAssetId(getAssetId());
        au.setRegulationId(getRegulationId());
        au.setStatus(getStatus());
        au.setStatusDeposit(getStatusDeposit());
        au.setReasonRefuse(getReasonRefuse());
        au.setStatus(getStatus());
        return au;
    }
}
