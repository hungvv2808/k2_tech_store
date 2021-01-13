package vn.compedia.website.auction.controller.admin.payment;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.controller.admin.common.UploadSingleFileController;
import vn.compedia.website.auction.controller.frontend.common.FacesNoticeController;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.payment.PaymentDto;
import vn.compedia.website.auction.dto.payment.PaymentSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.model.payment.Payment;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.util.*;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = "session")
@Getter
@Setter
public class PaymentController extends BaseController {

    @Inject
    private FacesNoticeController facesNoticeController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private UploadSingleFileController uploadSingleFileController;
    @Inject
    private ActionSystemController actionSystemController;

    @Autowired
    protected PaymentRepository paymentRepository;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    protected AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    protected RegulationRepository regulationRepository;
    @Autowired
    protected BidRepository bidRepository;
    @Autowired
    protected AssetRepository assetRepository;
    @Autowired
    private AssetManagementRepository assetManagementRepository;
    @Autowired
    private RegulationFileRepository regulationFileRepository;

    private Payment payment;
    private LazyDataModel<PaymentDto> lazyDataModel;
    private PaymentSearchDto paymentSearchDto;
    private List<SelectItem> auctionFormList;
    private Payment objBackup;
    private boolean disable;
    private Account accountDto;
    private List<SelectItem> accountList;
    private Long accountId;
    private Long assetId;
    private List<AuctionRegister> auctionRegisters;
    private Asset asset;
    private List<SelectItem> assetList;
    private List<AssetDto> assetDtoList;
    private Date timeNow;
    private Payment ps;
    private List<Payment> paymentList;
    private Regulation regulation;
    private Payment paymentCopy;
    private String regulationFilePath;
    private SmtpAuthenticator smtpAuthenticator;
    private Date minDate;
    private Integer paymentStatus;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
            onSearch();
        }
    }

    public void resetAll() {
        regulationFilePath = null;
        ps = new Payment();
        paymentList = new ArrayList<>();
        timeNow = new Date();
        regulation = new Regulation();
        accountDto = new Account();
        accountList = new ArrayList<>();
        accountId = null;
        assetId = null;
        assetList = new ArrayList<>();
        assetDtoList = new ArrayList<>();
        assetDtoList = assetRepository.getAllName();
        objBackup = new Payment();
        for (AssetDto ac : assetDtoList) {
            if (ac.getRegulationCode() != null) {
                assetList.add(new SelectItem(ac.getAssetId(), ac.getRegulationCode() + " - " + getCommonController().abbreviate(ac.getName(), DbConstant.LIMITCHARACTER)));
            } else {
                assetList.add(new SelectItem(ac.getAssetId(), ac.getName()));
            }
        }
        auctionRegisters = new ArrayList<>();
        auctionFormList = new ArrayList<>();
        asset = new Asset();
        payment = new Payment();
        paymentSearchDto = new PaymentSearchDto();
        payment.setPaymentFormality(2);
    }

    public long getTotalMonneyOnline() {
        Long totalMoney = paymentRepository.getTotalMoney(DbConstant.PAYMENT_FORMALITY_ONLINE);
        if (totalMoney == null) {
            return 0L;
        }
        return totalMoney;
    }

    public long getTotalMonneyOffline() {
        Long totalMoney = paymentRepository.getTotalMoney(DbConstant.PAYMENT_FORMALITY_OFFLINE);
        if (totalMoney == null) {
            return 0L;
        }
        return totalMoney;
    }

    public void refresh() {
        paymentSearchDto = new PaymentSearchDto();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        onSearch();
    }

    public boolean validateData() {
        AuctionRegister ar = auctionRegisterRepository.findAuctionRegisterByAssetIdAndAccountId(assetId, accountId);
        Asset asset = assetRepository.findAssetByAssetId(assetId);

        if (accountId == null) {
            FacesUtil.addErrorMessage("Người giao dịch là trường bắt buộc");
            return false;
        }
        if (payment.getMoney() == null) {
            FacesUtil.addErrorMessage("Số tiền giao dịch là trường bắt buộc");
            return false;
        }
        if (payment.getMoney() <= 0) {
            FacesUtil.addErrorMessage("Số tiền giao dịch phải lớn hơn 0");
            return false;
        }
        if (payment.getPaymentFormality() == null) {
            FacesUtil.addErrorMessage("Hình thức thanh toán là trường bắt buộc");
            return false;
        }
        if (payment.getCreateDate() == null) {
            FacesUtil.addErrorMessage("Thời gian giao dịch là trường bắt buộc");
            return false;
        }
        if (assetId == null) {
            FacesUtil.addErrorMessage("Tài sản thanh toán là trường bắt buộc");
            return false;
        }
        if (payment.getStatus() == null) {
            FacesUtil.addErrorMessage("Trạng thái là trường bắt buộc");
            return false;
        }

        if (ar == null) {
            FacesUtil.addErrorMessage("Người này chưa đăng ký tham gia đấu giá tài sản đã chọn");
            FacesUtil.updateView("growl");
            return false;
        }

        List<Payment> pm = paymentRepository.findPaymentsByAuctionRegisterId(ar.getAuctionRegisterId());
        if (payment.getPaymentId() == null) {
            if (pm.size() != 0 && pm.get(0).getStatus().equals(payment.getStatus())) {
                if (payment.getStatus() == DbConstant.PAYMENT_STATUS_PAID) {
                    setErrorForm("Tài khoản đã nộp tiền đặt trước.\n" +
                            "Vui lòng chọn trạng thái giao dịch khác");
                }
                if (payment.getStatus() == DbConstant.PAYMENT_STATUS_REFUND) {
                    setErrorForm("Trung tâm đã trả lại tiền cọc của người dùng.\n" +
                            "Vui lòng chọn trạng thái giao dịch khác");
                }
                FacesUtil.updateView("growl");
                return false;
            }
        }
        if ((ar.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_ZERO || ar.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_FOUR) && payment.getStatus() == DbConstant.PAYMENT_STATUS_REFUND) {
            FacesUtil.addErrorMessage("Bạn vui lòng tạo giao dịch nhận tiền trước khi tạo giao dịch hoàn tiền");
            FacesUtil.updateView("growl");
            return false;
        }
        if (asset.getStatus() == DbConstant.ASSET_STATUS_WAITING && ar.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED && payment.getStatus() == DbConstant.PAYMENT_STATUS_REFUND) {
            FacesUtil.addErrorMessage("Phiên đấu giá của tài sản chưa bắt đầu và người giao dịch không từ chối hoặc bị từ chối, không thể hoàn tiền ");
            FacesUtil.updateView("growl");
            return false;
        }
        if (asset.getStatus() == DbConstant.ASSET_STATUS_ENDED && payment.getStatus() == DbConstant.PAYMENT_STATUS_PAID && ar.getStatusRefund() != DbConstant.AUCTION_REGISTER_STATUS_REFUND_FOUR) {
            FacesUtil.addErrorMessage("Phiên đấu giá của tài sản đã kết thúc, Bạn vui lòng chọn trạng thái giao dịch khác ");
            FacesUtil.updateView("growl");
            return false;
        }
        if (asset.getStatus() != DbConstant.ASSET_STATUS_CANCELED && ar.getStatusDeposit() == DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_REFUSE && payment.getStatus() != DbConstant.PAYMENT_STATUS_REFUND) {
            FacesUtil.addErrorMessage("Tài khoản này không thể thực hiện giao dịch trả tiền đặt trước do bị truất quyền");
            FacesUtil.updateView("growl");
            return false;
        }

        List<Bid> bids = bidRepository.findBidsByAssetIdAndAuctionRegisterIdOrderByBidIdAsc(asset.getAssetId(), ar.getAuctionRegisterId());
        for (Bid bid : bids) {
            if (asset.getStatus() != DbConstant.ASSET_STATUS_ENDED && payment.getStatus() != DbConstant.PAYMENT_STATUS_REFUND && !bid.isStatusRetract()) {
                FacesUtil.addErrorMessage("Tài khoản này không thể thực hiện giao dịch trả tiền đặt trước do đã rút lại giá đã trả");
                FacesUtil.updateView("growl");
                return false;
            }
        }
        AssetManagement assetManagement = assetManagementRepository.findAssetManagementByAuctionRegisterIdAndEnding(ar.getAuctionRegisterId(), DbConstant.ASSET_MANAGEMENT_ENDING_GOOD);
        if (assetManagement != null && payment.getStatus() == DbConstant.PAYMENT_STATUS_REFUND) {
            FacesUtil.addErrorMessage("Tài khoản này không thể thực hiện giao dịch trả tiền đặt trước do là tài khoản trúng đấu giá");
            FacesUtil.updateView("growl");
            return false;
        }

        if (ar.isStatusRefuseWin() && payment.getStatus() == DbConstant.PAYMENT_STATUS_REFUND) {
            FacesUtil.addErrorMessage("Tài khoản này không thể thực hiện giao dịch trả tiền đặt trước do đã từ chối trúng đấu giá");
            FacesUtil.updateView("growl");
            return false;
        }

        if (ar.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED_JOIN && payment.getStatus() == DbConstant.PAYMENT_STATUS_PAID && ar.getStatusRefund() != DbConstant.AUCTION_REGISTER_STATUS_REFUND_FOUR) {
            FacesUtil.addErrorMessage("Người giao dịch đã từ chối tham gia, vui lòng đổi trạng thái giao dịch khác");
            FacesUtil.updateView("growl");
            return false;
        }
        if (ar.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED && payment.getStatus() == DbConstant.PAYMENT_STATUS_PAID && ar.getStatusRefund() != DbConstant.AUCTION_REGISTER_STATUS_REFUND_FOUR) {
            FacesUtil.addErrorMessage("Người giao dịch đã bị từ chối , vui lòng đổi trạng thái giao dịch khác");
            FacesUtil.updateView("growl");
            return false;
        }
        return true;
    }

    public void onSaveData() {
        if (!validateData()) {
            return;
        }

        AuctionRegister au = auctionRegisterRepository.findAuctionRegisterByAssetIdAndAccountId(assetId, accountId);
        payment.setFilePath(uploadSingleFileController.getFilePath());
        payment.setFileName(uploadSingleFileController.getFileName());
        payment.setAuctionRegisterId(au.getAuctionRegisterId());
        payment.setSendBillStatus(false);
        payment.setUpdateDate(new Date());
        if (payment.getCreateBy() == null) {
            payment.setCreateBy(authorizationController.getAccountDto().getAccountId());
            payment.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        } else {
            payment.setCreateBy(objBackup.getCreateBy());
            payment.setCreateDate(objBackup.getCreateDate());
            payment.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        }

        AuctionRegister auctionRegister = auctionRegisterRepository.findAuctionRegisterByAuctionRegisterId(payment.getAuctionRegisterId());
        Asset ass = assetRepository.findAssetByAssetId(auctionRegister.getAssetId());
        if(au.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_FOUR && ass.getStatus() != DbConstant.ASSET_STATUS_WAITING) {
            auctionRegister.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE);
        } else {
            if (payment.getStatus() == DbConstant.PAYMENT_STATUS_PAID ) {
                auctionRegister.setStatus(DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED);
                auctionRegister.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE);
            } else if (payment.getStatus() == DbConstant.PAYMENT_STATUS_REFUND) {
                auctionRegister.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_TWO);
            }
        }
        auctionRegisterRepository.save(auctionRegister);
        Payment paymentCopy = paymentRepository.save(payment);
        String content = payment.getStatus() == DbConstant.PAYMENT_STATUS_PAID ? " Đã nhận được tiền" : " Đã trả lại tiền";
        if (payment.getCode() == null) {
            actionSystemController().onSave("Đã tạo thông tin giao dịch \"" + "GD" + String.format("%06d", paymentCopy.getPaymentId()) + "\"," + " loại giao dịch:" + content, authorizationController.getAccountDto().getAccountId());
        } else {
            actionSystemController().onSave("Đã cập nhật thông tin giao dịch \"" + payment.getCode() + "\"," + " loại giao dịch:" + content, authorizationController.getAccountDto().getAccountId());
        }

        payment.setCode("GD" + String.format("%06d", paymentCopy.getPaymentId()));
        paymentRepository.save(payment);
        String assetName = assetRepository.getNameByAssetId(auctionRegister.getAssetId());
        String regulationCode = regulationRepository.getCodeByRegulationId(auctionRegister.getRegulationId());
        Account account = accountRepository.findAccountByAccountId(auctionRegister.getAccountId());
        String name = account.isOrg() ? account.getOrgName() : account.getFullName();
        EmailUtil.getInstance().sendNotificationAccepted(account.getEmail(),name, assetName, regulationCode, auctionRegister.getCode());
        FacesUtil.addSuccessMessage("Lưu thành công");
        FacesUtil.closeDialog("dialogInsertUpdate");
        resetAll();
        FacesUtil.updateView("growl");
        FacesUtil.updateView("searchForm");
        onSearch();
    }

    public void resetDialog() {
        resetAll();
        uploadSingleFileController.resetAll(null, null);
        disable = false;
    }

    public void onDateSelect() {
        if (null != this.paymentSearchDto.getFromDate() && null != this.paymentSearchDto.getToDate()
                && this.paymentSearchDto.getFromDate().compareTo(this.paymentSearchDto.getToDate()) > 0) {
            this.getPaymentSearchDto().setToDate(null);
            setErrorForm("Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
        }
    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<PaymentDto>() {

            @Override
            public List<PaymentDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                paymentSearchDto.setPageIndex(first);
                paymentSearchDto.setPageSize(pageSize);
                paymentSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                paymentSearchDto.setSortOrder(sort);
                return paymentRepository.search(paymentSearchDto);
            }
        };
        int count = paymentRepository.countSearch(paymentSearchDto).intValue();
        lazyDataModel.setRowCount(count);
    }

    public void onChangeAsset(Long assetId) {
        accountList = new ArrayList<>();
        if (assetId != null) {
            AssetDto asset = assetRepository.findAssetByAssetIdWithParam(assetId);
            payment.setMoney(asset.getDeposit());

            if (disable) {
                payment.setStatus(paymentStatus);
            }

            auctionRegisters = auctionRegisterRepository.getAuctionRegistersByAssetId(assetId);
            for (AuctionRegister au : auctionRegisters) {
                accountDto = accountRepository.findAccountByAccountId(au.getAccountId());
                List<Payment> pm = paymentRepository.findPaymentsByAuctionRegisterId(au.getAuctionRegisterId());
                if (pm.size() < 2) {
                    if (accountDto.isOrg()) {
                        accountList.add(new SelectItem(accountDto.getAccountId(), accountDto.getOrgName()));
                    } else {
                        accountList.add(new SelectItem(accountDto.getAccountId(), accountDto.getFullName()));
                    }
                }
            }
            minDate = asset.getStartRegistrationDate();
        } else {
            accountList = new ArrayList<>();
            payment = new Payment();
        }
    }

    public void onChangeAssetToSearch(Long assetId) {
        accountList = new ArrayList<>();
        auctionRegisters = auctionRegisterRepository.findAuctionRegistersByAssetId(assetId);
        for (AuctionRegister au : auctionRegisters) {
            accountDto = accountRepository.findAccountByAccountId(au.getAccountId());
            if (accountDto.isOrg()) {
                accountList.add(new SelectItem(accountDto.getAccountId(), accountDto.getOrgName()));
            } else {
                accountList.add(new SelectItem(accountDto.getAccountId(), accountDto.getFullName()));
            }
        }
    }

    public void showUpdatePopup(PaymentDto obj) {
        disable = true;
        payment = new Payment();
        AuctionRegister au = auctionRegisterRepository.findAuctionRegisterByAuctionRegisterId(obj.getAuctionRegisterId());
        assetId = au.getAssetId();
        onChangeAsset(assetId);
        accountId = au.getAccountId();
        uploadSingleFileController.resetAll(obj.getFilePath(), obj.getFileName());
        BeanUtils.copyProperties(obj, payment);
        BeanUtils.copyProperties(obj, objBackup);
    }

    // Get file path regulation
    public void getFilePathRegulation(PaymentDto dto) {
        regulationFilePath = regulationFileRepository.findRegulationFileByRegulationIdAndType(dto.getRegulationId(), DbConstant.REGULATION_FILE_TYPE_QUY_CHE).getFilePath();
    }

    public Date getTimeNow() {
        return new Date();
    }

    public Date getDateNow() {
        return new Date();
    }

    @Override
    protected EScope getMenuId() {
        return EScope.PAYMENT;
    }
}
