package vn.compedia.website.auction.dto.auction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.ValueUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetDto extends Asset {
    private List<Account> accountList;
    private List<String> filePaths;
    private String filePath;
    private Integer numberOfRounds;
    private Integer currentRound = 1;
    private Integer timePerRound;
    private Date currentTime;
    private List<BidDto> bidDtoList;
    private List<AssetFile> assetFileList;
    private List<AssetImage> assetImageList;
    private String reasonCancelAsset;
    private List<AuctionRegister> auctionRegisterList = new ArrayList<>();
    private Long highestPrice;
    private Integer countAssetFile;
    private String assetImage;
    private Integer auctionStatus;
    private Date endTime;
    private Long auctionFormalityId;
    private String auctionFormalityName;
    private Long auctionMethodId;
    private String auctionMethodName;
    private List<PriceRoundDto> priceRoundList;
    private Date regulationStartTime;
    private Long assetManagementId;
    private boolean assetManagementEnding; // trang thai dau gia: thanh cong | that bai
    private boolean assetManagementEndingFinal; // trang thai dau gia: thanh cong | that bai
    private Long accountIdWinner;
    private boolean historyStatus;
    private boolean retractPrice;
    private Long money;
    private String typeAssetName;
    private Date realStartTime;
    // regulation
    private Long regulationId;
    private String regulationCode;
    private Integer regulationStatus;
    private Date startRegistrationDate;
    private Date endRegistrationDate;
    private String auctioneerFullName;
    // auction register
    private Long auctionRegisterId;
    private Integer auctionRegisterStatus;
    private Date paymentStartTime;
    private Date paymentEndTime;
    // now
    private Long priceRoundId;
    // payment status
    private Integer paymentStatus;

    //Infor winner
    private boolean org;
    private String fullNameRegister;
    //price win
    private Long winPrice;
    //Time recorded
    private Date timeRecorded;

    //For report regulation
    private Integer statusSigned;
    private String filePathReport;
    private String fileNameReport;

    private Integer statusRefund;

    public AssetDto(Long assetId, String name, Long startingPrice, Long priceStep, Integer status, Long priceRoundId, Integer currentRound, Integer auctionRegisterStatus, Long auctionRegisterId, Long auctionFormalityId, Long auctionMethodId, boolean assetManagementEnding, boolean assetManagementEndingFinal, Long highestPrice, boolean retractPrice, Date paymentStartTime, Date paymentEndTime, Integer statusRefund) {
        super(assetId, name, startingPrice, priceStep, status);
        this.priceRoundId = priceRoundId;
        this.currentRound = currentRound;
        this.auctionRegisterStatus = auctionRegisterStatus;
        this.auctionRegisterId = auctionRegisterId;
        this.auctionFormalityId = auctionFormalityId;
        this.auctionMethodId = auctionMethodId;
        this.assetManagementEnding = assetManagementEnding;
        this.assetManagementEndingFinal = assetManagementEndingFinal;
        this.highestPrice = highestPrice;
        this.retractPrice = retractPrice;
        this.paymentStartTime = paymentStartTime;
        this.paymentEndTime = paymentEndTime;
        this.statusRefund = statusRefund;
    }

    public AssetDto(Long assetId, Long regulationId, String name, Long startingPrice, Long priceStep, String description, Long deposit, Integer status, Long minPrice, Integer numericalOrder, Date startTime, Long typeAssetId, String typeAssetName) {
        super(assetId, regulationId, name, startingPrice, priceStep, description, deposit, status, minPrice, numericalOrder, startTime, typeAssetId);
        this.typeAssetName = typeAssetName;
    }

    public AssetDto(Long assetId, Long regulationId, String name, Long startingPrice, Long priceStep, String description, Long deposit, Integer status, Long minPrice, Integer numericalOrder, Date assetStartTime, Long typeAssetId) {
        super(assetId, regulationId, name, startingPrice, priceStep, description, deposit, status, minPrice, numericalOrder,assetStartTime, typeAssetId );
    }
    public AssetDto(Long assetId, Long regulationId, String name, Long startingPrice, Long priceStep, String description, Long deposit, Integer status, Long minPrice, Integer numericalOrder,Date assetStartTime ,Date startRegistrationDate ,Long typeAssetId) {
        super(assetId, regulationId, name, startingPrice, priceStep, description, deposit, status, minPrice, numericalOrder,assetStartTime ,typeAssetId);
        this.startRegistrationDate = startRegistrationDate;
    }
    public AssetDto(Long assetId, Long regulationId, String name, Long startingPrice, Long priceStep, String description, Long deposit, Integer status, Long minPrice, String assetImage,Integer numericalOrder,Date assetStartTime ,Long typeAssetId) {
        super(assetId, regulationId, name, startingPrice, priceStep, description, deposit, status, minPrice, numericalOrder,assetStartTime,typeAssetId);
        this.assetImage = assetImage;
    }

    public AssetDto(Long assetId, Long regulationId, String name, Long startingPrice, Long priceStep, String assetImage) {
        super(assetId, regulationId, name, startingPrice, priceStep);
        this.assetImage = assetImage;
    }

    public AssetDto(Long assetId, String name, Integer status) {
        super(assetId, name, status);
    }

    public AssetDto(Long assetId,String name,String regulationCode){
        super(assetId, name);
        this.regulationCode = regulationCode;
    }
    public AssetDto(Long assetId, int status, Boolean ending, Date startTime) {
        super(assetId, status, startTime);
        this.assetManagementEnding = ending != null && DbConstant.ASSET_MANAGEMENT_ENDING_GOOD == ending;
    }
}
