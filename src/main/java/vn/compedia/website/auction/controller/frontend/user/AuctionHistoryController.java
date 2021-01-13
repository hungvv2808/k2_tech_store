package vn.compedia.website.auction.controller.frontend.user;

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
import vn.compedia.website.auction.controller.frontend.auction.AuctionController;
import vn.compedia.website.auction.controller.frontend.common.FacesNoticeController;
import vn.compedia.website.auction.controller.frontend.common.PaginationAjaxController;
import vn.compedia.website.auction.controller.frontend.common.UploadSingleFilePaymentFEController;
import vn.compedia.website.auction.controller.frontend.payment.momo.PaymentWalletReturnUrlController;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.auction.AssetSearchDto;
import vn.compedia.website.auction.dto.auction.AuctionRegisterSearchDto;
import vn.compedia.website.auction.dto.common.CityDistrictDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.entity.frontend.MyAsset;
import vn.compedia.website.auction.entity.frontend.MyAssetQueue;
import vn.compedia.website.auction.entity.frontend.MyAuction;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.Asset;
import vn.compedia.website.auction.model.AuctionRegister;
import vn.compedia.website.auction.model.payment.NotifyPayment;
import vn.compedia.website.auction.model.payment.Payment;
import vn.compedia.website.auction.model.payment.other.CaptureWalletRequest;
import vn.compedia.website.auction.model.payment.other.PaymentReturnUrlRequest;
import vn.compedia.website.auction.model.payment.other.PaymentReturnUrlResponse;
import vn.compedia.website.auction.model.payment.other.PaymentReturnUrlResponseRef;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.service.AuctionHistoryService;
import vn.compedia.website.auction.system.task.AuctionTask;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.system.task.dto.Bargain;
import vn.compedia.website.auction.util.*;
import vn.compedia.website.auction.util.payment.PaymentVariableUtil;
import vn.compedia.website.auction.util.payment.other.WalletPaymentUtil;
import vn.compedia.website.auction.util.payment.other.constants.PaymentConstant;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Named
@Scope(value = "session")
@Log4j2
public class AuctionHistoryController extends BaseFEController {
    @Inject
    private AuctionTask auctionTask;
    @Inject
    private AuthorizationFEController authorizationFEController;
    @Inject
    private AuctionController auctionController;
    @Inject
    private PaymentWalletReturnUrlController paymentWalletReturnUrlController;
    @Inject
    private FacesNoticeController facesNoticeController;
    @Inject
    private UploadSingleFilePaymentFEController uploadSingleFilePaymentFEController;
    @Inject
    private CommonFEController commonFEController;
    @Autowired
    private AuctionHistoryRepository auctionHistoryRepository;
    @Autowired
    private AuctionHistoryService auctionHistoryService;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentReturnUrlRequestRepository paymentReturnUrlRequestRepository;
    @Autowired
    private PaymentReturnUrlResponseRepository paymentReturnUrlResponseRepository;
    @Autowired
    private PaymentReturnUrlResponseRefRepository paymentReturnUrlResponseRefRepository;
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private NotifyPaymentRepository notifyPaymentRepository;

    private PaginationAjaxController<String> pagination;
    private AuctionRegisterSearchDto auctionRegisterSearchDto;
    private AuctionRegister auctionRegister;
    private List<RegulationDto> regulationDtoList;
    private List<MyAuction> myAuctionList;
    // onBargain
    private Long assetId;
    private Long money;
    private Long priceRoundId;
    //filter
    private Integer filterType;
    // payment
    private Long paymentAuctionRegisterId;
    // cancel btn
    private Long cancelAuctionRegisterId;
    // watch reason deposit
    private Long depositReasonShowId;
    private NotifyPayment notifyPayment;
    private Long assetPayment;
    private String amountPayment;
    private long countdown = -1;

    // final status asset
    public final int STATUS_WAITING = 1;
    public final int STATUS_PLAYING = 2;
    public final int STATUS_SUCCESS = 3;
    public final int STATUS_NOT_SUCCESS = 4;
    public final int STATUS_BAD_ENDING = 0;
    public final int STATUS_CANCEL = 5;

    // final status register
    public final int STATUS_PENDING = 0;
    public final int STATUS_APPROVED = 1;
    public final int STATUS_REFUSE = 3;
    public final int STATUS_WAIT_FOR_CONTROL = 4;

    public AuctionHistoryController() {
        pagination = new PaginationAjaxController<>();
        pagination.resetAll();
    }

    public void initData(){
        if(!FacesContext.getCurrentInstance().isPostback()){
            init();
            resetAll();
        }
    }

    public void resetAll() {
        auctionRegisterSearchDto = new AuctionRegisterSearchDto();
        auctionRegisterSearchDto.setAccountId(authorizationFEController.getAccountDto().getAccountId());
        onSearch();
    }

    public void onSearch() {
        pagination.setLazyDataModel(new LazyDataModel<String>() {
            @Override
            public List<String> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
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
                temp.add(buildDataString(auctionHistoryService.search(auctionRegisterSearchDto)));
                return temp;
            }

            @Override
            public int getRowCount() {
                return auctionHistoryService.countSearch(auctionRegisterSearchDto).intValue();
            }
        });
        pagination.loadData();
    }

    public boolean checkTime(Date paymentStartTime, Date paymentEndTime) {
        return paymentStartTime != null
                && DateUtil.formatDate(paymentStartTime, DateUtil.DATE_FORMAT_MINUTE).compareTo(new Date()) <= 0
                && DateUtil.formatDate(paymentEndTime, DateUtil.DATE_FORMAT_SECOND_END).compareTo(new Date()) >= 0;
    }

    private String buildDataString(List<RegulationDto> regulationDtoList) {
        myAuctionList = new ArrayList<>();
        int pageNumber = (pagination.getPageIndex() - 1) * pagination.getPageSize();
        int i = 1;
        countdown = -1;
        for (RegulationDto regulationDto : regulationDtoList) {
            List<MyAsset> myAssetList = new ArrayList<>();
            long temp = -1;
            for (AssetDto assetDto : regulationDto.getAssetDtoList()) {
                boolean checkTime = checkTime(assetDto.getPaymentStartTime(), assetDto.getPaymentEndTime());
                myAssetList.add(new MyAsset(
                        assetDto.getAssetId(),
                        assetDto.getName(),
                        assetDto.getAuctionFormalityId(),
                        assetDto.getAuctionMethodId(),
                        assetDto.getPriceRoundId(),
                        assetDto.getCurrentRound(),
                        assetDto.getStartingPrice(),
                        assetDto.getPriceStep(),
                        assetDto.getHighestPrice() == null ? 0 : assetDto.getHighestPrice(),
                        assetDto.getStatus(),
                        assetDto.getAuctionRegisterId(),
                        assetDto.getAuctionRegisterStatus(),
                        assetDto.isAssetManagementEnding(),
                        assetDto.isAssetManagementEndingFinal(),
                        assetDto.getHighestPrice(),
                        assetDto.isRetractPrice(),
                        buildAssetPlaying(assetDto.getAssetId()),
                        checkTime,
                        assetDto.getStatusRefund()
                ));
                // auto render payment
                if (assetDto.getPaymentStartTime() != null && assetDto.getPaymentStartTime() != null && assetDto.getAuctionRegisterId() != null) {
                    Date paymentStartTime = DateUtil.formatDate(assetDto.getPaymentStartTime(), DateUtil.DATE_FORMAT_MINUTE);
                    Date paymentEndTime = DateUtil.formatDate(assetDto.getPaymentEndTime(), DateUtil.DATE_FORMAT_SECOND_END);
                    if (new Date().compareTo(paymentStartTime) <= 0) {
                        temp = countMax(paymentStartTime.getTime() - new Date().getTime());
                    } else if (new Date().compareTo(paymentEndTime) <= 0) {
                        temp = countMax(paymentEndTime.getTime() - new Date().getTime());
                    }
                }
                if (countdown <= -1 || (temp > -1 && temp < countdown)) {
                    countdown = temp;
                }
            }
            myAuctionList.add(new MyAuction(
                    pageNumber + i++,
                    regulationDto.getRegulationId(),
                    regulationDto.getCode(),
                    regulationDto.getNumberOfRounds(),
                    regulationDto.getAuctionFormalityId(),
                    regulationDto.getAuctionFormalityName(),
                    regulationDto.getAuctionMethodId(),
                    regulationDto.getAuctionMethodName(),
                    DateUtil.formatToPattern(regulationDto.getStartTime(), DateUtil.HOUR_DATE_FORMAT),
                    regulationDto.getEndTime() == null ? "" : DateUtil.formatToPattern(regulationDto.getEndTime(), DateUtil.HOUR_DATE_FORMAT),
                    Math.max((regulationDto.getStartTime().getTime() - new Date().getTime()) / 1000, 0),
                    parseCountTimeString(regulationDto.getStartTime()),
                    myAssetList
            ));
        }
        return StringUtil.toJson(myAuctionList);
    }

    public void onBargain() {
        // onBargain
        auctionController.setAssetId(assetId);
        auctionController.loadAssetDto();
        auctionController.loadAuctionRegister();
        auctionController.setMoney(money);
        auctionController.setPriceRoundId(priceRoundId);
        auctionController.onBargain();
    }

    public void onPriceAccept() {
        auctionController.setAssetId(assetId);
        auctionController.loadAssetDto();
        auctionController.loadAuctionRegister();
        auctionController.setPriceRoundId(priceRoundId);
        auctionController.onAcceptPrice();
    }

    public void onRetractPrice() {
        auctionController.setAssetId(assetId);
        auctionController.onRetractPrice();
    }

    public void onRefuseWinner() {
        auctionController.setAssetId(assetId);
        auctionController.onRefuseWinner();
    }

    private String parseCountTimeString(Date date) {
        long[] dhms = DateUtil.countdown(new Date(), date);
        if (dhms[0] <= 0) {
            return String.format("%02d:%02d:%02d", dhms[1], dhms[2], dhms[3]);
        }
        return String.format("%02d ngày, %02d:%02d:%02d", dhms[0], dhms[1], dhms[2], dhms[3]);
    }

    public void redirectPaymentWallet() {
        AuctionRegister auctionRegister = auctionRegisterRepository.findAllByAuctionRegisterId(paymentAuctionRegisterId);
        AssetSearchDto assetSearchDto = new AssetSearchDto();
        assetSearchDto.setAssetId(auctionRegister.getAssetId());
        assetSearchDto.setAccountId(auctionRegister.getAccountId());
        AssetDto assetDto = assetRepository.getAssetDtoByAssetId(assetSearchDto);
        if (!commonFEController.renderButtonPayment(assetDto)) {
            setErrorForm("Thời gian thanh toán chưa đến hoặc đã hết hạn");
            return;
        }
        Asset asset = assetRepository.findAssetByAssetId(auctionRegister.getAssetId());
        Account account = auctionRegisterRepository.findAccountByAuctioneerId(paymentAuctionRegisterId);
        Integer statusResponse = paymentReturnUrlResponseRepository.getStatusResponse(paymentAuctionRegisterId);
        try {
            // when status = 0, we create order request to api
            if (statusResponse == null || statusResponse == PaymentConstant.API_WALLET_STATUS_NONE_REGISTER) {
                createAndSaveDataApi(asset, account);
                return;
            }

            // when status = 1, get payurl check time remaining if < 7 day access to checkout url else recreate request
            if (PaymentConstant.API_WALLET_STATUS_NEWLY_CREATED == statusResponse) {
                PaymentReturnUrlResponseRef paymentReturnUrlResponseRef = paymentReturnUrlResponseRefRepository.getAllByAuctionRegisterId(paymentAuctionRegisterId);
                Date date = new Date();
                if (DateUtil.plusMinute(paymentReturnUrlResponseRef.getCreateDate(), 7).compareTo(date) > 0) {
                    createAndSaveDataApi(asset, account);
                } else {
                    paymentWalletReturnUrlController.setTokenCode(paymentReturnUrlResponseRef.getToken_code());
                    paymentWalletReturnUrlController.setAuctionRegisterId(paymentAuctionRegisterId);
                    FacesUtil.externalRedirect(paymentReturnUrlResponseRef.getCheckout_url());
                }
            }
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    public CaptureWalletRequest createOrderRequest(Asset asset, Account account) {
        CaptureWalletRequest captureWalletRequest = new CaptureWalletRequest();

        captureWalletRequest.setFunction(PaymentVariableUtil.getApiWalletFunction());
        captureWalletRequest.setMerchant_site_code(PaymentVariableUtil.getApiWalletMerchantSiteCode());
        captureWalletRequest.setOrder_code(String.format("OSP%06d", paymentAuctionRegisterId));
        captureWalletRequest.setOrder_description("Thanh toán tiền cọc");
        // min amount is 20000
        captureWalletRequest.setAmount(asset.getDeposit() < 20000 ? 20000 : asset.getDeposit());
        captureWalletRequest.setCurrency(PaymentVariableUtil.getApiWalletCurrency());
        captureWalletRequest.setBuyer_fullname(account.getFullName());
        captureWalletRequest.setBuyer_email(account.getEmail());
        captureWalletRequest.setBuyer_mobile(account.getPhone());
        CityDistrictDto cityDistrictDto = provinceRepository.getNameCityDistrict(account.getProvinceId(), account.getDistrictId(), account.getCommuneId());
        captureWalletRequest.setBuyer_address(cityDistrictDto == null ? "Việt Nam" : cityDistrictDto.toString());
        captureWalletRequest.setReturn_url(PaymentVariableUtil.getApiWalletReturnUrl());
        captureWalletRequest.setCancel_url(PaymentVariableUtil.getApiWalletCancelUrl());
        captureWalletRequest.setNotify_url(PaymentVariableUtil.getApiWalletNotifyUrl());
        captureWalletRequest.setLanguage(PaymentVariableUtil.getApiWalletLanguage());
        captureWalletRequest.setApplication_id(Integer.parseInt(PaymentVariableUtil.getApiWalletApplicationId()));

        return captureWalletRequest;
    }

    public void createAndSaveDataApi(Asset asset, Account account) throws IOException {
        CaptureWalletRequest captureWalletRequest = createOrderRequest(asset, account);
        String payUrl = WalletPaymentUtil.getPayUrl(captureWalletRequest, authorizationFEController.getAccountDto().getAccountId());

        // save data on table payment_return_url_request
        PaymentReturnUrlRequest paymentReturnUrlRequest = WalletPaymentUtil.paymentReturnUrlRequest;
        if (paymentReturnUrlRequestRepository.countRequest(captureWalletRequest.getOrder_code()) == 0) {
            paymentReturnUrlRequestRepository.save(paymentReturnUrlRequest);
        }

        // save data on table payment_return_url_response
        PaymentReturnUrlResponse paymentReturnUrlResponse = WalletPaymentUtil.paymentReturnUrlResponse;
        paymentReturnUrlResponse.setPaymentReturnUrlRequestId(paymentReturnUrlRequest.getPayment_return_url_request_id());
        paymentReturnUrlResponse.setAuctionRegisterId(paymentAuctionRegisterId);
        paymentReturnUrlResponse.setStatus(PaymentConstant.API_WALLET_STATUS_NONE_REGISTER);
        if (paymentReturnUrlResponseRepository.countAuctionRegisterResponse(paymentReturnUrlRequest.getPayment_return_url_request_id()) == 0) {
            paymentReturnUrlResponseRepository.save(paymentReturnUrlResponse);
        }

        // save data on table payment_return_url_response_ref
        PaymentReturnUrlResponseRef paymentReturnUrlResponseRef = WalletPaymentUtil.paymentReturnUrlResponseRef;
        paymentReturnUrlResponseRef.setPayment_return_url_response_id(paymentReturnUrlResponse.getPayment_return_url_response_id());
        paymentReturnUrlResponseRefRepository.save(paymentReturnUrlResponseRef);

        // set value token code for return page to send check order request
        paymentWalletReturnUrlController.setTokenCode(paymentReturnUrlResponseRef.getToken_code());
        // set value asset id for return page
        paymentWalletReturnUrlController.setAuctionRegisterId(paymentAuctionRegisterId);
        // set asset deposit for return page
        paymentWalletReturnUrlController.setAssetDeposit(asset.getDeposit());
        // set asset name for return page
        paymentWalletReturnUrlController.setAssetName(asset.getName());
        // set payment return url request id
        paymentWalletReturnUrlController.setPaymentReturnUrlRequestId(paymentReturnUrlRequest.getPayment_return_url_request_id());

        // redirect to payment page
        FacesUtil.externalRedirect(payUrl);
    }

    public void cancelAuctionRegisterAsset() {
        AuctionRegister auctionRegister = auctionRegisterRepository.findAllByAuctionRegisterId(cancelAuctionRegisterId);
        Payment payment = paymentRepository.findByAuctionRegisterId(cancelAuctionRegisterId);
        Asset asset = assetRepository.findById(auctionRegister.getAssetId()).orElse(null);
        if (asset == null) {
           setErrorForm("Tài sản không tồn tại");
           return;
        }
        if (asset.getStatus().equals(DbConstant.ASSET_STATUS_PLAYING)) {
            setErrorForm("Tài sản đang diễn ra đấu giá, không thể hủy tham gia.");
            return;
        }
        if (!asset.getStatus().equals(DbConstant.ASSET_STATUS_WAITING)) {
            setErrorForm("Chỉ có thể hủy tham gia khi tài sản chưa diễn ra đấu giá");
            return;
        }
//        if (payment != null && payment.getStatus() == DbConstant.PAYMENT_STATUS_PAID){
//            auctionRegister.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_ONE);
//        }

        // save
        auctionRegister.setStatus(DbConstant.AUCTION_REGISTER_STATUS_REJECTED_JOIN);
        auctionRegisterRepository.save(auctionRegister);

        // remove if in queue
        //auctionTask.onRemoveAuctionRegister(auctionRegister);
        // remove in ram
        //authorizationFEController.getAccountDto().getSessionAsset().remove(auctionRegister.getAssetId());

        // notify
        setSuccessForm("Từ chối tham gia đấu giá thành công!");
        // reload data
        onSearch();
    }

    public void getReasonDepositByRegulationId() {
        auctionRegister = auctionRegisterRepository.findAuctionRegisterByAuctionRegisterId(depositReasonShowId);
        auctionController.setAuctionRegister(auctionRegister);
        facesNotice().openModal("rejectPopup");
    }

    private MyAssetQueue buildAssetPlaying(Long assetId) {
        MyAssetQueue myAssetQueue = new MyAssetQueue();
        AssetDtoQueue assetDtoQueue = auctionTask.getAssetDtoList().get(assetId);
        Long accountId = authorizationFEController.getAccountDto().getAccountId();
        if (assetDtoQueue != null && assetDtoQueue.isInProcessing()) {
            myAssetQueue.setStart(assetDtoQueue.getBargainList().isEmpty());
            Long priceBargain = assetDtoQueue.getPriceBargained().get(accountId);

            if (DbConstant.AUCTION_FORMALITY_ID_POLL != assetDtoQueue.getAuctionFormalityId()) {
                myAssetQueue.setCurrentPrice(assetDtoQueue.getCurrentPrice());
            } else {
                Bargain bargain = assetDtoQueue.getWinner();
                if (assetDtoQueue.isEnded() && bargain != null) {
                    myAssetQueue.setCurrentPrice(bargain.getMoney());
                }
                if (assetDtoQueue.isEnded() && bargain == null) {
                    myAssetQueue.setCurrentPrice(assetDtoQueue.getStartingPrice());
                }
                if (!assetDtoQueue.isEnded() && assetDtoQueue.getPriceRoundCurrent() != null) {
                    myAssetQueue.setCurrentPrice(assetDtoQueue.getPriceRoundCurrent().getHighestPrice());
                }
            }

            myAssetQueue.setPriceBargained(priceBargain);
            if (assetDtoQueue.getEndTimeRound() != null) {
                if (assetDtoQueue.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_DOWN && assetDtoQueue.isInTimeConfirm()) {
                    myAssetQueue.setEndTimeRound(Math.max((assetDtoQueue.endTimeAcceptPrice().getTime() - new Date().getTime()) / 1000, 0));
                    myAssetQueue.setEndTimeRoundString(parseCountTimeString(assetDtoQueue.endTimeAcceptPrice()));
                } else {
                    myAssetQueue.setEndTimeRound(Math.max((assetDtoQueue.getEndTimeRound().getTime() - new Date().getTime()) / 1000, 0));
                    myAssetQueue.setEndTimeRoundString(parseCountTimeString(assetDtoQueue.getEndTimeRound()));
                }
            }

            if (assetDtoQueue.getRoundConfirm() != null) {
                Bargain bargain = assetDtoQueue.getWinner();
                if (bargain != null) {
                    myAssetQueue.setWinner(bargain.getAccountId().equals(accountId));
                    myAssetQueue.setPriceWinner(bargain.getMoney());
                }
            }

            myAssetQueue.setRoundConfirm(assetDtoQueue.getRoundConfirm());
            myAssetQueue.setInTimeConfirm(assetDtoQueue.isInTimeConfirm());
            myAssetQueue.setDeposit(assetDtoQueue.hasDeposition(accountId));
            myAssetQueue.setShowBargain(!assetDtoQueue.hasBargained(accountId) || (assetDtoQueue.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_DIRECT && assetDtoQueue.getBargain(1) != null && !assetDtoQueue.getBargain(1).getAccountId().equals(accountId)));
            myAssetQueue.setEnded(assetDtoQueue.isEnded());
            return myAssetQueue;
        }
        return null;
    }

    public void takePaymentOption() {
        auctionRegister = auctionRegisterRepository.findById(paymentAuctionRegisterId).orElse(null);
        Integer countAuctionRegisterId = paymentRepository.countAuctionRegisterIdSuccess(paymentAuctionRegisterId);
        if (countAuctionRegisterId != 0) {
            facesNotice().addErrorMessage("Bạn đã thanh toán tiền đặt trước cho tài sản này");
            return;
        }
        Integer countNotifyPaymentId = notifyPaymentRepository.countNotifyPaymentId(auctionRegister.getAssetId(), auctionRegister.getRegulationId(), authorizationFEController.getAccountDto().getAccountId());
        if (countNotifyPaymentId != 0) {
            facesNotice().addErrorMessage("Bạn đã gửi biên lai thanh toán cho quản trị viên, vui lòng chờ phê duyệt hoá đơn thanh toán của bạn");
            return;
        }
        notifyPayment = new NotifyPayment();
        amountPayment = assetRepository.getDepositByAssetId(auctionRegister.getAssetId()).toString();
        uploadSingleFilePaymentFEController.resetAll(null);
        facesNotice().openModal("paymentOptionPopup");
    }

    public void createNotifyPayment() {
        notifyPayment.setAuctionRegisterId(paymentAuctionRegisterId);
        notifyPayment.setAccountId(authorizationFEController.getAccountDto().getAccountId());
        notifyPayment.setAssetId(auctionRegister.getAssetId());
        notifyPayment.setRegulationId(auctionRegister.getRegulationId());
        notifyPayment.setFilePath(uploadSingleFilePaymentFEController.getUploadSingleFileFEDto().getFilePath());
        notifyPayment.setFileName(uploadSingleFilePaymentFEController.getUploadSingleFileFEDto().getFileName());
        notifyPayment.setCreateBy(authorizationFEController.getAccountDto().getAccountId());
        notifyPayment.setStatus(DbConstant.NOTIFY_PAYMENT_STATUS_NON_CREATE);
        if (amountPayment == null) {
            setErrorForm("Số tiền thanh toán là trường bắt buộc");
            return;
        }
        try {
            notifyPayment.setAmount(Long.parseLong(amountPayment.replaceAll("\\.", "")));
        } catch (Exception e) {
            setErrorForm("Số tiền thanh toán không đúng định dạng");
            return;
        }
        if (notifyPayment.getFilePath() == null) {
            setErrorForm("File biên lai là trường bắt buộc");
            return;
        }
        notifyPaymentRepository.save(notifyPayment);
        AuctionRegister auctionRegister = auctionRegisterRepository.findById(notifyPayment.getAuctionRegisterId()).orElse(null);
        if (auctionRegister == null) {
            setErrorForm("Đăng ký tham gia đấu giá thất bại");
            return;
        }
        auctionRegister.setStatus(DbConstant.AUCTION_REGISTER_STATUS_WAITING);
        auctionRegister.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_FOUR);
        auctionRegisterRepository.save(auctionRegister);
        setSuccessForm("Gửi biên lai thanh toán thành công");
        facesNotice().activeFunction("cmdRefreshData");
        facesNotice().closeModal("paymentOptionPopup");
    }

    private long countMax(long number) {
        long maxCount = 24 * 60 * 60 * 1000;
        if (number <= maxCount) {
            return number;
        }
        return -1;
    }

    @Override
    protected String getMenuId() {
        return Constant.ID_MY_AUCTION;
    }
}
