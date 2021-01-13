package vn.compedia.website.auction.controller.admin.manage_auction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.selectonemenu.SelectOneMenu;
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
import vn.compedia.website.auction.controller.admin.export.ExportExcelController;
import vn.compedia.website.auction.dto.assign_auction.RunTheAuctionDto;
import vn.compedia.website.auction.dto.assign_auction.RunTheAuctionSearchDto;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.system.task.AuctionTask;
import vn.compedia.website.auction.system.task.helper.RedisHelper;
import vn.compedia.website.auction.system.util.SysConstant;
import vn.compedia.website.auction.util.*;

import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Log4j2
@Named
@Scope(value = "session")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuctionManageController extends BaseController {

    @Inject
    private UploadSingleFileController uploadSingleFileController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ExportExcelController exportExcelController;
    @Inject
    private AuctionTask auctionTask;
    @Inject
    private DetailAuctionManageController detailAuctionManageController;
    @Inject
    private  ActionSystemController actionSystemController;
    @Autowired
    private RunTheAuctionRepository runTheAuctionRepository;
    @Autowired
    private RegulationFileRepository regulationFileRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private AuctionFormalityRepository auctionFormalityRepository;
    @Autowired
    private AuctionMethodRepository auctionMethodRepository;
    @Autowired
    private AccountRepository accountRepository;

    //lazydata
    private LazyDataModel<RunTheAuctionDto> lazyDataModel;
    private List<AuctionFormality> auctionFormalityList;
    private List<SelectItem> listAuctionForm;
    private List<SelectItem> listAuctionMethod;
    //Dto
    private RunTheAuctionSearchDto runTheAuctionSearchDto;
    private RunTheAuctionDto runTheAuctionDto;
    private LazyDataModel<AssetDto> assetDtoList;
    private RegulationDto regulationDto;
    //file
    private RegulationFile regulationFile;
    //model
    private Regulation regulation;
    private Asset asset;
    private AssetManagement assetManagement;
    //status
    private boolean disableReason;
    private boolean depositionReasonStatus;
    private boolean reasonCancelAssetStatus;
    private boolean showPanel;
    //time
    private Date nowday;
    private String name;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        name = null;
        showPanel = false;
        asset = new Asset();
        disableReason = false;
        depositionReasonStatus = false;
        reasonCancelAssetStatus = false;
        assetManagement = new AssetManagement();
        runTheAuctionDto = new RunTheAuctionDto();
        runTheAuctionSearchDto = new RunTheAuctionSearchDto();
        uploadSingleFileController.resetAll(null, null);
        listAuctionForm = new ArrayList<>();
        listAuctionMethod = new ArrayList<>();
        auctionFormalityList = new ArrayList<>();
        auctionFormalityList = (List<AuctionFormality>) auctionFormalityRepository.getAuctionFormalityByAuctionFormalityId  ();
        for (AuctionFormality dto : auctionFormalityList) {
            listAuctionForm.add(new SelectItem(dto.getAuctionFormalityId(), dto.getName()));
        }
        List<AuctionMethod> auctionMethod = auctionMethodRepository.getAuctionMethodByAuctionMethodId();
        for (AuctionMethod dto : auctionMethod) {
            listAuctionMethod.add(new SelectItem(dto.getAuctionMethodId(), dto.getName()));
        }
        actionSystemController.resetAll();
        onSearch();
    }

    public void onSearch() {
        runTheAuctionSearchDto.setAuctioneerId(authorizationController.getAccountDto().getAccountId());
        lazyDataModel = new LazyDataModel<RunTheAuctionDto>() {

            @Override
            public List<RunTheAuctionDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                runTheAuctionSearchDto.setPageIndex(first);
                runTheAuctionSearchDto.setPageSize(pageSize);
                runTheAuctionSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                runTheAuctionSearchDto.setSortOrder(sort);
                return runTheAuctionRepository.search(runTheAuctionSearchDto);
            }

            @Override
            public RunTheAuctionDto getRowData(String rowKey) {
                List<RunTheAuctionDto> RunTheAuctionList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (RunTheAuctionDto obj : RunTheAuctionList) {
                    if (obj.getCode().equals(value)) {
                        return obj;
                    }

                }
                return null;
            }
        };
        int count = runTheAuctionRepository.countSearch(runTheAuctionSearchDto).intValue();
        lazyDataModel.setRowCount(count);
    }

    public void onRedirectToAsset(RunTheAuctionDto dto) {
        detailAuctionManageController.prepareData(dto.getRegulationId());
        FacesUtil.redirect("/admin/dieu-hanh-dau-gia/chi-tiet-dieu-hanh-phien-dau-gia.xhtml");
    }

    public void showUpdatePopup(Regulation obj) {
        disableReason = false;
        regulationFile = new RegulationFile();
        regulation = new Regulation();
        BeanUtils.copyProperties(obj, regulation);
        uploadSingleFileController.resetAll(null, null);
    }

    public void validateReason() {
        if (regulation.getStatus() != DbConstant.DA_HUY_QUY_CHE) {
            if (StringUtils.isBlank(regulation.getReasonCancel())) {
                FacesUtil.addErrorMessage("Lý do tạm dừng là trường bắt buộc");
                return;
            }
            if (StringUtils.isBlank(uploadSingleFileController.getFilePath())) {
                FacesUtil.addErrorMessage("File văn bản lý do tạm dừng là trường bắt buộc");
                return;
            }
            Regulation oldRegulation = runTheAuctionRepository.findById(regulation.getRegulationId()).orElse(null);
            if (oldRegulation == null) {
                setErrorForm("Quy chế không tồn tại");
                return;
            }
            FacesUtil.showDialog("confirm");
        }
    }

    public void onSaveData() {
        Regulation oldRegulation = runTheAuctionRepository.findById(regulation.getRegulationId()).orElse(null);
        if (oldRegulation == null) {
            return;
        }
        oldRegulation.setReasonCancel(regulation.getReasonCancel());
        oldRegulation.setAuctionStatus(DbConstant.REGULATION_AUCTION_STATUS_CANCELLED);
        oldRegulation.setStatus(DbConstant.REGULATION_STATUS_CANCELED);
        List<Asset> oldAsset = assetRepository.findAssetsByRegulationIdAndStatusList(oldRegulation.getRegulationId(), new int[] { DbConstant.ASSET_STATUS_WAITING, DbConstant.ASSET_STATUS_PLAYING });
        for (Asset asset : oldAsset) {
            assetRepository.changeStatus(asset.getAssetId(), DbConstant.ASSET_STATUS_CANCELED);
        }
        List<Account> accountList = accountRepository.findAccountsByRegulationId(regulation.getRegulationId());
        for (Account account : accountList) {
            if (account.getAccountId() != null) {
                EmailUtil.getInstance().sendReasonCancelRegulation(
                        account.getEmail(),
                        FacesUtil.getRealPath(FileUtil.getFilePathFromDatabase(uploadSingleFileController.getFilePath())),
                        uploadSingleFileController.getFileName(),
                        account.getFullName(),
                        oldRegulation.getCode(),
                        oldRegulation.getReasonCancel());
            }
        }
        runTheAuctionRepository.save(oldRegulation);
        regulationFile.setFileName(uploadSingleFileController.getFileName());
        regulationFile.setFilePath(uploadSingleFileController.getFilePath());
        regulationFile.setType(DbConstant.REGULATION_FILE_TYPE_HUY_BO);
        regulationFile.setRegulationId(oldRegulation.getRegulationId());
        regulationFileRepository.save(regulationFile);
        actionSystemController.onSave("Tạm dừng đấu giá quy chế \"" + oldRegulation.getCode() + "\"", authorizationController.getAccountDto().getAccountId());
        FacesUtil.addSuccessMessage("Tạm dừng đấu giá thành công");
        FacesUtil.closeDialog("dialogReasonCancel");
        initData();

        // stop regulation if auction playing
        auctionTask.cancelRegulation(oldRegulation.getRegulationId());
        RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_CANCEL_ASSET, oldRegulation.getRegulationId());
    }

    public void showReasonPopup(RunTheAuctionDto obj) {
        disableReason = true;
        regulation = runTheAuctionRepository.findById(obj.getRegulationId()).orElse(null);
        BeanUtils.copyProperties(regulation, runTheAuctionDto);
        regulationFile = regulationFileRepository.findRegulationFileByRegulationIdAndType(obj.getRegulationId(), DbConstant.REGULATION_FILE_TYPE_HUY_BO);
        runTheAuctionDto.setReasonFilePath(regulationFile.getFilePath());
    }

    public StreamedContent downloadReasonFile() {
        return getFileDownload(runTheAuctionDto.getReasonFilePath());
    }

    public void onSelectDate() {
        if (null != this.getRunTheAuctionSearchDto().getTimeStart() && null != this.getRunTheAuctionSearchDto().getTimeEnd()
                && this.getRunTheAuctionSearchDto().getTimeStart().compareTo(this.getRunTheAuctionSearchDto().getTimeEnd()) > 0) {
            this.getRunTheAuctionSearchDto().setTimeEnd(null);
            FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, "Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
        }
    }

    public StreamedContent downloadHistoryBargain(RunTheAuctionDto dto) {
        try {
            if (dto.getRegulationId() == null) {
                return null;
            }
            if (dto.getAuctionMethodId().intValue() == DbConstant.AUCTION_METHOD_ID_UP && dto.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_DIRECT) {
                exportExcelController.setRegulationId(dto.getRegulationId());
                actionSystemController().onSave("Xuất biên bản đấu giá của quy chế :" + dto.getCode(),authorizationController.getAccountDto().getAccountId());
                return exportExcelController.downloadFileDirectPayment(dto.getAuctionStatus());
            }
            if (dto.getAuctionMethodId().intValue() == DbConstant.AUCTION_METHOD_ID_UP && dto.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_POLL) {
                exportExcelController.setRegulationId(dto.getRegulationId());
                actionSystemController().onSave("Xuất biên bản đấu giá của quy chế :" + dto.getCode(),authorizationController.getAccountDto().getAccountId());
                return exportExcelController.downloadFileVote(dto.getAuctionStatus());
            }
            if (dto.getAuctionMethodId().intValue() == DbConstant.AUCTION_METHOD_ID_DOWN) {
                FacesUtil.addErrorMessage("Chưa có template file xuất lịch sử cho phương thức đặt giá xuống");
                return null;
            }
            return null;
        } catch (Exception e) {
            log.error(e);
            setErrorForm("Không tồn tại dữ liệu");
            return null;
        }
    }

    @Override
    protected EScope getMenuId() {
        return EScope.AUCTION_MANAGE;
    }
}
