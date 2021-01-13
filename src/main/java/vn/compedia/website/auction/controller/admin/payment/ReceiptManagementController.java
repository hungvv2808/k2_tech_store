package vn.compedia.website.auction.controller.admin.payment;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.CommonController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.controller.admin.common.CityDistrictController;
import vn.compedia.website.auction.controller.frontend.common.FacesNoticeController;
import vn.compedia.website.auction.dto.common.CityDistrictDto;
import vn.compedia.website.auction.dto.payment.PaymentDto;
import vn.compedia.website.auction.dto.payment.ReceiptManagementDto;
import vn.compedia.website.auction.dto.payment.ReceiptManagementSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.payment.Payment;
import vn.compedia.website.auction.model.payment.ReceiptManagement;
import vn.compedia.website.auction.repository.AssetRepository;
import vn.compedia.website.auction.repository.PaymentRepository;
import vn.compedia.website.auction.repository.ReceiptManagementRepository;
import vn.compedia.website.auction.util.*;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = "session")
@Getter
@Setter
public class ReceiptManagementController extends BaseController {
    @Inject
    private FacesNoticeController facesNoticeController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private CityDistrictController cityDistrictController;
    @Inject
    private CommonController commonController;

    @Autowired
    private ReceiptManagementRepository receiptManagementRepository;
    @Autowired
    protected AssetRepository assetRepository;
    @Autowired
    protected PaymentRepository paymentRepository;
    @Autowired
    protected ActionSystemController actionSystemController;

    private ReceiptManagement receiptManagement;
    private ReceiptManagementDto receiptManagementDto;
    private ReceiptManagementSearchDto searchDto;
    private LazyDataModel<ReceiptManagementDto> lazyDataModel;
    private Long paymentId = null;
    private List<SelectItem> codePaymentList;
    private List<SelectItem> codePaymentForSearchList;
    private List<PaymentDto> paymentDtoList;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        actionSystemController.resetAll();
        receiptManagement = new ReceiptManagement();
        searchDto = new ReceiptManagementSearchDto();
        cityDistrictController.resetAll();
        cityDistrictController.setCheckDisable(true);
        lazyDataModel = new LazyDataModel<ReceiptManagementDto>() {
            @Override
            public List<ReceiptManagementDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
        resetDialog();
        onSearch();
        paymentDtoList = new ArrayList<>();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public boolean validateData(ReceiptManagementDto receiptManagementDto) {
        if (receiptManagementRepository.countIdByCode(paymentId) != 0 && receiptManagementDto.isStatus()) {
            FacesUtil.addErrorMessage("Mã giao dịch đã được tạo biên lai");
            FacesUtil.updateView("growl");
            return false;
        }
        return true;
    }

    public void onSaveData() {
        if (!validateData(receiptManagementDto)) {
            return;
        }
        BeanUtils.copyProperties(receiptManagementDto, receiptManagement);
        if (paymentId != null) {
            receiptManagement.setPaymentId(paymentId);
        }
        receiptManagement.setStatus(DbConstant.RECEIPT_MANAGEMENT_SEND_FAIL);

        ReceiptManagement receiptManagementTmp = receiptManagementRepository.save(receiptManagement);
        if (receiptManagementTmp == null) {
            FacesUtil.addErrorMessage("Lưu thất bại");
            return;
        }

        if (receiptManagementTmp.getCode() == null ){
            receiptManagementTmp.setCode(String.format("BL%06d", receiptManagementTmp.getReceiptManagementId()));
            actionSystemController.onSave("Tạo mới thông tin biên lai \"" + receiptManagementTmp.getCode() + "\"", authorizationController.getAccountDto().getAccountId());
            receiptManagementTmp.setCreateBy(authorizationController.getAccountDto().getAccountId());
        } else {
            actionSystemController.onSave("Cập nhật thông tin biên lai \"" + receiptManagementTmp.getCode() + "\"", authorizationController.getAccountDto().getAccountId());
            receiptManagementTmp.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        }
        receiptManagementRepository.save(receiptManagementTmp);

        FacesUtil.addSuccessMessage("Lưu thành công");
        FacesUtil.closeDialog("dialogInsertUpdate");
        FacesUtil.updateView("growl");
        FacesUtil.updateView("searchForm");
        resetAll();
    }

    public void resetDialog() {
        paymentId = null;
        receiptManagementDto = new ReceiptManagementDto();
        codePaymentList = new ArrayList<>();
        codePaymentForSearchList = new ArrayList<>();
        paymentDtoList = receiptManagementRepository.searchPaymentReceipt();
        for (PaymentDto p : paymentDtoList) {
            if(p.getReceiptManagementId() == null) {
                codePaymentList.add(new SelectItem(p.getPaymentId(), p.getCode()));
            }
            codePaymentForSearchList.add(new SelectItem(p.getPaymentId(), p.getCode()));
        }
        cityDistrictController.resetAll();
        cityDistrictController.setCheckDisable(true);
    }

    public void onDateSelect() {
        if (null != searchDto.getStartDate() && null != searchDto.getEndDate()
                && searchDto.getStartDate().compareTo(searchDto.getEndDate()) > 0) {
            searchDto.setEndDate(null);
            setErrorForm("Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
        }
    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<ReceiptManagementDto>() {
            @Override
            public List<ReceiptManagementDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                searchDto.setPageIndex(first);
                searchDto.setPageSize(pageSize);
                searchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                searchDto.setSortOrder(sort);
                return receiptManagementRepository.search(searchDto);
            }
        };
        int count = receiptManagementRepository.countSearch(searchDto).intValue();
        lazyDataModel.setRowCount(count);
    }

    public StreamedContent sendBill(ReceiptManagementDto receiptManagementView) {
        Account account = receiptManagementRepository.getAllByPaymentId(receiptManagementView.getPaymentId());
        Payment payment1 = paymentRepository.findById(receiptManagementView.getPaymentId()).orElse(null);
        receiptManagementView.setPaymentCode(String.format("GD%06d", receiptManagementView.getPaymentId()));
        receiptManagementView.setPaymentCode(String.format("GD%06d", receiptManagementView.getPaymentId()));
        receiptManagementView.setTime(DateUtil.formatToPattern(new Date(), DateUtil.DAY_DD_MONTH_MM_YEAR_YYYY));
        receiptManagementView.setMoneyVietWords(" " + upperCaseFirstChar(ExportNumberToVietWords.num2String(receiptManagementView.getAmount())) + " đồng");
        receiptManagementView.setMail(account.getEmail());
        receiptManagementView.setPayerFullname(account.isOrg() ? account.getOrgName() : account.getFullName());
        String address = account.isOrg() ? account.getOrgAddress() : account.getAddress();
        receiptManagementView.setPayerAddress(upperCaseFirstChar(commonController.showAddress(address, receiptManagementView.getCommuneName(), receiptManagementView.getDistrictName(), receiptManagementView.getProvinceName())));
        receiptManagementView.setTitleBill(receiptManagementView.getTitleBill().toUpperCase());
        StreamedContent rs = BillWordUtil.downloadBillTemplate1(receiptManagementView);
        String name = account.isOrg() ? account.getOrgName() : account.getFullName();
        if(payment1 != null) {
            if(payment1.getStatus() == DbConstant.PAYMENT_STATUS_PAID) {
                if (!EmailUtil.getInstance().sendBillPaid(
                        receiptManagementView.getMail(),
                        FacesUtil.getRealPath(Constant.REPORT_EXPORT_FILE_BILL),
                        "Bill.docx",
                        receiptManagementView.getContentPayment(),
                        receiptManagementView.getAssetName(),
                        receiptManagementView.getRegulationCode(),
                        name)) {
                    FacesUtil.addErrorMessage("Có lỗi trong quá trình xuất dữ liệu");
                    return null;
                }
            } else if (payment1.getStatus() == DbConstant.PAYMENT_STATUS_REFUND){
                if (!EmailUtil.getInstance().sendBillRefund(
                        receiptManagementView.getMail(),
                        FacesUtil.getRealPath(Constant.REPORT_EXPORT_FILE_BILL),
                        "Bill.docx",
                        receiptManagementView.getContentPayment(),
                        receiptManagementView.getAssetName(),
                        receiptManagementView.getRegulationCode(),
                        name)) {
                    FacesUtil.addErrorMessage("Có lỗi trong quá trình xuất dữ liệu");
                    return null;
                }
            }
        }
        if (rs != null) {
            // update status on receipt management
            ReceiptManagement receiptManagementSave = new ReceiptManagement();
            BeanUtils.copyProperties(receiptManagementView, receiptManagementSave);
            receiptManagementSave.setStatus(DbConstant.RECEIPT_MANAGEMENT_SENDED);
            receiptManagementSave.setUpdateBy(authorizationController.getAccountDto().getAccountId());
            receiptManagementSave.setUpdateDate(new Date());
            receiptManagementRepository.save(receiptManagementSave);

            actionSystemController.onSave("Gửi thông tin biên lai \"" + receiptManagementSave.getCode() + "\"", authorizationController.getAccountDto().getAccountId());

            String pathBill = FileUtil.copyFileToStorage(FacesUtil.getRealPath(Constant.REPORT_EXPORT_FILE_BILL));
            Payment payment = paymentRepository.findById(receiptManagementSave.getPaymentId()).orElse(null);
            if (payment != null) {
                payment.setReceiptFilePath(pathBill);
                paymentRepository.save(payment);
            }

            FacesUtil.addSuccessMessage("Gửi biên lai thành công");
            FacesUtil.updateView("searchForm");
            return rs;
        } else {
            FacesUtil.addErrorMessage("Có lỗi trong quá trình xuất dữ liệu");
            return null;
        }
    }

    public void onChangeReceipt(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        Account account = receiptManagementRepository.getAllByPaymentId(paymentId);
        if (account != null) {
            receiptManagementDto.setPayerFullname(account.isOrg() ? account.getOrgName() : account.getFullName());
            receiptManagementDto.setPayerAddress(account.isOrg() ? account.getOrgAddress() : account.getAddress());
            receiptManagementDto.setAmount(payment.getMoney());
            receiptManagementDto.setContentPayment(null);
            if (account.getProvinceId() == null && account.getDistrictId() == null && account.getCommuneId() == null) {
                cityDistrictController.setCityDistrictDto(new CityDistrictDto());
                return;
            } else {
                cityDistrictController.setCityDistrictDto(new CityDistrictDto(account.getProvinceId(), account.getDistrictId(), account.getCommuneId()));
                cityDistrictController.loadData();
            }
        } else {
            receiptManagementDto = new ReceiptManagementDto();
            cityDistrictController.resetAll();
            cityDistrictController.setCheckDisable(true);
            FacesUtil.addErrorMessage("Người thanh toán không tồn tại hoặc đã bị xoá khỏi cơ sở dữ liệu");
            FacesUtil.updateView("growl");
            return;
        }
    }

    public void showUpdatePopup(ReceiptManagementDto obj) {
        Payment payment = paymentRepository.findPaymentByPaymentId(obj.getPaymentId());
        Account account = receiptManagementRepository.getAllByPaymentId(obj.getPaymentId());
        codePaymentList.add(new SelectItem(payment.getPaymentId(),payment.getCode()));
        paymentId = obj.getPaymentId();
        cityDistrictController.setCityDistrictDto(new CityDistrictDto(account.getProvinceId(), account.getDistrictId(), account.getCommuneId()));
        cityDistrictController.loadData();
        BeanUtils.copyProperties(obj, receiptManagementDto);
    }

    public String upperCaseFirstChar(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    @Override
    protected EScope getMenuId() {
        return EScope.RECEIPT_MANAGEMENT;
    }
}
