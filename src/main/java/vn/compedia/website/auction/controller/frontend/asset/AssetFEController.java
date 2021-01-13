package vn.compedia.website.auction.controller.frontend.asset;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.common.UploadSingleFileController;
import vn.compedia.website.auction.controller.admin.common.UploadSingleImageController;
import vn.compedia.website.auction.controller.frontend.AuthorizationFEController;
import vn.compedia.website.auction.controller.frontend.BaseFEController;
import vn.compedia.website.auction.controller.frontend.CommonFEController;
import vn.compedia.website.auction.controller.frontend.common.FacesNoticeController;
import vn.compedia.website.auction.controller.frontend.common.PaginationController;
import vn.compedia.website.auction.controller.frontend.common.UploadSingleFileFEController;
import vn.compedia.website.auction.controller.frontend.common.UploadSingleFilePaymentFEController;
import vn.compedia.website.auction.controller.frontend.user.AuctionHistoryController;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.auction.AssetSearchDto;
import vn.compedia.website.auction.dto.payment.PaymentDto;
import vn.compedia.website.auction.model.Asset;
import vn.compedia.website.auction.model.AuctionRegister;
import vn.compedia.website.auction.model.TypeAsset;
import vn.compedia.website.auction.model.payment.NotifyPayment;
import vn.compedia.website.auction.model.payment.Payment;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.util.*;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@Log4j2
@Named
@Scope(value = "session")
@Getter
@Setter
public class AssetFEController extends BaseFEController {
    @Inject
    private AuthorizationFEController authorizationFEController;
    @Inject
    private AuctionHistoryController auctionHistoryController;
    @Inject
    private UploadSingleFilePaymentFEController uploadSingleFilePaymentFEController;
    @Inject
    private HttpServletRequest request;
    @Inject
    private CommonFEController commonFEController;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private TypeAssetRepository typeAssetRepository;
    @Autowired
    private RegulationRepository regulationRepository;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private NotifyPaymentRepository notifyPaymentRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    private PaginationController<AssetDto> pagination;
    private List<AssetDto> assetDtoList;
    private AssetSearchDto assetSearchDto;
    private String fromPrice;
    private String toPrice;
    private List<SelectItem> typeAssetList;
    private NotifyPayment notifyPayment;
    private AssetDto assetDtoCopyFromPayment;
    private Long auctionRegisterId;
    private String amountPayment;

    private final String ERROR_MESSAGE_HIDE = "Lỗi không xác định";

    public AssetFEController() {
        pagination = new PaginationController<>();
        pagination.setRequest(request);
        pagination.setPageSize(9);
        pagination.setPageOption("9,15,45,90");
        pagination.resetAll();
    }

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            pagination.setRequest(request);
            resetAll();
        }
    }

    public void resetAll() {
        assetDtoList = new ArrayList<>();
        assetSearchDto = new AssetSearchDto();
        // asset search
        String typeAssetId = FacesUtil.getRequestParameter("typeAssetId");
        String assetStatus = FacesUtil.getRequestParameter("assetStatus");
        String regulationId = FacesUtil.getRequestParameter("regulationId");
        fromPrice = FacesUtil.getRequestParameter("fromPrice");
        toPrice = FacesUtil.getRequestParameter("toPrice");
        if (StringUtils.isNotBlank(fromPrice)) {
            fromPrice = fromPrice.replaceAll("^(0+\\.?)*", "");
        }
        if (StringUtils.isNotBlank(toPrice)) {
            toPrice = toPrice.replaceAll("^(0+\\.?)*", "");
        }
        assetSearchDto.setKeyword(FacesUtil.getRequestParameter("keyword"));
        assetSearchDto.setFromTime(DateUtil.formatDatePattern(FacesUtil.getRequestParameter("fromDate"), DateUtil.DDMMYYYY));
        assetSearchDto.setToTime(DateUtil.formatDatePattern(FacesUtil.getRequestParameter("toDate"), DateUtil.DDMMYYYY));
        assetSearchDto.setAssetStatus(StringUtils.isBlank(assetStatus) ? null : Integer.parseInt(assetStatus));
        assetSearchDto.setTypeAssetId(StringUtils.isBlank(typeAssetId) ? null : Long.parseLong(typeAssetId));
        assetSearchDto.setRegulationId(StringUtils.isBlank(regulationId) ? null : Long.parseLong(regulationId));
        assetSearchDto.setFromPrice(StringUtils.isBlank(fromPrice) ? null : Long.parseLong(fromPrice.replaceAll("[^0-9]", "")));
        assetSearchDto.setToPrice(StringUtils.isBlank(toPrice) ? null : Long.parseLong(toPrice.replaceAll("[^0-9]", "")));

        //
        typeAssetList = new ArrayList<>();
        List<TypeAsset> typeAssets = typeAssetRepository.findTypeAssetByStatus();
        for (TypeAsset ac : typeAssets) {
            typeAssetList.add(new SelectItem(ac.getTypeAssetId(), ac.getName()));
        }

        onSearch();
    }

    public void resetDialog() {
        assetSearchDto = new AssetSearchDto();
        fromPrice = "";
        toPrice = "";
    }

    public void onSearchQuery() {

        if ((assetSearchDto.getFromTime() != null && assetSearchDto.getToTime() != null)
                && DateUtil.formatDate(assetSearchDto.getFromTime(), DateUtil.FROM_DATE_FORMAT).compareTo(DateUtil.formatDate(assetSearchDto.getToTime(), DateUtil.TO_DATE_FORMAT)) >= 0) {
            setErrorForm("Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc");
            return;
        }
        if (assetSearchDto.getFromPrice() != null && assetSearchDto.getToPrice() != null
                && assetSearchDto.getFromPrice().compareTo(assetSearchDto.getToPrice()) >= 0){
            setErrorForm("Giá khởi điểm phải nằm trong khoảng giá bạn nhập");
            return;
        }

        Map<String, String> search = new LinkedHashMap<>();
        String fromDate = DateUtil.formatToPattern(assetSearchDto.getFromTime(), DateUtil.DDMMYYYY);
        String toDate = DateUtil.formatToPattern(assetSearchDto.getToTime(), DateUtil.DDMMYYYY);
        search.put("assetStatus", assetSearchDto.getAssetStatus() == null ? "" : String.valueOf(assetSearchDto.getAssetStatus()));
        search.put("fromDate", fromDate == null ? "" : fromDate);
        search.put("toDate", toDate == null ? "" : toDate);
        search.put("keyword", assetSearchDto.getKeyword().trim());
        search.put("typeAssetId", assetSearchDto.getTypeAssetId() == null ? "" : String.valueOf(assetSearchDto.getTypeAssetId()));
        if (StringUtils.isNotBlank(fromPrice)) {
            fromPrice = fromPrice.replaceAll("^(0+\\.?)*", "");
        }
        if (StringUtils.isNotBlank(toPrice)) {
            toPrice = toPrice.replaceAll("^(0+\\.?)*", "");
        }
        search.put("fromPrice", fromPrice);
        search.put("toPrice", toPrice);

        String redirect = String.format(
                "/frontend/asset/asset_list.xhtml?%s",
                StringUtil.buildQueryParam(search)
        );
        FacesUtil.redirect(redirect);
    }

    public void onSearch() {

        if (assetSearchDto.getRegulationId() != null) {
            assetSearchDto.setSortField("serialNumber");
            assetSearchDto.setSortOrder("ASC");
        }

        assetSearchDto.setRegulationStatus(DbConstant.REGULATION_STATUS_NOT_PUBLIC);
        pagination.setLazyDataModel(new LazyDataModel<AssetDto>() {
            public List<AssetDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                assetSearchDto.setPageIndex(first);
                assetSearchDto.setPageSize(pageSize);
                assetSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder == null || sortOrder.equals(SortOrder.DESCENDING)) {
                    sort = "DESC";
                } else {
                    sort = "ASC";
                }
                assetSearchDto.setSortOrder(sort);
                return assetRepository.searchForGuestUser(assetSearchDto);
            }

            @Override
            public int getRowCount() {
                return assetRepository.countSearchForGuest(assetSearchDto).intValue();
            }
        });
        pagination.loadData();
    }

    public String formatDate(Date date) {
        return DateUtil.formatToPattern(date, DateUtil.MMDDYYYYHHMMSS);
    }

    public void takePaymentOption(AssetDto assetDto) {
        if (assetDto.getAuctionRegisterId() == null) {
            setErrorForm("Bạn chưa đăng ký tham gia đấu giá tài sản này");
            return;
        }
        if (!commonFEController.renderButtonPayment(assetDto)) {
            setErrorForm("Thời gian thanh toán chưa đến hoặc đã hết hạn");
            return;
        }
        auctionRegisterId = assetDto.getAuctionRegisterId();
        Integer countAuctionRegisterId = paymentRepository.countAuctionRegisterIdSuccess(assetDto.getAuctionRegisterId());
        if (countAuctionRegisterId != 0) {
            setErrorForm("Bạn đã thanh toán tiền đặt trước cho tài sản này");
            return;
        }
        Integer countNotifyPaymentId = notifyPaymentRepository.countNotifyPaymentId(assetDto.getAssetId(), assetDto.getRegulationId(), authorizationFEController.getAccountDto().getAccountId());
        if (countNotifyPaymentId != 0) {
            setErrorForm("Bạn đã gửi biên lai thanh toán cho quản trị viên, vui lòng chờ phê duyệt hoá đơn thanh toán của bạn");
            return;
        }
        notifyPayment = new NotifyPayment();
        amountPayment = assetDto.getDeposit().toString();
        assetDtoCopyFromPayment = new AssetDto();
        uploadSingleFilePaymentFEController.resetAll(null);
        BeanUtils.copyProperties(assetDto, assetDtoCopyFromPayment);

        if (auctionRegisterId != null) {
            auctionHistoryController.setPaymentAuctionRegisterId(auctionRegisterId);
            notifyPayment.setAuctionRegisterId(auctionRegisterId);
        } else {
            setErrorForm("Tài sản chưa được đăng ký tham gia đấu giá");
            return;
        }
        facesNotice().openModal("paymentOptionPopup");
    }

    public void createNotifyPayment() {
        if (notifyPayment.getAuctionRegisterId() != null) {
            notifyPayment.setAccountId(authorizationFEController.getAccountDto().getAccountId());
            notifyPayment.setAssetId(assetDtoCopyFromPayment.getAssetId());
            notifyPayment.setRegulationId(assetDtoCopyFromPayment.getRegulationId());
            notifyPayment.setFilePath(uploadSingleFilePaymentFEController.getUploadSingleFileFEDto().getFilePath());
            notifyPayment.setFileName(uploadSingleFilePaymentFEController.getUploadSingleFileFEDto().getFileName());
            notifyPayment.setCreateBy(authorizationFEController.getAccountDto().getAccountId());
            notifyPayment.setStatus(DbConstant.NOTIFY_PAYMENT_STATUS_NON_CREATE);
            if (amountPayment == null) {
                setErrorForm("Số tiền thanh toán là trường bắt buộc");
                return;
            }
            try {
                notifyPayment.setAmount(Long.parseLong(amountPayment.replaceAll("[^0-9]", "")));
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
            facesNotice().addSuccessMessage("Gửi biên lai thanh toán thành công");
            facesNotice().activeFunction("cmdRefreshData");
            facesNotice().closeModal("paymentOptionPopup");
        } else {
            setErrorForm("Tài sản chưa được đăng ký tham gia đấu giá");
        }
    }

//    public StreamedContent sendBill(PaymentDto paymentIndex){
//        if (paymentIndex != null) {
//            Date now = new Date();
//            paymentIndex.setTime(DateUtil.formatToPattern(now,DateUtil.DAY_DD_MONTH_MM_YEAR_YYYY));
//            paymentIndex.setMoneyVietWords(" " + ExportNumberToVietWords.num2String(paymentIndex.getMoney()) + " đồng");
//            StreamedContent streamedContent = BillWordUtil.downloadBillTemplate(paymentIndex);
//            if(!EmailUtil.getInstance().sendBill(paymentIndex.getMail(), FacesUtil.getRealPath(Constant.REPORT_EXPORT_FILE_BILL), "Bill.docx", paymentIndex.getFullName())){
//                return null;
//            }
//            return streamedContent;
//        } else {
//            return null;
//        }
//    }

    @Override
    protected String getMenuId() {
        return Constant.ID_ASSET_FRONTEND;
    }
}
