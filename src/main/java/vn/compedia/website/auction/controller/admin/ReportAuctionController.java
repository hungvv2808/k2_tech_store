package vn.compedia.website.auction.controller.admin;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.common.UploadSingleFileRegulationController;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.dto.regulation.RegulationSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.Regulation;
import vn.compedia.website.auction.model.RegulationReportFile;
import vn.compedia.website.auction.repository.AssetRepository;
import vn.compedia.website.auction.repository.RegulationReportFileRepository;
import vn.compedia.website.auction.repository.RegulationRepository;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.util.FileUtil;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Named
@Scope(value = "session")
public class ReportAuctionController extends BaseController {

    @Inject
    private UploadSingleFileRegulationController uploadSingleFileRegulationController;

    @Autowired
    private RegulationRepository regulationRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private RegulationReportFileRepository regulationReportFileRepository;

    private RegulationSearchDto searchDto;
    private LazyDataModel<RegulationDto> lazyDataModel;
    private List<SelectItem> regulationSelectItemList;
    private RegulationDto regulationDto;
    private List<AssetDto> assetDtoList;
    private List<AssetDto> assetDtoSelected;
    private RegulationReportFile regulationReportFile;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        regulationDto = new RegulationDto();
        searchDto = new RegulationSearchDto();
        assetDtoList = new ArrayList<>();
        regulationSelectItemList = new ArrayList<>();
        List<Regulation> regulationList = regulationRepository.findAllByAuctionStatusOrderByRegulationId(DbConstant.REGULATION_AUCTION_STATUS_ENDED, DbConstant.REGULATION_AUCTION_STATUS_CANCELLED);
        for (Regulation regulation : regulationList) {
            regulationSelectItemList.add(new SelectItem(regulation.getRegulationId(), regulation.getCode()));
        }
        lazyDataModel = new LazyDataModel<RegulationDto>() {
            @Override
            public List<RegulationDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
        uploadSingleFileRegulationController.resetAll(null);
        searchDto.setAuctionStatusEnded(DbConstant.REGULATION_AUCTION_STATUS_ENDED);
        searchDto.setAuctionStatusCanceled(DbConstant.REGULATION_AUCTION_STATUS_CANCELLED);
        onSearch();
    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm","tblSearchResult");
        lazyDataModel = new LazyDataModel<RegulationDto>() {
            @Override
            public List<RegulationDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
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
                return regulationRepository.searchForReport(searchDto, DbConstant.REGULATION_FILE_TYPE_BIEN_BAN);
            }

            @Override
            public RegulationDto getRowData(String rowKey) {
                List<RegulationDto> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (RegulationDto obj : listNhatKy) {
                    if (obj.getRegulationId().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = regulationRepository.countSearchForReport(searchDto, DbConstant.REGULATION_FILE_TYPE_BIEN_BAN).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }

    public void viewDetailReportAuction(RegulationDto dto) {
        BeanUtils.copyProperties(dto, regulationDto);
        if (regulationDto == null) {
            setErrorForm("Quy chế không tồn tại");
            return;
        }
        regulationReportFile = regulationReportFileRepository.findByRegulationId(regulationDto.getRegulationId());
        if (regulationReportFile == null) {
            setErrorForm("Không tồn tại dữ liệu");
            return;
        }
        assetDtoList = assetRepository.searchForReportAuction(regulationDto.getRegulationId());
        if (CollectionUtils.isEmpty(assetDtoList)) {
            setErrorForm("Không tồn tại dữ liệu");
            return;
        }
        uploadSingleFileRegulationController.setFileName(regulationReportFile.getFileName());
        uploadSingleFileRegulationController.setFilePath(regulationReportFile.getFilePath());

        assetDtoSelected = new ArrayList<>();

        for (AssetDto assetDto:assetDtoList) {
            if (assetDto.isAssetManagementEnding() == DbConstant.ASSET_MANAGEMENT_ENDING_GOOD) {
                assetDtoSelected.add(assetDto);
            }
        }

        FacesUtil.redirect("/admin/bien-ban-dau-gia/chi-tiet-bien-ban-dau-gia.xhtml");
    }

    public void backToReportAuction() {
        FacesUtil.redirect("/admin/bien-ban-dau-gia/luu-tru-bien-ban-dau-gia.xhtml");
    }

    public void endReportAuction() {
        if (regulationReportFile.getTotalWinner() == null || regulationReportFile.getPeopleSigned() == null) {
            setErrorForm("Bạn vui lòng gửi biên bản đấu giá để thực hiện lấy chữ ký của người thắng cuộc trước khi bấm nút hoàn tất");
            return;
        }
        if (regulationReportFile.getTotalWinner().intValue() == regulationReportFile.getPeopleSigned().intValue()) {
            regulationReportFile.setStatus(DbConstant.REGULATION_REPORT_FILE_STATUS_FINISH);
            regulationReportFileRepository.save(regulationReportFile);
            setSuccessForm("Hoàn tất việc lấy chứ ký những người trúng đấu giá");
        } else {
            setErrorForm("Hiện tại có người trúng đấu giá chưa hoàn thành việc ký vào biên bản đấu giá nên chưa thể đóng hồ sơ. Bạn vui thao tác lại sau khi tất cả người trúng đấu giá đã ký vào biên bản");
            return;
        }
    }

    public boolean disableCheckBox(AssetDto dto) {
        if (dto.getStatus() == DbConstant.ASSET_STATUS_ENDED && dto.isAssetManagementEnding() == DbConstant.ASSET_MANAGEMENT_ENDING_GOOD) {
            return false;
        }
        return true;
    }

    public void checkValidate() {
        if(StringUtils.isBlank(uploadSingleFileRegulationController.getFileName()) && StringUtils.isBlank(uploadSingleFileRegulationController.getFilePath())) {
            setErrorForm("Bạn vui lòng chọn file biên bản đấu giá");
            return;
        }
        if (regulationDto == null) {
            setErrorForm("Quy chế không tồn tại");
            return;
        }
        int i = 0;
        for (AssetDto assetDto:assetDtoList) {
            if (assetDto.isAssetManagementEnding() == DbConstant.ASSET_MANAGEMENT_ENDING_GOOD) {
                i++;
            }
        }
        if (CollectionUtils.isEmpty(assetDtoSelected)) {
            setErrorForm("Bạn vui lòng chọn danh sách những tài sản có người trúng đấu giá để thực hiện gửi biên bản");
            return;
        } else {
            if (assetDtoSelected.size() != i) {
                setErrorForm("Biên bản cuộc đấu giá cần phải được gửi đến tất cả người trúng đấu giá để thực hiện lấy chữ ký");
                return;
            }
        }
        FacesUtil.showDialog("confirmDialog");
    }

    public void sendReportAuction() {
        regulationReportFile.setFileName(uploadSingleFileRegulationController.getFileName());
        regulationReportFile.setFilePath(uploadSingleFileRegulationController.getFilePath());
        regulationReportFile.setTotalWinner(assetDtoSelected.size());
        regulationReportFile.setPeopleSigned(DbConstant.REGULATION_REPORT_FILE_PEOPLE_SIGNED);
        regulationReportFile.setStatus(DbConstant.REGULATION_REPORT_FILE_STATUS_PROCESSING);
        regulationReportFile.setCreateBy(authorizationController.getAccountDto().getAccountId());
        regulationReportFile.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        regulationReportFileRepository.save(regulationReportFile);
        setSuccessForm("Gửi biên bản thành công");
        FacesUtil.closeDialog("confirmDialog");
    }

    public boolean renderedStatusFile(AssetDto assetDto) {
        if ((assetDto.getStatus() == DbConstant.ASSET_STATUS_ENDED && assetDto.isAssetManagementEnding() == DbConstant.ASSET_MANAGEMENT_ENDING_BAD)
                || assetDto.getStatusSigned() == null) {
            return true;
        }
        return false;
    }

    public StreamedContent getFileDownload(String fileName) {
        StreamedContent streamedContent = FileUtil.getDownloadFileFromDatabase(fileName);
        if (streamedContent == null) {
            FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, "File không tồn tại");
        }
        return streamedContent;
    }

    public void onDateSelect() {
        if (null != searchDto.getFromDate() && null != searchDto.getToDate()
                && searchDto.getFromDate().compareTo(searchDto.getToDate()) > 0) {
            searchDto.setToDate(null);
            setErrorForm("Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
        }
    }

    @Override
    protected EScope getMenuId() {
        return EScope.PUBLIC;
    }
}
