package vn.compedia.website.auction.controller.frontend.auction;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.frontend.AuthorizationFEController;
import vn.compedia.website.auction.controller.frontend.BaseFEController;
import vn.compedia.website.auction.controller.frontend.CommonFEController;
import vn.compedia.website.auction.controller.frontend.common.PaginationAjaxController;
import vn.compedia.website.auction.controller.frontend.common.UploadMultipleFileFEController;
import vn.compedia.website.auction.dto.auction.*;
import vn.compedia.website.auction.dto.common.UploadMultipleFileNameDto;
import vn.compedia.website.auction.entity.frontend.MyParticipant;
import vn.compedia.website.auction.entity.frontend.MyPriceRoundHistory;
import vn.compedia.website.auction.exception.AuctionException;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.model.payment.Payment;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.system.socket.SocketServer;
import vn.compedia.website.auction.system.socket.SocketUpdater;
import vn.compedia.website.auction.system.task.AuctionTask;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.system.task.dto.Bargain;
import vn.compedia.website.auction.util.*;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Log4j2
@Named
@Scope(value = "session")
@Getter
@Setter
public class AuctionController extends BaseFEController {
    @Inject
    private AuctionTask auctionTask;
    @Inject
    private UploadMultipleFileFEController uploadMultipleFileFEController;
    @Inject
    private AuthorizationFEController authorizationFEController;
    @Inject
    private SocketUpdater socketUpdater;
    @Inject
    private CommonFEController commonFEController;
    @Autowired
    private AuctionReqRepository auctionReqRepository;
    @Autowired
    private RegulationRepository regulationRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private PriceRoundRepository priceRoundRepository;
    @Autowired
    private AssetManagementRepository assetManagementRepository;
    @Autowired
    private ContactInfoRepository contactInfoRepository;
    @Autowired
    private AssetFileRepository assetFileRepository;
    @Autowired
    private RegulationFileRepository regulationFileRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AuctionRegisterFileRepository auctionRegisterFileRepository;

    private final String ERROR_MESSAGE_HIDE = "Lỗi không xác định";

    // paginate
    private PaginationAjaxController<String> paginationPriceRound;
    private PaginationAjaxController<String> paginationHistoryBargainFormalityDirectUp;
    private PaginationAjaxController<String> paginationRegisterList;
    // ID asset
    private AssetDto assetDto;
    private AssetFile assetFileCancel;
    // onBargain
    private Long money;
    private Long priceRoundId;
    //
    private List<AssetDto> assetList;
    private AuctionRegister auctionRegister;
    private AssetSearchDto assetSearchDto;
    private AssetSearchDto assetsSearch;
    private AuctionReq auctionReq;
    private PriceRoundSearchDto priceRoundSearchDto;
    private AuctionRegisterSearchDto auctionRegisterSearchDto;
    private BidSearchDto bidSearchDto;
    private Regulation regulation;
    private List<Bid> bidList;
    private Long assetId;
    private AssetManagement assetManagement;
    private Date now;
    private Payment payment;
    // regulation file cancel
    private RegulationFile regulationFile;
    // date unix
    private Date dateUnix = new Date();

    public AuctionController() {
        this.paginationPriceRound = new PaginationAjaxController<>(new LazyDataModel<String>() {
            @Override
            public List<String> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map filters) {
                return new ArrayList<>();
            }
        });
        this.paginationRegisterList = new PaginationAjaxController<>(new LazyDataModel<String>() {
            @Override
            public List<String> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map filters) {
                return new ArrayList<>();
            }
        });
        this.paginationHistoryBargainFormalityDirectUp = new PaginationAjaxController<>(new LazyDataModel<String>() {
            @Override
            public List<String> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map filters) {
                return new ArrayList<>();
            }
        });
    }

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback() && (assetId == null || getNow().compareTo(DateUtil.plusSecond(dateUnix, 1)) > 0)) {
            init();
            if (!initAssetId()) {
                return;
            }
            resetAll();
            dateUnix = getNow();
        }
    }

    public void resetAll() {
        loadAssetDto();

        if (assetDto == null) {
            return;
        }
        // auction register
        loadAuctionRegister();

        regulation = regulationRepository.findRegulationByRegulationId(assetDto.getRegulationId());
        auctionReq = auctionReqRepository.findAuctionReqByAuctionReqId(regulation.getAuctionReqId());
        uploadMultipleFileFEController.resetAll(null);
        bidSearchDto = new BidSearchDto();
    }

    public void onSaveRegister() {
        UploadMultipleFileNameDto uploadMultipleFileNameDto = uploadMultipleFileFEController.getUploadMultipleFileNameDto();
//        if (uploadMultipleFileNameDto.getListToShow().isEmpty()) {
//            setErrorForm("Vui lòng chọn file hồ sơ");
//            return;
//        }
        if (auctionRegisterRepository.existsAuctionRegisterByAccountIdAndAssetId(authorizationFEController.getAccountDto().getAccountId(), assetDto.getAssetId())) {
            setErrorForm("Bạn đã đăng ký tham gia đấu giá tài sản này rồi");
            return;
        }
        regulation = regulationRepository.findRegulationByRegulationId(assetDto.getRegulationId());
        if (getNow().compareTo(regulation.getStartRegistrationDate()) < 0) {
            setErrorForm("Rất tiếc, chưa đến thời gian đăng ký tham gia đấu giá");
            return;
        }
        if (getNow().compareTo(regulation.getEndRegistrationDate()) > 0) {
            setErrorForm("Rất tiếc, thời gian đăng ký tham gia đã kết thúc");
            return;
        }

        auctionRegister = new AuctionRegister();
        auctionRegister.setStatus(DbConstant.AUCTION_REGISTER_STATUS_WAITING);
        auctionRegister.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_ZERO);
        auctionRegister.setAccountId(authorizationFEController.getAccountDto().getAccountId());
        auctionRegister.setAssetId(assetDto.getAssetId());
        auctionRegister.setRegulationId(assetDto.getRegulationId());
        auctionRegister.setCreateDate(new Date());
        if (auctionRegister.getAuctionRegisterId() == null) {
            auctionRegister.setCreateBy(authorizationFEController.getAccountDto().getAccountId());
        }
        auctionRegister.setUpdateBy(authorizationFEController.getAccountDto().getAccountId());
        // save
        auctionRegisterRepository.save(auctionRegister);
        String auctionRegisterCode = String.format("NTG%07d", auctionRegister.getAuctionRegisterId());
        auctionRegister.setCode(auctionRegisterCode);
        auctionRegisterRepository.save(auctionRegister);

        // save file
        if (!uploadMultipleFileNameDto.getListToShow().isEmpty()) {
            List<AuctionRegisterFile> auctionRegisterFileList = new ArrayList<>();
            uploadMultipleFileNameDto.getListToShow().forEach((k, v) -> auctionRegisterFileList.add(new AuctionRegisterFile(null, auctionRegister.getAuctionRegisterId(), v, k)));
            auctionRegisterFileRepository.saveAll(auctionRegisterFileList);
        }

        setSuccessForm("Bạn đã nộp hồ sơ thành công, vui lòng nộp tiền đặt trước trong thời gian quy định");
        facesNotice().closeModal("auctionRegisterPopup");
        facesNotice().activeFunction("cmdRefreshData");

        // reload asset register
        loadAssetJoined();
    }

    public void onAcceptWinner() {
        try {
            auctionTask.onAcceptWinner(assetId, authorizationFEController.getAccountDto().getAccountId());
            setSuccessForm("Chấp nhận thắng cuộc thành công. Vui lòng đợi hệ thống xử lý.");
        } catch (AuctionException e) {
            setErrorForm(e.getMessage());
        }
    }

    public void onRefuseWinner() {
        try {
            auctionTask.onRefuseWinner(assetId, authorizationFEController.getAccountDto().getAccountId());
            setSuccessForm("Từ chối thành công");
        } catch (AuctionException e) {
            setErrorForm(e.getMessage());
        }
    }

    public void onRetractPrice() {
        try {
            auctionTask.onRetractPrice(assetId, authorizationFEController.getAccountDto().getAccountId());
            setSuccessForm("Rút lại giá thành công");
        } catch (AuctionException e) {
            setErrorForm(e.getMessage());
        }
    }

    public void onAcceptPrice() {
        try {
            Bid bid = new Bid(
                    priceRoundId,
                    auctionRegister.getAuctionRegisterId(),
                    assetDto.getAssetId(),
                    money,
                    new Date()
            );
            auctionTask.onAcceptPrice(bid, authorizationFEController.getAccountDto().getAccountId());
            // save
            bidRepository.saveAndFlush(bid);
            // notify
            setSuccessForm("Đã ghi nhận, xin đợi hệ thống xử lý...");
        } catch (AuctionException e) {
            setErrorForm(e.getMessage());
        }
    }

    public synchronized void onBargain() {
        // processing save
        try {
            Bid bid = new Bid(
                    priceRoundId,
                    auctionRegister.getAuctionRegisterId(),
                    assetDto.getAssetId(),
                    money,
                    new Date()
            );
            // bargain
            auctionTask.onBargain(assetId, authorizationFEController.getAccountDto().getAccountId(), bid);
            // notify
            setSuccessForm("Trả giá thành công!");
        } catch (AuctionException e) {
            setErrorForm(e.getMessage());
        } catch (Exception e) {
            log.info("[onBargain] error cause: ", e);
            setErrorForm("Có lỗi trong quá trình trả giá");
        }
    }

    // lịch sủ trả giá với hình thức bỏ phiếu
    private void loadHistoryBargain() {
        this.paginationPriceRound = new PaginationAjaxController<>(new LazyDataModel<String>() {
            @Override
            public List<String> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map filters) {
                return new ArrayList<>();
            }
        });

        paginationPriceRound.setLazyDataModel(new LazyDataModel<String>() {
            @Override
            public List<String> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                priceRoundSearchDto = new PriceRoundSearchDto();
                priceRoundSearchDto.setAssetId(assetId);
                priceRoundSearchDto.setStateEnd(true);
                priceRoundSearchDto.setPageIndex(first);
                priceRoundSearchDto.setPageSize(pageSize);
                priceRoundSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder == null || sortOrder.equals(SortOrder.DESCENDING)) {
                    sort = "DESC";
                } else {
                    sort = "ASC";
                }
                priceRoundSearchDto.setSortOrder(sort);
                List<String> temp = new ArrayList<>();
                temp.add(buildHistoryBargainString(priceRoundRepository.searchForUser(priceRoundSearchDto)));
                return temp;
            }

            @Override
            public int getRowCount() {
                return priceRoundRepository.countSearchForUser(priceRoundSearchDto).intValue();
            }
        });
        paginationPriceRound.loadData();
    }

    // Lịch sử trả giá với hình thức trả giá trực tiếp trả giá lên
    private void loadHistoryBargainFormalityDirect() {
        this.paginationHistoryBargainFormalityDirectUp = new PaginationAjaxController<>(new LazyDataModel<String>() {
            @Override
            public List<String> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map filters) {
                return new ArrayList<>();
            }
        });

        paginationHistoryBargainFormalityDirectUp.setLazyDataModel(new LazyDataModel<String>() {
            @Override
            public List<String> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                bidSearchDto = new BidSearchDto();
                bidSearchDto.setAssetId(assetId);
                bidSearchDto.setPageIndex(first);
                bidSearchDto.setPageSize(pageSize);
                bidSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder == null || sortOrder.equals(SortOrder.DESCENDING)) {
                    sort = "DESC";
                } else {
                    sort = "ASC";
                }
                bidSearchDto.setSortOrder(sort);
                List<String> temp = new ArrayList<>();
                temp.add(buildHistoryBargainDirectUpString(bidRepository.searchForHistoryFormalityDirectUp(bidSearchDto)));
                return temp;
            }

            @Override
            public int getRowCount() {
                return bidRepository.countSearchForHistoryFormalityDirectUp(bidSearchDto).intValue();
            }
        });
        paginationHistoryBargainFormalityDirectUp.loadData();
    }

    private void loadParticipantsList() {
        paginationRegisterList.setLazyDataModel(new LazyDataModel<String>() {
            @Override
            public List<String> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                auctionRegisterSearchDto = new AuctionRegisterSearchDto();
                auctionRegisterSearchDto.setAssetId(assetId);
                auctionRegisterSearchDto.setStatus(DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED);
                auctionRegisterSearchDto.setPageIndex(first);
                auctionRegisterSearchDto.setPageSize(pageSize);
                auctionRegisterSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder == null || sortOrder.equals(SortOrder.DESCENDING)) {
                    sort = "DESC";
                } else {
                    sort = "ASC";
                }
                auctionRegisterSearchDto.setSortOrder(sort);
                List<String> temp = new ArrayList<>();
                temp.add(buildRegisterString(auctionRegisterRepository.searchForGuest(auctionRegisterSearchDto)));
                return temp;
            }

            @Override
            public int getRowCount() {
                return auctionRegisterRepository.countSearchForGuest(auctionRegisterSearchDto).intValue();
            }
        });
        paginationRegisterList.loadData();
    }

    // Lịch sử trả giá hình thức bỏ phiếu
    private String buildHistoryBargainString(List<PriceRoundDto> priceRoundDtoList) {
        List<MyPriceRoundHistory> myPriceRoundHistoryList = new ArrayList<>();
        for (PriceRoundDto priceRoundDto : priceRoundDtoList) {
            myPriceRoundHistoryList.add(new MyPriceRoundHistory(
                    DateUtil.formatToPattern(priceRoundDto.getUpdateDate(), DateUtil.HHMMSS_DDMMYYYY),
                    priceRoundDto.getNumberOfRound(),
                    priceRoundDto.getHighestPrice()
            ));
        }
        return StringUtil.toJson(myPriceRoundHistoryList);
    }

    // Lịch sử trả giá hình thức trả giá trực tiếp, phương thức trả giá lên
    private String buildHistoryBargainDirectUpString(List<BidDto> bidDtoList) {
        List<MyPriceRoundHistory> myPriceRoundHistoryList = new ArrayList<>();
        for (BidDto bidDto : bidDtoList) {
            String timeString = DateUtil.formatToPattern(bidDto.getTime(), DateUtil.HHMMSS_DDMMYYYY);
            if (assetDto.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_POLL) {
                timeString = "Vòng " + bidDto.getNumberOfRound() + " - " + timeString;
            }
            myPriceRoundHistoryList.add(new MyPriceRoundHistory(
                    timeString,
                    bidDto.getCode(),
                    bidDto.getMoney()
            ));
        }
        return StringUtil.toJson(myPriceRoundHistoryList);
    }

    private String buildRegisterString(List<AuctionRegisterDto> auctionRegisterDtoList) {
        // build confirm
        buildInfoConfirm(auctionRegisterDtoList);
        //
        List<MyParticipant> myParticipantList = new ArrayList<>();
        for (AuctionRegisterDto auctionRegisterDto : auctionRegisterDtoList) {
            myParticipantList.add(new MyParticipant(
                    auctionRegisterDto.getAuctionRegisterId(),
                    auctionRegisterDto.getCode(),
                    auctionRegisterDto.getRoleId(),
                    auctionRegisterDto.getNotes()
            ));
        }
        return StringUtil.toJson(myParticipantList);
    }

    private String getNotes(AuctionRegisterDto auctionRegisterDto, AssetDtoQueue assetDtoQueue) {
        List<String> str = new ArrayList<>();

        if (!auctionRegisterDto.isStatusJoined() && Arrays.asList(
                DbConstant.ASSET_STATUS_ENDED,
                DbConstant.ASSET_STATUS_NOT_SUCCESS,
                DbConstant.ASSET_STATUS_CANCELED
            ).contains(auctionRegisterDto.getAssetStatus())) {
            if (auctionRegisterDto.getStatusDeposit() == DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_REFUSE
                    && (auctionRegisterDto.getFilePath() != null || (assetDtoQueue != null && !assetDtoQueue.isEnded()) || auctionRegisterDto.isStatusJoined())) {
                str.add("Đã bị truất quyền" + commonFEController.statusDeposit(auctionRegisterDto.getFilePath() != null, auctionRegisterDto.isStatusRetract()));
            } else {
                str.add("Không tham gia");
            }
        } else {
            if (auctionRegisterDto.isStatusRetract()) {
                str.add("Đã rút lại giá");
            }
            if (auctionRegisterDto.isInConfirm()) {
                str.add("Đang xác nhận trúng đấu giá");
            }
            if (auctionRegisterDto.isRefuseWinner() || auctionRegisterDto.isStatusRefuseWin()) {
                str.add("Đã từ chối trúng đấu giá");
            } else if (auctionRegisterDto.isWinnerFirst()) {
                str.add("Không thanh toán tiền trúng đấu giá");
            } else if (auctionRegisterDto.getStatusDeposit() == DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_REFUSE) {
                str.add("Đã bị truất quyền" + commonFEController.statusDeposit(auctionRegisterDto.getFilePath() != null, auctionRegisterDto.isStatusRetract()));
            }
            if (auctionRegisterDto.isEnding()) {
                str.add("Người thắng đấu giá");
            }
            if (str.isEmpty() && auctionRegisterDto.isStatusJoined() && (assetDtoQueue == null || assetDtoQueue.isEnded())) {
                str.add("Đã tham gia");
            }
        }
        return String.join(", ", str);
    }

    private void buildInfoConfirm(List<AuctionRegisterDto> auctionRegisterDtoList) {
        AssetDtoQueue assetDtoQueue = auctionTask.getAssetDtoList().get(assetDto.getAssetId());
        BidDto bidWinner1th = null;
        AssetManagement assetManagement = null;
        Bargain bargain = null;
        if (assetDtoQueue != null) {
            bargain = assetDtoQueue.getWinner();
        }
        if (auctionRegisterDtoList.size() > 0 && Arrays.asList(
                DbConstant.ASSET_STATUS_ENDED,
                DbConstant.ASSET_STATUS_NOT_SUCCESS,
                DbConstant.ASSET_STATUS_CANCELED
            ).contains(auctionRegisterDtoList.get(0).getAssetStatus())) {
            assetManagement = assetManagementRepository.findAssetManagementByAssetId(assetDto.getAssetId());
            if (assetManagement != null) {
                List<BidDto> bidDtoList = bidRepository.findBidsByAssetId(assetId);
                for (BidDto bidDto : bidDtoList) {
                    if (bidDto.getWinnerSn() != null && DbConstant.BID_WINNER_SN_1TH == bidDto.getWinnerSn()) {
                        bidWinner1th = bidDto;
                        break;
                    }
                }
            }
        }
        for (AuctionRegisterDto auctionRegisterDto : auctionRegisterDtoList) {
            // info confirm
            if (assetDtoQueue != null && assetDtoQueue.isInTimeConfirm() && bargain != null) {
                // check doing confirm?
                if (auctionRegisterDto.getAccountId().equals(bargain.getAccountId())) {
                    auctionRegisterDto.setInConfirm(!assetDtoQueue.isEnded() && !assetDtoQueue.isInTimeRandom());
                }
                // check 1th refused?
                if (assetDtoQueue.getRoundConfirm() == 2
                        && !assetDtoQueue.isDeposit1th()
                        && auctionRegisterDto.getAccountId().equals(assetDtoQueue.getWinner1th().getAccountId())
                        && !auctionRegisterDto.getAccountId().equals(assetDtoQueue.getWinner().getAccountId())
                ) {
                    auctionRegisterDto.setRefuseWinner(true);
                }
            }
            if (assetManagement != null && bidWinner1th != null) {
                if ((assetManagement.isEnding()
                            && !assetManagement.getAuctionRegisterId().equals(auctionRegisterDto.getAuctionRegisterId())
                            && bidWinner1th.getAuctionRegisterId().equals(auctionRegisterDto.getAuctionRegisterId()))
                        || (!assetManagement.isEnding() && auctionRegisterDto.getAuctionRegisterId().equals(assetManagement.getAuctionRegisterId()))) {
                    auctionRegisterDto.setWinnerFirst(true);
                }
            }
            // set notes
            auctionRegisterDto.setNotes(getNotes(auctionRegisterDto, assetDtoQueue));
        }
    }

    public void loadAssetDto() {

        if (!initAssetId()) {
            return;
        }

        assetSearchDto = new AssetSearchDto();
        assetDto = new AssetDto();
        assetSearchDto.setAssetId(assetId);
        if (authorizationFEController.hasLogged()) {
            assetSearchDto.setAccountId(authorizationFEController.getAccountDto().getAccountId());
        }
        assetDto = assetRepository.getAssetDtoByAssetId(assetSearchDto);

        if (assetDto == null) {
            assetId = null;
            FacesUtil.redirect("/error/404.xhtml");
            return;
        }

        loadAssetList();
        loadMoreInfoAsset();
    }

    public void loadMoreInfoAsset() {
        try {
            // load asset file cancel
            assetFileCancel = assetFileRepository.findAssetFileByAssetIdAndType(assetId, DbConstant.ASSET_FILE_TYPE_CANCEL);
            if (DbConstant.ASSET_STATUS_CANCELED == assetDto.getStatus() && assetFileCancel == null) {
                // load regulation cancel
                Regulation regulation = regulationRepository.findById(assetDto.getRegulationId()).orElse(new Regulation());
                RegulationFile regulationFile = regulationFileRepository.findRegulationFileByRegulationIdAndType(assetDto.getRegulationId(), DbConstant.REGULATION_FILE_TYPE_HUY_BO);
                //
                assetFileCancel = new AssetFile();
                if (regulationFile != null) {
                    assetFileCancel.setFilePath(regulationFile.getFilePath());
                }
                assetFileCancel.setReasonCancelAsset(regulation.getReasonCancel());
            }

            // ended
            if (DbConstant.ASSET_STATUS_ENDED == assetDto.getStatus() || DbConstant.ASSET_STATUS_NOT_SUCCESS == assetDto.getStatus()) {
                assetManagement = assetManagementRepository.findAssetManagementByAssetId(assetId);
            }

            // load auctionRegister
            if (assetDto.getAuctionRegisterId() != null) {
                loadAuctionRegister();
            }

            if (checkRenderAuction()) {
                // load price round
                if (checkRenderHistoryBargainPoll()) {
                    loadHistoryBargain();
                }
                // load history direct
                if (checkRenderedHistoryBargainDirect()) {
                    loadHistoryBargainFormalityDirect();
                }
                // load register list
                loadParticipantsList();
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    public void loadAuctionRegister() {
        if (assetDto.getAuctionRegisterId() != null) {
            auctionRegister = auctionRegisterRepository.findById(assetDto.getAuctionRegisterId()).orElse(new AuctionRegister());
        } else {
            auctionRegister = null;
        }
    }

    public String formatDate(Date date) {
        return DateUtil.formatToPattern(date, DateUtil.MMDDYYYYHHMMSS);
    }

    public boolean checkCallback(Date date) {
        return new Date().compareTo(date) < 0;
    }

    public Date getNow() {
        return new Date();
    }

    public boolean showButtonRegister() {
        try {
            return commonFEController.showButtonRegister(assetDto, auctionReq);
        } catch (Exception e) {
            return false;
        }
    }

    public long countdownShowButton() {
        try {
            long temp = -1;

            // register button
            if (assetDto.getAuctionRegisterId() == null
                    && DbConstant.ASSET_STATUS_WAITING == assetDto.getStatus()
                    && !auctionReq.getAccountId().equals(authorizationFEController.getAccountDto().getAccountId())) {
                Date startRegistrationDate = DateUtil.formatDate(assetDto.getStartRegistrationDate(), DateUtil.DATE_FORMAT_MINUTE);
                Date endRegistrationDate = DateUtil.formatDate(assetDto.getEndRegistrationDate(), DateUtil.DATE_FORMAT_SECOND_END);
                // start register
                if (getNow().compareTo(startRegistrationDate) <= 0) {
                    temp = countMax(startRegistrationDate.getTime() - getNow().getTime());
                } else if (getNow().compareTo(endRegistrationDate) <= 0) {
                    temp = countMax(endRegistrationDate.getTime() - getNow().getTime());
                }
            }
            // payment button
            if (assetDto.getPaymentStartTime() != null && assetDto.getPaymentStartTime() != null) {
                Date paymentStartTime = DateUtil.formatDate(assetDto.getPaymentStartTime(), DateUtil.DATE_FORMAT_MINUTE);
                Date paymentEndTime = DateUtil.formatDate(assetDto.getPaymentEndTime(), DateUtil.DATE_FORMAT_SECOND_END);
                if (assetDto.getAuctionRegisterId() != null) {
                    if (getNow().compareTo(paymentStartTime) <= 0) {
                        temp = countMax(paymentStartTime.getTime() - getNow().getTime());
                    } else if (getNow().compareTo(paymentEndTime) <= 0) {
                        temp = countMax(paymentEndTime.getTime() - getNow().getTime());
                    }
                }
            }

            return temp;
        } catch (Exception e) {
            return -1;
        }
    }

    private long countMax(long number) {
        long maxCount = 24 * 60 * 60 * 1000;
        if (number <= maxCount) {
            return number;
        }
        return -1;
    }

    public boolean showButtonRegister(AssetDto dto) {
        return commonFEController.showButtonRegister(dto, auctionReq);
    }

    public void signUpToJoin(AssetDto dto) {
        if (!commonFEController.updateInfoBeforeJoin(dto)) {
            if (authorizationFEController.hasLogged()) {
                setErrorForm("Bạn cần cập nhật thêm thông tin để đăng ký tham gia đấu giá");
            }
            authorizationFEController.setAssetId(dto.getAssetId());
            return;
        }
        facesNotice().openModal("auctionRegisterPopup");
        assetDto = dto;
    }

    public String stateOnline() {
        List<AuctionRegister> auctionRegisterList = assetDto.getAuctionRegisterList();
        if (auctionRegisterList == null || auctionRegisterList.isEmpty()) {
            return "[]";
        }

        List<Long> auctionRegisterIdList = new ArrayList<>();
        for (AuctionRegister auctionRegister : auctionRegisterList) {
            if (SocketServer.isOnline(auctionRegister.getAccountId())) {
                auctionRegisterIdList.add(auctionRegister.getAuctionRegisterId());
            }
        }
        return StringUtil.toJson(auctionRegisterIdList);
    }

    public boolean checkRenderedHistoryBargainDirect() {
        boolean formality = false;
        boolean method = false;

        if (assetDto == null || assetDto.getAuctionFormalityId() == null) {
            return false;
        }

        if (assetDto.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_POLL
                && !Arrays.asList(DbConstant.ASSET_STATUS_WAITING, DbConstant.ASSET_STATUS_PLAYING).contains(assetDto.getStatus())) {
            return true;
        }

        if(assetDto.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_DIRECT) {
            formality = true;
        }
        if(assetDto.getAuctionMethodId().intValue() == DbConstant.AUCTION_METHOD_ID_UP) {
            method = true;
        }
        return formality && method;
    }

    public boolean checkRenderHistoryBargainPoll() {
        boolean formality = false;

        if (assetDto == null || assetDto.getAuctionFormalityId() == null) {
            return false;
        }

        if (assetDto.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_POLL) {
            formality = true;
        }

        if (!Arrays.asList(DbConstant.ASSET_STATUS_WAITING, DbConstant.ASSET_STATUS_PLAYING).contains(assetDto.getStatus())) {
            formality = false;
        }

        return formality;
    }

    public boolean checkRenderAuctionReq() {
        return authorizationFEController.hasLogged()
                && auctionReq != null
                && auctionReq.getAccountId().equals(authorizationFEController.getAccountDto().getAccountId());
    }

    public boolean checkRenderAuction() {
        if (assetDto != null
                &&  assetDto.getStatus() != null
                && DbConstant.ASSET_STATUS_WAITING != assetDto.getStatus()) {
            if (DbConstant.ASSET_STATUS_CANCELED == assetDto.getStatus() && assetDto.getPriceRoundId() == null) {
                return false;
            }
            if (assetDto.isHistoryStatus()) {
                return true;
            }
            if (authorizationFEController.hasLogged()) {
                return checkRenderAuctionReq()
                        || (assetDto.getAuctionRegisterId() != null
                            && assetDto.getAuctionRegisterStatus() == DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED);
            }
        }
        return false;
    }

    public String getPhoneNumberContact() {
        return contactInfoRepository.findById(DbConstant.MA_PHONE).orElse(new ContactInfo()).getContent();
    }

    public void getReasonDeposit() {
        if (auctionRegister.getReasonRefuse() != null) {
            facesNotice().openModal("rejectAssetDetailPopup");
        }
    }

    public boolean renderAreaWinner() {
        AssetDtoQueue assetDtoQueue = auctionTask.getAssetDtoList().get(assetId);

        return assetDtoQueue != null
                && assetDtoQueue.isInTimeConfirm()
                && !assetDtoQueue.isAcceptWinner()
                && assetDtoQueue.getWinner() != null
                && assetDtoQueue.getWinner().getAccountId().equals(authorizationFEController.getAccountDto().getAccountId());
    }

    public String convertDateToPattern(Date date) {
        return DateUtil.formatToPattern(date, DateUtil.HOUR_DATE_FORMAT);
    }

    private void loadAssetList() {
        assetsSearch = new AssetSearchDto();
        assetsSearch.setRegulationId(assetDto.getRegulationId());
        assetsSearch.setAccountId(authorizationFEController.getAccountDto().getAccountId());
        assetList = assetRepository.searchForGuestUser(assetsSearch);
    }

    public boolean renderMoreInfo() {
        return renderMoreInfoPoll()
                || renderMoreInfoDirectUp()
                || renderMoreInfoPayment()
                || renderMoreInfoPayment()
                || renderMoreInfoSuccess();
    }

    public boolean renderMoreInfoPoll() {
        return assetDto != null && assetDto.getAssetId() != null
                && assetDto.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_POLL;
    }

    public boolean renderMoreInfoDirectUp() {
        return assetDto != null
                && assetDto.getAssetId() != null
                && assetDto.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_DIRECT
                && assetDto.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_UP
                && assetDto.isRetractPrice();
    }

    public boolean renderMoreInfoPayment() {
        return auctionRegister != null
                && assetDto != null
                && assetDto.getAssetId() != null
                && auctionRegister.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_ZERO
                && assetDto.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_WAITING;
    }

    public boolean renderMoreInfoSuccess() {
        return auctionRegister != null
                && auctionRegister.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE
                && assetDto != null
                && assetDto.getStatus() != null
                && assetDto.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_WAITING;
    }

    public boolean renderButtonCancel() {
        if (assetDto == null) {
            return false;
        }
        AssetDtoQueue assetDtoQueue = auctionTask.getAssetDtoList().get(assetDto.getAssetId());
        return (assetDtoQueue != null && assetDtoQueue.isEnded() && !assetDtoQueue.isRefuseWinner() && assetDtoQueue.isCancel())
                || (assetDto.getStatus() != null && assetDto.getStatus() == DbConstant.ASSET_STATUS_CANCELED);
    }

    public boolean renderButtonDeposited() {
        AssetDtoQueue assetDtoQueue = auctionTask.getAssetDtoList().get(assetDto.getAssetId());
        return (assetDtoQueue != null && assetDtoQueue.hasDeposition(authorizationFEController.getAccountDto().getAccountId()))
                || (auctionRegister != null && auctionRegister.getStatusDeposit() == DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_REFUSE);
    }

    private boolean initAssetId() {
        String id = FacesUtil.getRequestParameter("id");
        if (assetId == null && (id == null || !StringUtil.isNumber(id))) {
            FacesUtil.redirect("/error/404.xhtml");
            return false;
        }
        if (id != null && StringUtil.isNumber(id)) {
            assetId = Long.valueOf(id);
        }
        return true;
    }

    @Override
    protected String getMenuId() {
        return Constant.ID_ASSET_FRONTEND;
    }
}
