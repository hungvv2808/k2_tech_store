package vn.compedia.website.auction.controller.admin.payment;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.UploadSingleFileController;
import vn.compedia.website.auction.dto.payment.NotifyPaymentDto;
import vn.compedia.website.auction.dto.payment.NotifyPaymentSearchDto;
import vn.compedia.website.auction.dto.payment.PaymentDto;
import vn.compedia.website.auction.dto.user.AccountDto;
import vn.compedia.website.auction.entity.EFunction;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.model.payment.NotifyPayment;
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
public class TransactionController extends BaseController {
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private UploadSingleFileController uploadSingleFileController;

    @Autowired
    private NotifyPaymentRepository notifyPaymentRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    protected AssetRepository assetRepository;
    @Autowired
    protected RegulationRepository regulationRepository;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private CommuneRepository communeRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private RegulationFileRepository regulationFileRepository;
    @Autowired
    private AccountRepository accountRepository;

    private LazyDataModel<NotifyPaymentDto> lazyDataModel;
    private Payment payment;
    private NotifyPayment notifyPayment;
    private NotifyPaymentDto notifyPaymentDto;
    private NotifyPaymentSearchDto notifyPaymentSearchDto;
    private List<Asset> assetList;
    private List<Regulation> regulationList;
    private List<SelectItem> accountSelectItemList;
    private List<AccountDto> accountDtoList;
    private String regulationFilePath;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        payment = new Payment();
        notifyPaymentDto = new NotifyPaymentDto();
        notifyPaymentSearchDto = new NotifyPaymentSearchDto();
        uploadSingleFileController.resetAll(null, null);
        regulationList = new ArrayList<>();
        assetList = new ArrayList<>();
        accountSelectItemList = new ArrayList<>();
        accountDtoList = new ArrayList<>();
        regulationList = regulationRepository.getRegulationOrderByAuctionStatus(DbConstant.REGULATION_STATUS_NOT_PUBLIC);
        onSearch();
    }

    public void onChangeRegulation(Long regulationId) {
        assetList = new ArrayList<>();
        if (regulationId != null) {
            assetList = assetRepository.findAssetsByRegulationId(regulationId);
        }
        accountSelectItemList = new ArrayList<>();
    }

    public void onChangeAsset(Long assetId) {
        if (assetId == null) {
            return;
        }

        List<Account> accountList = accountRepository.getAccountByAssetIdAndStatus(assetId, DbConstant.AUCTION_REGISTER_STATUS_WAITING);
        if (accountList.isEmpty()) {
            return;
        }

        for (Account account : accountList) {
            if (account.isOrg()) {
                accountSelectItemList.add(new SelectItem(account.getAccountId(), account.getOrgName()));
            } else {
                accountSelectItemList.add(new SelectItem(account.getAccountId(), account.getFullName()));
            }
        }

    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<NotifyPaymentDto>() {

            @Override
            public List<NotifyPaymentDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                notifyPaymentSearchDto.setPageIndex(first);
                notifyPaymentSearchDto.setPageSize(pageSize);
                notifyPaymentSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                notifyPaymentSearchDto.setSortOrder(sort);
                return notifyPaymentRepository.search(notifyPaymentSearchDto);
            }
        };
        int count = notifyPaymentRepository.countSearch(notifyPaymentSearchDto).intValue();
        lazyDataModel.setRowCount(count);
    }

    public Payment convertNotifyPaymentToPayment(NotifyPaymentDto notifyPaymentDtoInput) {
        Payment paymentOutput = new Payment();
        paymentOutput.setAuctionRegisterId(notifyPaymentDtoInput.getAuctionRegisterId());
        paymentOutput.setMoney(notifyPaymentDtoInput.getAmount());
        paymentOutput.setStatus(DbConstant.PAYMENT_STATUS_PAID);
        paymentOutput.setPaymentFormality(DbConstant.PAYMENT_FORMALITY_OFFLINE);
        paymentOutput.setNote(notifyPaymentDtoInput.getPaymentNote());
        paymentOutput.setFilePath(notifyPaymentDtoInput.getFilePath());
        paymentOutput.setFileName(notifyPaymentDtoInput.getFileName());
        paymentOutput.setSendBillStatus(DbConstant.PAYMENT_SEND_BILL_STATUS_UNPAID);
        paymentOutput.setCreateDate(new Date());
        paymentOutput.setCreateBy(authorizationController.getAccountDto().getAccountId());
        return paymentOutput;
    }

    public void showUpdatePopup(NotifyPaymentDto notifyPaymentDtoView) {
        payment = convertNotifyPaymentToPayment(notifyPaymentDtoView);
        BeanUtils.copyProperties(notifyPaymentDtoView, notifyPaymentDto);
        uploadSingleFileController.setFilePath(notifyPaymentDtoView.getFilePath());
        uploadSingleFileController.setFileName(notifyPaymentDtoView.getFileName());
    }

    public void onSaveData() {
        Payment paymentCoppy = paymentRepository.save(payment);
        paymentCoppy.setCode("GD" + String.format("%06d", paymentCoppy.getPaymentId()));
        paymentRepository.save(paymentCoppy);

        AuctionRegister auctionRegister = auctionRegisterRepository.findById(paymentCoppy.getAuctionRegisterId()).orElse(null);
        auctionRegister.setStatus(DbConstant.DANG_KY_THANH_CONG);
        auctionRegisterRepository.save(auctionRegister);

        actionSystemController().onSave("Đã thực hiện đối soát và tạo thông tin giao dịch \"" + paymentCoppy.getCode() + "\"", authorizationController.getAccountDto().getAccountId());

//        PaymentDto paymentDto = new PaymentDto();
//        BeanUtils.copyProperties(paymentCoppy, paymentDto);
//        Account account = auctionRegisterRepository.findAccountByAuctionRegisterId(paymentCoppy.getAuctionRegisterId());
//        paymentDto.setMail(account.getEmail());
//        paymentDto.setFullName(account.getFullName());
//        paymentDto.setAddress(getAddress(account));
//        StreamedContent streamedContent = sendBill(paymentDto);
//        if (streamedContent != null) {
//            System.err.println("Update data on table payment complete !!!");
//        }

        NotifyPayment notifyPayment = new NotifyPayment();
        BeanUtils.copyProperties(notifyPaymentDto, notifyPayment);
        notifyPayment.setStatus(DbConstant.NOTIFY_PAYMENT_STATUS_CREATED);
        notifyPaymentRepository.save(notifyPayment);

        FacesUtil.addSuccessMessage("Đối soát giao dịch thành công");
        FacesUtil.closeDialog("dialogInsertUpdate");
        resetAll();
        FacesUtil.updateView("growl");
        FacesUtil.updateView("searchForm");
        onSearch();
    }

    public StreamedContent sendBill(PaymentDto paymentIndex) {
        if (paymentIndex != null) {
            Date now = new Date();
            paymentIndex.setTime(DateUtil.formatToPattern(now, DateUtil.DAY_DD_MONTH_MM_YEAR_YYYY));
            paymentIndex.setMoneyVietWords(" " + ExportNumberToVietWords.num2String(paymentIndex.getMoney()) + " đồng");
            StreamedContent streamedContent = BillWordUtil.downloadBillTransactionControlTemplate(paymentIndex);
            if (!EmailUtil.getInstance().sendBillTransactionControl(paymentIndex.getMail(), paymentIndex.getFullName())) {
                return null;
            }
            return streamedContent;
        } else {
            return null;
        }
    }

    public String getAddress(Account account) {
        Commune commune = communeRepository.findById(account.getCommuneId()).orElse(null);
        District district = districtRepository.findById(account.getDistrictId()).orElse(null);
        Province province = provinceRepository.findById(account.getProvinceId()).orElse(null);
        return account.getAddress() + (commune != null ? ", " + commune.getName() : " ") + (district != null ? ", " + district.getName() : " ") + (province != null ? ", " + province.getName() : " ");
    }

    public void onDateSelect() {
        if (null != this.notifyPaymentSearchDto.getFromDate() && null != this.notifyPaymentSearchDto.getToDate()
                && this.notifyPaymentSearchDto.getFromDate().compareTo(this.notifyPaymentSearchDto.getToDate()) > 0) {
            this.getNotifyPaymentSearchDto().setToDate(null);
            setErrorForm("Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
        }
    }

    //Get file path regulation
    public void getFilePathRegulation(NotifyPaymentDto dto) {
        regulationFilePath = regulationFileRepository.findRegulationFileByRegulationIdAndType(dto.getRegulationId(), DbConstant.REGULATION_FILE_TYPE_QUY_CHE).getFilePath();
    }

    public EFunction getActions() {
        return authorizationController.getActions(getMenuId());
    }

    public String convertDateToPattern(Date date) {
        return DateUtil.formatToPattern(date, DateUtil.HOUR_DATE_FORMAT);
    }

    public String renderStatus(NotifyPaymentDto check) {
        if (check.getPaymentId() != null && check.getStatus() == DbConstant.NOTIFY_PAYMENT_STATUS_CREATED) {
            return "Đã đối soát và tạo giao dịch";
        }
        if (check.getPaymentId() == null && check.getStatus() == DbConstant.NOTIFY_PAYMENT_STATUS_NON_CREATE && check.getPaymentEndTime().compareTo(new Date()) <= 0) {
            return "Đã quá thời gian nộp tiền đặt trước";
        }
        if (check.getPaymentId() == null && check.getStatus() == DbConstant.NOTIFY_PAYMENT_STATUS_NON_CREATE && check.getPaymentEndTime().compareTo(new Date()) > 0) {
            return "Chưa đối soát";
        }
        return null;
    }

    public boolean renderButtonCreatePayment(NotifyPaymentDto check) {
        if (check.getPaymentId() == null && check.getStatus() == DbConstant.NOTIFY_PAYMENT_STATUS_NON_CREATE && check.getPaymentEndTime().compareTo(new Date()) > 0) {
            return true;
        }
        return false;
    }

    @Override
    protected EScope getMenuId() {
        return EScope.PAYMENT;
    }
}
