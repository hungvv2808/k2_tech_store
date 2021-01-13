package vn.compedia.website.auction.controller.frontend.user;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.common.PdfSignatureInfoController;
import vn.compedia.website.auction.controller.frontend.AuthorizationFEController;
import vn.compedia.website.auction.controller.frontend.BaseFEController;
import vn.compedia.website.auction.controller.frontend.common.FacesNoticeController;
import vn.compedia.website.auction.controller.frontend.common.PaginationAjaxController;
import vn.compedia.website.auction.controller.frontend.common.PaginationController;
import vn.compedia.website.auction.controller.frontend.common.UploadSingleFilePaymentFEController;
import vn.compedia.website.auction.dto.PdfSignatureInfo.PdfSignatureInfoDto;
import vn.compedia.website.auction.dto.common.UploadSingleFileFEDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.dto.user.PropertySaleHistoryDto;
import vn.compedia.website.auction.dto.user.PropertySaleHistorySearchDto;
import vn.compedia.website.auction.entity.frontend.MyAsset;
import vn.compedia.website.auction.entity.frontend.MyAuctionHistory;
import vn.compedia.website.auction.model.RegulationReportFile;
import vn.compedia.website.auction.model.RegulationReportUser;
import vn.compedia.website.auction.repository.AssetRepository;
import vn.compedia.website.auction.repository.PropertySaleHistoryRepository;
import vn.compedia.website.auction.repository.RegulationReportFileRepository;
import vn.compedia.website.auction.repository.RegulationReportUserRepository;
import vn.compedia.website.auction.util.*;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InvalidNameException;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = "session")
@Getter
@Setter
@Log4j2
public class PropertySaleHistoryController extends BaseFEController {
    @Inject
    private AuthorizationFEController authorizationFEController;
    @Inject
    private FacesNoticeController facesNoticeController;
    @Inject
    private PdfSignatureInfoController pdfSignatureInfoController;

    @Autowired
    private PropertySaleHistoryRepository propertySaleHistoryRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private RegulationReportFileRepository regulationReportFileRepository;
    @Autowired
    private RegulationReportUserRepository regulationReportUserRepository;

    private PaginationAjaxController<String> pagination;
    private PaginationController<String> paginationController;
    private PropertySaleHistorySearchDto searchDto;
    private PropertySaleHistoryDto propertySaleHistoryDto;
    private List<MyAuctionHistory> myAuctionHistoryList;
    private String data;
    private Long regulationId;
    private Long assetId;
    private RegulationReportFile regulationReportFile;
    private RegulationReportUser regulationReportUser;
    private Integer renderZone;

    // variable for search active status auction by field
    private Integer SEARCH_ALL = 0;
    private Integer SEARCH_BY_SALE = 1;
    private Integer SEARCH_BY_BUY = 2;

    // variable rendered signature
    private Integer SIGNATURE_DEFAULT = 0;
    private Integer SIGNATURE_SIGNED = 1;
    private Integer SIGNATURE_NOT_SIGN = 2;

    public PropertySaleHistoryController() {
        pagination = new PaginationAjaxController<>();
        pagination.resetAll();
    }

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        renderZone = SIGNATURE_DEFAULT;
        searchDto = new PropertySaleHistorySearchDto();
        searchDto.setCheckStatusSearch(SEARCH_ALL);
        regulationReportFile = new RegulationReportFile();
        regulationReportUser = new RegulationReportUser();
        pdfSignatureInfoController.resetAll();
        onSearch();
    }


    public void onSearch() {
        if (pagination.getPageIndex() > 1) {
            pagination.onChangeFirstPage(1);
        }
        searchDto.setAccountId(authorizationFEController.getAccountDto().getAccountId());
        searchDto.setCheckStatusSearch(searchDto.getCheckStatusSearch() == null ? SEARCH_ALL : searchDto.getCheckStatusSearch());
        if (StringUtils.isNotBlank(searchDto.getAssetName())) {
            searchDto.setAssetName(searchDto.getAssetName());
        }
        if (searchDto.getFromDate() != null && searchDto.getToDate() != null) {
            searchDto.setFromDate(DateUtil.formatDate(searchDto.getFromDate(), DateUtil.FROM_DATE_FORMAT));
            searchDto.setToDate(DateUtil.formatDate(searchDto.getToDate(), DateUtil.TO_DATE_FORMAT));
            if (searchDto.getFromDate().compareTo(searchDto.getToDate()) >= 0) {
                facesNoticeController.addErrorMessage("Thời gian từ ngày phải nhỏ hơn thời gian đến ngày");
                return;
            }
        }
        pagination.setLazyDataModel(new LazyDataModel<String>() {
            @Override
            public List<String> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                searchDto.setPageIndex(first);
                searchDto.setPageSize(pageSize);
                searchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder == null || sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                searchDto.setSortOrder(sort);
                List<String> temp = new ArrayList<>();
                temp.add(buildDataString(propertySaleHistoryRepository.search(searchDto)));
                return temp;
            }

            @Override
            public int getRowCount() {
                return propertySaleHistoryRepository.countSearch(searchDto).intValue();
            }
        });
        pagination.loadData();
    }

    public String buildDataString(List<PropertySaleHistoryDto> propertySaleHistoryDtoList) {
        myAuctionHistoryList = new ArrayList<>();
        int pageNumber = (pagination.getPageIndex() - 1) * pagination.getPageSize();
        int i = 1;
        for (PropertySaleHistoryDto propertySaleHistoryDto : propertySaleHistoryDtoList) {
            List<MyAsset> myAssetList = new ArrayList<>();
            for (RegulationDto regulationDto : propertySaleHistoryDto.getRegulationDtoList()) {
                myAssetList.add(new MyAsset(
                        regulationDto.getRegulationId(),
                        regulationDto.getCode(),
                        DateUtil.formatToPattern(regulationDto.getStartTime(), DateUtil.HOUR_DATE_FORMAT),
                        regulationDto.isType(),
                        regulationDto.getAuctionFormalityId(),
                        regulationDto.getAuctionFormalityName(),
                        regulationDto.getAuctionMethodId(),
                        regulationDto.getAuctionMethodName(),
                        regulationDto.getAssetId(),
                        regulationDto.getStatusRefund(),
                        regulationDto.getAuctionStatus(),
                        regulationDto.getStatusDeposit(),
                        regulationDto.isStatusRefuseWin(),
                        regulationDto.isStatusJoined(),
                        regulationDto.isStatusEnding(),
                        regulationDto.getSignature(),
                        regulationDto.getStatusRetract(),
                        regulationDto.getRegulationAuctionStatus(),
                        regulationDto.getAssetStatus(),
                        regulationDto.getAuctionId(),
                        regulationDto.getWinnerId(),
                        propertySaleHistoryDto.isType() ? getStatusUser(regulationDto) : "",
                        propertySaleHistoryDto.isType() ? getStatusPayment(regulationDto) : ""
                ));
            }

            Date date = new Date();
            myAuctionHistoryList.add(new MyAuctionHistory(
                    pageNumber + i++,
                    propertySaleHistoryDto.getAuctionId(),
                    DateUtil.formatToPattern(propertySaleHistoryDto.getAuctionDate() == null ? date : propertySaleHistoryDto.getAuctionDate(), DateUtil.HOUR_DATE_FORMAT),
                    propertySaleHistoryDto.getAssetId(),
                    propertySaleHistoryDto.getAssetName(),
                    propertySaleHistoryDto.getAuctionStatus(),
                    propertySaleHistoryDto.getRegulationCode(),
                    propertySaleHistoryDto.getAuctionFormalityName(),
                    propertySaleHistoryDto.getAuctionMethodName(),
                    propertySaleHistoryDto.isType(),
                    myAssetList,
                    propertySaleHistoryDto.getRegulationId(),
                    propertySaleHistoryDto.getAssetStatus(),
                    propertySaleHistoryDto.getPaymentStatus(),
                    propertySaleHistoryDto.isAssetManagementStatusEnding(),
                    propertySaleHistoryDto.getWinnerId(),
                    getNoteUser(propertySaleHistoryDto)
            ));
        }
        return StringUtil.toJson(myAuctionHistoryList);
    }

    public void signatureFileBill() {
        if (regulationId == null ||assetId == null) {
            facesNoticeController.addErrorMessage("Hiện tại biên bản đấu giá chưa được cập nhật. Bạn vui lòng quay lại sau.");
            return;
        }
        regulationReportFile = regulationReportFileRepository.findByRegulationId(regulationId);
        if (regulationReportFile == null) {
            facesNoticeController.addErrorMessage("File biên bản chưa được cập nhật. Bạn vui lòng quay lại sau");
            return;
        }
        if (regulationReportFile.getFilePath() == null) {
            facesNoticeController.addErrorMessage("File biên bản chưa được cập nhật. Bạn vui lòng quay lại sau");
            return;
        }

        regulationReportUser = regulationReportUserRepository.findRegulationReportUser(regulationId, assetId, authorizationFEController.getAccountDto().getAccountId());

        if (regulationReportUser != null) {
            try {
                pdfSignatureInfoController.setFilePath(regulationReportUser.getFilePath());
                pdfSignatureInfoController.setFileName(regulationReportUser.getFileName());
                InputStream is = FileUtil.getDownloadFileFromDatabase(regulationReportUser.getFilePath()).getStream();
                List<PdfSignatureInfoDto> info = pdfSignatureInfoController.getPdfSignatureInfoDto(is);
                if (info.size() != 0) {
                    pdfSignatureInfoController.setInfo(info);
                    renderZone = SIGNATURE_SIGNED;
                } else {
                    pdfSignatureInfoController.setInfo(new ArrayList<>());
                    renderZone = SIGNATURE_NOT_SIGN;
                }
            } catch (Exception e) {
                log.error(e);
                setErrorForm("Có lỗi đã xảy ra");
                return;
            }
        } else {
            regulationReportUser = new RegulationReportUser();
            pdfSignatureInfoController.resetAll();
            pdfSignatureInfoController.setFilePath(null);
            pdfSignatureInfoController.setFileName(null);
            renderZone = SIGNATURE_DEFAULT;
        }
        facesNoticeController.openModal("signReportPopup");
    }

    public void onSaveFileRegulationReport() {
        if (pdfSignatureInfoController.getFilePath() == null) {
            facesNoticeController.addErrorMessage("Bạn vui lòng chọn file biên bản đã đăng ký");
            return;
        }

        if (regulationReportUser.getRegulationReportUserId() == null) {
            regulationReportFile.setPeopleSigned(regulationReportFile.getPeopleSigned() + 1);
            regulationReportFileRepository.save(regulationReportFile);
        }

        regulationReportUser.setAssetId(assetId);
        regulationReportUser.setRegulationReportFileId(regulationReportFile.getRegulationReportFileId());
        regulationReportUser.setFilePath(pdfSignatureInfoController.getFilePath());
        regulationReportUser.setFileName(pdfSignatureInfoController.getFileName());
        regulationReportUser.setStatus(DbConstant.REGULATION_REPORT_USER_SIGNED);
        regulationReportUser.setCreateBy(authorizationFEController.getAccountDto().getAccountId());
        regulationReportUser.setUpdateBy(authorizationFEController.getAccountDto().getAccountId());
        regulationReportUserRepository.save(regulationReportUser);

        facesNoticeController.closeModal("signReportPopup");
        facesNoticeController.addSuccessMessage("Gửi file biên bản thành công");

        onSearch();
    }

    public void renderBySizeInfo() {
        if (pdfSignatureInfoController.getInfo().size() != 0) {
            renderZone = SIGNATURE_SIGNED;
        } else {
            renderZone = SIGNATURE_NOT_SIGN;
        }
    }
    
    public String getNoteUser(PropertySaleHistoryDto dto) {
        if (dto.isType()) {
            if (dto.getAssetStatus() == DbConstant.ASSET_STATUS_CANCELED) {
                return "Tạm dừng đấu giá";
            }
            if (dto.getAssetStatus() == DbConstant.ASSET_STATUS_ENDED && dto.isAssetManagementStatusEndingAll()) {
                return "Đấu giá thành công";
            }
            if (dto.getAssetStatus() == DbConstant.ASSET_STATUS_NOT_SUCCESS || (dto.getAssetStatus() == DbConstant.ASSET_STATUS_ENDED && !dto.isAssetManagementStatusEndingAll())) {
                return "Đấu giá không thành";
            }
        } else {
            if (dto.getAuctionStatus() == DbConstant.AUCTION_REQ_STATUS_NO_PROCESS_ID) {
                return "Chưa xử lý";
            } else if (dto.getAuctionStatus() == DbConstant.AUCTION_REQ_STATUS_PROCESSED_ID) {
                return "Đang xử lý";
            } else if (dto.getAuctionStatus() == DbConstant.AUCTION_REQ_STATUS_CONTRACT_ID) {
                return "Ký hợp đồng";
            } else if (dto.getAuctionStatus() == DbConstant.AUCTION_REQ_STATUS_NO_CONTRACT_ID) {
                return "Không ký hợp đồng";
            }
        }
        return "";
    }
    
    public String getStatusPayment(RegulationDto dto) {
        String str;
        switch (dto.getStatusRefund()) {
            case DbConstant.AUCTION_REGISTER_STATUS_REFUND_ZERO:
                str = "Chưa nộp tiền đặt trước";
                break;
            case DbConstant.AUCTION_REGISTER_STATUS_REFUND_ONE:
                str = "Chưa hoàn tiền đặt trước";
                break;
            case DbConstant.AUCTION_REGISTER_STATUS_REFUND_TWO:
                str = "Đã hoàn tiền đặt trước";
                break;
            case DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE:
                str = "Đã nộp tiền đặt trước";
                break;
            case DbConstant.AUCTION_REGISTER_STATUS_REFUND_FOUR:
                str = "Chờ đối soát";
                break;
            default:
                str = "";
        }
        return str;
    }

    public String getStatusUser(RegulationDto dto) {
        List<String> str = new ArrayList<>();
        // Bị truất quyền
        if (dto.getStatusDeposit() == DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_REFUSE
                && !dto.getStatusRetract()
                && (dto.getFilePathDeposit() != null || dto.isStatusJoined())) {
            str.add("Bị truất quyền" + getCommonController().statusDeposit(dto.getFilePathDeposit() != null, dto.getStatusRetract()));
        }
        // Từ chối bởi người dùng
        if (dto.getAuctionStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED_JOIN) {
            str.add("Từ chối tham gia đấu giá");
        }
        // Từ chối bởi đấu giá viên
        if (dto.getAuctionStatus() == DbConstant.AUCTION_REGISTER_STATUS_REJECTED) {
            str.add("Đăng ký không hợp lệ");
        }
        // Không tham gia
        if (!dto.isStatusJoined() && dto.getAuctionStatus() == DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED) {
            if (dto.getAssetStatus() == DbConstant.ASSET_STATUS_ENDED
                    || dto.getAssetStatus() == DbConstant.ASSET_STATUS_NOT_SUCCESS
                    || (dto.getAssetStatus() == DbConstant.ASSET_STATUS_CANCELED && dto.isAssetCancelPlaying())) {
                str.add("Không tham gia đấu giá");
            }
        }
        // Từ chối trúng đấu giá
        if (dto.isStatusRefuseWin()) {
            str.add("Từ chối trúng đấu giá");
        }
        // Tài khoản thắng cuộc
        if (dto.getAssetStatus() == DbConstant.ASSET_STATUS_ENDED &&
                !dto.isStatusRefuseWin() && dto.isStatusEnding() &&
                dto.getAuctionId().equals(dto.getWinnerId())) {
            str.add("Trúng đấu giá");
        }
        // Rút lại giá đã trả
        if (dto.getStatusRetract()) {
            str.add("Rút lại giá đã trả");
        }
        if (str.size() <= 0 && dto.isStatusJoined()) {
            str.add("Tham gia đấu giá");
        }
        return String.join(", ", str);
    }

    @Override
    protected String getMenuId() {
        return Constant.ID_PROPERTY_SALE_HISTORY;
    }
}
