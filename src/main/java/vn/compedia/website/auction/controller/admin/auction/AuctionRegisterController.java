package vn.compedia.website.auction.controller.admin.auction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.controller.admin.common.UploadSingleFileController;
import vn.compedia.website.auction.controller.admin.common.UploadSingleImageController;
import vn.compedia.website.auction.controller.admin.payment.PaymentController;
import vn.compedia.website.auction.dto.auction.AuctionRegisterDto;
import vn.compedia.website.auction.dto.auction.AuctionRegisterSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.AuctionRegister;
import vn.compedia.website.auction.model.payment.NotifyPayment;
import vn.compedia.website.auction.model.payment.Payment;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.util.*;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.InputStream;
import java.util.*;

@Named
@Scope(value = "session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionRegisterController extends BaseController {

    @Inject
    protected UploadSingleImageController uploadSingleImageController;
    @Inject
    protected UploadSingleFileController uploadSingleFileController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private PaymentController paymentController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private NotifyPaymentRepository notifyPaymentRepository;

    @Autowired
    protected AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    protected AssetRepository assetRepository;
    @Autowired
    protected RegulationRepository regulationRepository;
    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected PaymentRepository paymentRepository;
    private Payment payment;
    private AuctionRegister auctionRegister;
    private AuctionRegisterDto auctionRegisterDto;
    private AuctionRegisterSearchDto auctionRegisterSearchDto;
    private LazyDataModel<AuctionRegisterDto> lazyDataModel;
    private String status;
    private String statusRefund;
    private String assetStatus;
    private String assetStatusCancel;
    private String regulationFilePath;
    private String linkImage;
    private boolean refundPrice;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            status = FacesUtil.getRequestParameter("status");
            statusRefund = FacesUtil.getRequestParameter("statusrefund");
            assetStatus = FacesUtil.getRequestParameter("assetstatus");
            assetStatusCancel = FacesUtil.getRequestParameter("statuscancel");
            refundPrice = ValueUtil.getBooleanByObject(FacesUtil.getRequestParameter("refundprice"));
            resetAll();
        }
    }

    public void resetAll() {
        linkImage = null;
        payment = new Payment();
        auctionRegister = new AuctionRegister();
        auctionRegisterDto = new AuctionRegisterDto();
        auctionRegisterSearchDto = new AuctionRegisterSearchDto();
        auctionRegisterSearchDto.setRegulationAuctionStatusList(Arrays.asList(
                DbConstant.REGULATION_AUCTION_STATUS_WAITING,
                DbConstant.REGULATION_AUCTION_STATUS_PLAYING,
                DbConstant.REGULATION_AUCTION_STATUS_ENDED,
                DbConstant.REGULATION_AUCTION_STATUS_CANCELLED
        ));
        uploadSingleImageController.resetAll(null);
        actionSystemController.resetAll();
        onSearch();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void onSend() {
        if (auctionRegisterDto.getStatus() != DbConstant.AUCTION_REGISTER_STATUS_REJECTED_JOIN) {
            if (StringUtils.isBlank(auctionRegisterDto.getReasonRefuse().trim())) {
                setErrorForm("Bạn vui lòng nhập lý do hủy bỏ đăng ký tham gia đấu giá");
                return;
            }
            //AuctionRegister auctionRegister = auctionRegisterRepository.findById(auctionRegisterDto.getAuctionRegisterId()).orElse(null);
            BeanUtils.copyProperties(auctionRegisterDto, auctionRegister);
            auctionRegister.setStatus(DbConstant.AUCTION_REGISTER_STATUS_REJECTED);
//            if (auctionRegister.getStatusRefund().equals(DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE)) {
//                auctionRegister.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_ONE);
//            }
            auctionRegister.setUpdateBy(authorizationController.getAccountDto().getAccountId());
            auctionRegister.setUpdateDate(new Date());
            String assetName = assetRepository.getNameByAssetId(auctionRegister.getAssetId());
            String regulationCode = "-" + regulationRepository.getCodeByRegulationId(auctionRegister.getRegulationId());
            Account account = accountRepository.findAccountByAccountId(auctionRegister.getAccountId());
            if (account.isOrg()) {
                EmailUtil.getInstance().sendRefuseRegister(account.getEmail(), assetName, regulationCode, auctionRegisterDto.getReasonRefuse().trim(), account.getOrgName());
            } else {
                EmailUtil.getInstance().sendRefuseRegister(account.getEmail(), assetName, regulationCode, auctionRegisterDto.getReasonRefuse().trim(), account.getFullName());
            }

            auctionRegisterRepository.save(auctionRegister);
            actionSystemController.onSave("Hủy đăng ký tham gia đấu giá của " + auctionRegisterDto.getFullNameNguoiDangKy(), authorizationController.getAccountDto().getAccountId());
            setSuccessForm("Hủy bỏ thông tin đăng ký tham gia đấu giá thành công");
            FacesUtil.closeDialog("dialoglido");
            resetAll();
            FacesUtil.updateView("searchForm");
            onSearch();
        }
    }

    public void onDateSelect() {
        if (null != this.auctionRegisterSearchDto.getStartDateSearch() && null != this.auctionRegisterSearchDto.getEndDateSearch()
                && this.auctionRegisterSearchDto.getStartDateSearch().compareTo(this.auctionRegisterSearchDto.getEndDateSearch()) > 0) {
            this.getAuctionRegisterSearchDto().setEndDateSearch(null);
            setErrorForm("Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
        }
    }

    public void approved(AuctionRegisterDto obj) {
        if (obj.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED) {
            obj.setStatus(DbConstant.AUCTION_REGISTER_STATUS_WAITING);
        } else {
            obj.setStatus(DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED);
        }
        if (obj.getStatusRefund() != null) {
            obj.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE);
            obj.setReasonRefuse(null);
        } else {
            obj.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_ONE);
        }
        String assetName = assetRepository.getNameByAssetId(obj.getAssetId());
        String regulationCode = regulationRepository.getCodeByRegulationId(obj.getRegulationId());
        Account account = accountRepository.findAccountByAccountId(obj.getAccountId());
        String name = account.isOrg() ? account.getOrgName() : account.getFullName();
        EmailUtil.getInstance().sendNotificationAccepted(account.getEmail(),name, assetName, regulationCode, obj.getCode());
        actionSystemController.onSave("Phê duyệt đơn yêu cầu đấu giá của " + obj.getFullNameNguoiDangKy(), authorizationController.getAccountDto().getAccountId());
        BeanUtils.copyProperties(obj, auctionRegister);
        auctionRegister.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        auctionRegister.setUpdateDate(new Date());
        auctionRegisterRepository.save(auctionRegister);

        onSearch();
        setSuccessForm("Phê duyệt thành công");
    }

    public void changeStatus(AuctionRegisterDto obj) {
        obj.setStatus(DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED);
        BeanUtils.copyProperties(obj, auctionRegister);
        auctionRegister.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        auctionRegister.setUpdateDate(new Date());
        auctionRegisterRepository.save(auctionRegister);
        onSearch();
        setSuccessForm("Xử lý lại yêu cầu tham gia đấu giá thành công");
    }

    public void refund(AuctionRegisterDto obj) {
        obj.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_TWO);
        BeanUtils.copyProperties(obj, auctionRegister);
        auctionRegister.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        auctionRegister.setUpdateDate(new Date());
        auctionRegisterRepository.save(auctionRegister);
        onSearch();
        actionSystemController.onSave("Thay đổi trạng thái tiền đặt trước của " + obj.getFullNameNguoiDangKy(), authorizationController.getAccountDto().getAccountId());
        setSuccessForm("Thay đổi trạng thái thành công");
    }

    public void refresh(AuctionRegisterDto obj) {

        List<Payment> paymentList = paymentRepository.findPaymentsByAuctionRegisterId(obj.getAuctionRegisterId());
        if (!paymentList.isEmpty()) {
            paymentRepository.deleteAll(paymentList);
        }

        List<NotifyPayment> notifyPaymentList = notifyPaymentRepository.findAllByAuctionRegisterId(obj.getAuctionRegisterId());
        if (!notifyPaymentList.isEmpty()) {
            notifyPaymentRepository.deleteAll(notifyPaymentList);
        }

        if (obj.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED_JOIN
                && obj.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE) {
            obj.setStatus(DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED);
            obj.setReasonRefuse(null);
            obj.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE);
        }

        if (obj.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED_JOIN
                && obj.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_ZERO) {
            obj.setStatus(DbConstant.AUCTION_REGISTER_STATUS_WAITING);
            obj.setReasonRefuse(null);
            obj.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_ZERO);
        }

        if (obj.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED
                && obj.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE) {
            obj.setStatus(DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED);
            obj.setReasonRefuse(null);
            obj.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE);
        }

        if (obj.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED
                && obj.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_ZERO) {
            obj.setStatus(DbConstant.AUCTION_REGISTER_STATUS_WAITING);
            obj.setReasonRefuse(null);
            obj.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_ZERO);
        }

        if ((obj.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED_JOIN && obj.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_TWO)
                || (obj.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED && (obj.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_TWO))) {
            obj.setStatus(DbConstant.AUCTION_REGISTER_STATUS_WAITING);
            obj.setReasonRefuse(null);
            obj.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_ZERO);
        }

        if ((obj.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED_JOIN && obj.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_ZERO)
                || (obj.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED && obj.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_ZERO)) {
            obj.setStatus(DbConstant.AUCTION_REGISTER_STATUS_WAITING);
            obj.setReasonRefuse(null);
            obj.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_ZERO);
        }

        if ((obj.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED_JOIN || obj.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED)
                && obj.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_FOUR) {
            obj.setStatus(DbConstant.AUCTION_REGISTER_STATUS_WAITING);
            obj.setReasonRefuse(null);
            obj.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_FOUR);
        }

        BeanUtils.copyProperties(obj, auctionRegister);
        auctionRegister.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        auctionRegister.setUpdateDate(new Date());
        auctionRegisterRepository.save(auctionRegister);
        onSearch();
        actionSystemController.onSave("Xử lý lại đơn yêu cầu của " + obj.getFullNameNguoiDangKy(), authorizationController.getAccountDto().getAccountId());
        setSuccessForm("Xử lý lại đơn yêu cầu thành công");
    }

    public void resetDialog() {
        auctionRegister = new AuctionRegister();
    }

    public void onShow(AuctionRegisterDto dto) {
        this.auctionRegisterDto = dto;
    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        if (StringUtils.isNotBlank(status)) {
            auctionRegisterSearchDto.setStatus(Integer.parseInt(status));
            status = "";
        }
        if (StringUtils.isNotBlank(statusRefund)) {
            auctionRegisterSearchDto.setStatusRefund(Integer.parseInt(statusRefund));
            statusRefund = "";
        }
        if (StringUtils.isNotBlank(assetStatus)) {
            auctionRegisterSearchDto.setAssetStatus(Integer.parseInt(assetStatus));
            assetStatus = "";
        }
        if (StringUtils.isNotBlank(assetStatusCancel)) {
            auctionRegisterSearchDto.setAssetStatusCancel(Integer.parseInt(assetStatusCancel));
            assetStatusCancel = "";
        }
        auctionRegisterSearchDto.setAuctionRegisterRefundPrice(refundPrice);
        lazyDataModel = new LazyDataModel<AuctionRegisterDto>() {
            @Override
            public List<AuctionRegisterDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                auctionRegisterSearchDto.setPageIndex(first);
                auctionRegisterSearchDto.setPageSize(pageSize);
                auctionRegisterSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                auctionRegisterSearchDto.setSortOrder(sort);
                return auctionRegisterRepository.search(auctionRegisterSearchDto);
            }

            @Override
            public AuctionRegisterDto getRowData(String rowKey) {
                List<AuctionRegisterDto> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (AuctionRegisterDto obj : listNhatKy) {
                    if (obj.getRegulationId().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = auctionRegisterRepository.countSearch(auctionRegisterSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }

    public boolean renderCountString(String assetName) {
        if (assetName.length() < 100) {
            return true;
        }
        return false;
    }

    public void createPayment(AuctionRegisterDto dto) {
        if (dto.getRegulationPaymentEndTime() != null) {
            if (dto.getRegulationPaymentEndTime().compareTo(new Date()) < 0) {
                if (dto.getAuctionStatus() == DbConstant.REGULATION_AUCTION_STATUS_ENDED || dto.getAuctionStatus() == DbConstant.REGULATION_AUCTION_STATUS_CANCELLED) {
                    setDataForCreatePayment(dto);
                } else {
                    setErrorForm("Không thể tạo giao dịch đối với tài khoản này vì đã hết thời gian nộp tiền đặt trước");
                    FacesUtil.updateView(Constant.ERROR_GROWL_ID);
                }
            } else {
                setDataForCreatePayment(dto);
            }
        } else {
            setDataForCreatePayment(dto);
        }
    }

    public void setDataForCreatePayment(AuctionRegisterDto dto) {
        paymentController.resetAll();
        paymentController.setAssetId(dto.getAssetId());
        paymentController.setAccountId(dto.getAccountId());
        paymentController.setDisable(false);
        paymentController.onChangeAsset(dto.getAssetId());
        uploadSingleFileController.resetAll(null, null);
        FacesUtil.showDialog("dialogInsertUpdate");
        FacesUtil.updateView("dlg1");
    }

    public void onShowRegulationFile(String regulationFilePath) {
        this.regulationFilePath = regulationFilePath;
    }

    public boolean showButtonReprocessing(AuctionRegisterDto auctionRegisterDto) {
        if (auctionRegisterDto.getAuctionStatus().equals(DbConstant.REGULATION_AUCTION_STATUS_WAITING)) {
            if (auctionRegisterDto.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED_JOIN
                    || auctionRegisterDto.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED) {
                return true;
            }
        }
        return false;
    }

    public boolean showButtonCreatePayment(AuctionRegisterDto auctionRegisterDto) {
        if (auctionRegisterDto.getAuctionStatus() == DbConstant.REGULATION_AUCTION_STATUS_ENDED
                && auctionRegisterDto.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED
                && auctionRegisterDto.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE) {
            if ((!auctionRegisterDto.isRefuseWinner() && auctionRegisterDto.isEnding() && auctionRegisterDto.getStatusDeposit() == DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_NORMAL)
                    || auctionRegisterDto.isStatusRetract()
                    || (auctionRegisterDto.isStatusRefuseWin() && auctionRegisterDto.getStatusDeposit() == DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_NORMAL)
                    || (auctionRegisterDto.getStatusDeposit() == DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_REFUSE && auctionRegisterDto.isStatusRetract())
            ) {
                return false;
            }
        }

        if (auctionRegisterDto.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_ZERO
                && (auctionRegisterDto.getAuctionStatus() == DbConstant.REGULATION_AUCTION_STATUS_ENDED
                || auctionRegisterDto.getAuctionStatus() == DbConstant.REGULATION_AUCTION_STATUS_CANCELLED)) {
            return false;
        }

        if (auctionRegisterDto.getAuctionStatus() == DbConstant.REGULATION_AUCTION_STATUS_WAITING
                && auctionRegisterDto.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_ZERO) {
            if (auctionRegisterDto.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED_JOIN
                    || auctionRegisterDto.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED) {
                return false;
            }
        }

        if (auctionRegisterDto.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_TWO) {
            return false;
        }

        if (auctionRegisterDto.getAuctionStatus() == DbConstant.REGULATION_AUCTION_STATUS_ENDED
                && auctionRegisterDto.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_FOUR) {
            return true;
        }

        if (auctionRegisterDto.getAuctionStatus() == DbConstant.REGULATION_AUCTION_STATUS_WAITING
                && auctionRegisterDto.getStatusRefund() == DbConstant.AUCTION_REGISTER_STATUS_REFUND_FOUR) {
            if (auctionRegisterDto.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED_JOIN ||
                    auctionRegisterDto.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED) {
                return false;
            }
        }

        return true;
    }

    public String renderValueNotes(AuctionRegisterDto auctionRegisterDto) {
        List<String> output = new ArrayList<>();

        if (DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_REFUSE == auctionRegisterDto.getStatusDeposit()
                && !auctionRegisterDto.isStatusRetract()) {
            if (auctionRegisterDto.isStatusJoined() || auctionRegisterDto.getFilePath() != null) {
                output.add("Bị truất quyền" + getCommonController().statusDeposit(auctionRegisterDto.getFilePath() != null, auctionRegisterDto.isStatusRetract()));
            } else {
                output.add("Không tham gia");
            }
        }
        if (DbConstant.AUCTION_REGISTER_STATUS_REJECTED_JOIN == auctionRegisterDto.getStatus()) {
            output.add("Từ chối bởi người dùng");
        }
        if (DbConstant.AUCTION_REGISTER_STATUS_REJECTED == auctionRegisterDto.getStatus()) {
            output.add("Từ chối bởi đấu giá viên");
        }
        if ((Arrays.asList(
                    DbConstant.ASSET_STATUS_ENDED,
                    DbConstant.ASSET_STATUS_CANCELED,
                    DbConstant.ASSET_STATUS_NOT_SUCCESS
            ).contains(auctionRegisterDto.getAssetStatus()))
            && DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED == auctionRegisterDto.getStatus()
            && DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_NORMAL == auctionRegisterDto.getStatusDeposit()) {
            if (auctionRegisterDto.isStatusJoined() && !auctionRegisterDto.isEnding() && !auctionRegisterDto.isStatusRefuseWin()) {
                output.add("Đã tham gia");
            }
            if ((auctionRegisterDto.getAssetManagementId() != null || auctionRegisterDto.isAssetCancelPlaying()) && !auctionRegisterDto.isStatusJoined()) {
                output.add("Không tham gia");
            }
        }
        if (Arrays.asList(
                DbConstant.ASSET_STATUS_ENDED,
                DbConstant.ASSET_STATUS_NOT_SUCCESS
            ).contains(auctionRegisterDto.getAuctionStatus())
                && auctionRegisterDto.isStatusRefuseWin()
                && DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED == auctionRegisterDto.getStatus()
                && DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_NORMAL == auctionRegisterDto.getStatusDeposit()) {
            output.add("Từ chối trúng đấu giá");
        }
        if (auctionRegisterDto.isEnding()
                && DbConstant.ASSET_STATUS_ENDED == auctionRegisterDto.getAssetStatus()
                && !auctionRegisterDto.isStatusRefuseWin()
                && auctionRegisterDto.isEnding()
                && DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED == auctionRegisterDto.getStatus()) {
            output.add("Tài khoản thắng cuộc");
        }
        if (auctionRegisterDto.isStatusRetract()) {
            output.add("Rút lại giá đã trả");
        }

        return String.join(", ", output);
    }

    public boolean showButtonCancelRegister(AuctionRegisterDto auctionRegisterDto) {
        if (auctionRegisterDto.getStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED) {
            return true;
        }
        return false;
    }

    public void getLinkImageShow(String filePath) {
        linkImage = filePath;
    }

    public boolean checkFileImage(String imagePath) {
        return FileUtil.isAcceptFileImageType(imagePath);
    }

    @Override
    protected EScope getMenuId() {
        return EScope.AUCTION_REGISTER;
    }

}

