package vn.compedia.website.auction.controller.admin.manage_auction;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.controller.admin.export.ExportExcelController;
import vn.compedia.website.auction.controller.admin.regulation.RegulationController;
import vn.compedia.website.auction.controller.frontend.common.FacesNoticeController;
import vn.compedia.website.auction.dto.assign_auction.AssignAuctionDto;
import vn.compedia.website.auction.dto.assign_auction.AssignAuctionSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.Regulation;
import vn.compedia.website.auction.model.RegulationFile;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Log4j2
@Named
@Scope(value="session")
@Getter
@Setter
public class AssignAuctionController extends BaseController {
    @Inject
    private ExportExcelController exportExcelController;
    @Inject
    private FacesNoticeController facesNoticeController;
    @Inject
    private RegulationController regulationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private AuctionManageController auctionManageController;
    @Inject
    private DetailAuctionManageController detailAuctionManageController;
    @Autowired
    private AssignAuctionRepository assignAuctionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuctionReqRepository auctionReqRepository;
    @Autowired
    private FunctionRepository functionRepository;
    @Autowired
    private RunTheAuctionRepository runTheAuctionRepository;
    @Autowired
    private RegulationFileRepository regulationFileRepository;


    private List<SelectItem> accountList;
    private Regulation regulation;
    private RegulationFile regulationFile;
    private AssignAuctionDto assignAuctionDto;
    private LazyDataModel<AssignAuctionDto> lazyDataModel;
    private AssignAuctionSearchDto assignAuctionSearchDto;
    private List<AssignAuctionController> assignAuctionDtoList;
    private Long auctioneerId;
    private String auctioneerRequestId = null;
    private String status;
    private String auctionStatus;
    private String auctioneerName;
    private int auctioneerCount;
    private String regulationFilePath;
    private boolean disableReason = false;

    public void initData() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            init();
            auctioneerRequestId = FacesUtil.getRequestParameter("auctioneerid");
            status = FacesUtil.getRequestParameter("status");
            auctionStatus = FacesUtil.getRequestParameter("auctionstatus");
            resetAll();
       }
    }

    public void resetAll() {
        regulationFile = new RegulationFile();
        regulation = new Regulation();
        assignAuctionDto = new AssignAuctionDto();
        assignAuctionSearchDto = new AssignAuctionSearchDto();
        actionSystemController.resetAll();
        loadAuctioneerList();
        onSearch();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm","tblSearchResult");
        assignAuctionSearchDto.setAccountStatus(DbConstant.ACCOUNT_ACTIVE_STATUS);

        assignAuctionSearchDto.setRegulationStatus(Arrays.asList(
                DbConstant.REGULATION_STATUS_PUBLIC,
                DbConstant.REGULATION_STATUS_CANCELED
        ));
        if (auctioneerRequestId != null) {
            assignAuctionSearchDto.setNullAuctioneerId(auctioneerRequestId);
            if (StringUtils.isNotBlank(auctionStatus)) {
                assignAuctionSearchDto.setAuctionStatus(Integer.parseInt(auctionStatus));
                auctionStatus = "";
            }
        }

        lazyDataModel = new LazyDataModel<AssignAuctionDto>() {
            @Override
            public List<AssignAuctionDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                assignAuctionSearchDto.setPageIndex(first);
                assignAuctionSearchDto.setPageSize(pageSize);
                assignAuctionSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                assignAuctionSearchDto.setSortOrder(sort);
                return assignAuctionRepository.search(assignAuctionSearchDto);
            }

            @Override
            public AssignAuctionDto getRowData(String rowKey) {
                List<AssignAuctionDto> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (AssignAuctionDto obj : listNhatKy) {
                    if (obj.getRegulationId().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = assignAuctionRepository.countSearch(assignAuctionSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("growl");
        FacesUtil.updateView("searchForm");
    }

    public void onDateSelect() {
        if (null !=  this.assignAuctionSearchDto.getFromDate() && null != this.assignAuctionSearchDto.getToDate()
                && this.assignAuctionSearchDto.getFromDate().compareTo(this.assignAuctionSearchDto.getToDate()) > 0) {
            this.getAssignAuctionSearchDto().setToDate(null);
            setErrorForm("Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
            FacesUtil.updateView("growl");
        }
    }

    public StreamedContent downloadHistoryBargain(AssignAuctionDto dto) {
        try {
            if (dto.getRegulationId() == null) {
                return null;
            }
            if (dto.getAuctionMethodId().intValue() == DbConstant.AUCTION_METHOD_ID_UP && dto.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_DIRECT) {
                exportExcelController.setRegulationId(dto.getRegulationId());
                actionSystemController().onSave("Xuất biên bản đấu giá của quy chế :" + dto.getCode(), authorizationController.getAccountDto().getAccountId());
                return exportExcelController.downloadFileDirectPayment(dto.getAuctionStatus());
            }
            if (dto.getAuctionMethodId().intValue() == DbConstant.AUCTION_METHOD_ID_UP && dto.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_POLL) {
                exportExcelController.setRegulationId(dto.getRegulationId());
                actionSystemController().onSave("Xuất biên bản đấu giá của quy chế :" + dto.getCode(), authorizationController.getAccountDto().getAccountId());
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

    public void showUpdatePopup(AssignAuctionDto obj) {
        assignAuctionDto = obj;
        auctioneerId = assignAuctionDto.getAuctioneerId();
        loadAuctioneerList();
    }

    public void checkAuctioneer(){
        auctioneerName = null;
        auctioneerCount = 0;
        if (auctioneerId == null) {
            setErrorForm("Bạn phải chọn đấu giá viên cho buổi đấu giá " + assignAuctionDto.getCode());
            return;
        }
        Account auctioneer = accountRepository.findAccountByAccountId(auctioneerId);
        if (auctioneer != null) {
            auctioneerName = auctioneer.getFullName();
        }
        List<Regulation> assignRegulationList = assignAuctionRepository.findAllByAuctioneerIdAndAuctionStatusInAndStartTimeIsBetween(
                auctioneerId,
                Arrays.asList(DbConstant.REGULATION_AUCTION_STATUS_WAITING, DbConstant.REGULATION_AUCTION_STATUS_PLAYING),
                DateUtil.formatFromDate(assignAuctionDto.getStartTime()),
                DateUtil.formatToDate(assignAuctionDto.getStartTime())
        );
        if (!assignRegulationList.isEmpty()) {
            auctioneerCount = assignRegulationList.size();
            PrimeFaces current = PrimeFaces.current();
            current.executeScript("PF('dialogWarn').show();");
            return;
        }
        onSave();
    }

    public void onSave() {

        regulation = assignAuctionRepository.findById(assignAuctionDto.getRegulationId()).orElse(null);
        if (regulation == null) {
            setErrorForm("Phiên đấu giá không tồn tại");
            return;
        }
        if(regulation.getAuctioneerId() == null) {
            actionSystemController.onSave("Phân công đấu giá viên " + auctioneerName + " cho quy chế " + regulation.getCode(), authorizationController.getAccountDto().getAccountId());
        }
        else {
            actionSystemController.onSave("Cập nhật đấu giá viên cho quy chế " + regulation.getCode(), authorizationController.getAccountDto().getAccountId());
        }
            regulation.setAuctioneerId(auctioneerId);
            regulation.setUpdateBy(authorizationController.getAccountDto().getAccountId());
            assignAuctionRepository.save(regulation);
            FacesUtil.addSuccessMessage("Lưu thành công");
            FacesUtil.updateView("searchForm");

    }

    private void loadAuctioneerList() {
        accountList = new ArrayList<>();
        List<Integer> roleIds = functionRepository.findRoleIdsFromScopes(EScope.AUCTION_MANAGE.toString());
        List<Account> account = accountRepository.findAccountByRoleIdInOrderByFullName(roleIds);
        for (Account ac : account) {
            if(ac.getAccountStatus() == DbConstant.ACCOUNT_ACTIVE_STATUS){
                accountList.add(new SelectItem(ac.getAccountId(), ac.getFullName()));
            }
        }
    }

    public void onRedirectListAsset(Long regulationId) {
        detailAuctionManageController.prepareData(regulationId);
        FacesUtil.redirect("/admin/dieu-hanh-dau-gia/chi-tiet-phien-dau-gia.xhtml");
    }

    public void showReasonPopup(Long obj) {
        disableReason = true;
        regulation = runTheAuctionRepository.findById(obj).orElse(null);
        regulationFile = regulationFileRepository.findRegulationFileByRegulationIdAndType(obj, DbConstant.REGULATION_FILE_TYPE_HUY_BO);
    }

    public StreamedContent downloadReasonFile() {
        return getFileDownload(regulationFile.getFilePath());
    }

    public boolean renderCountString(String assetName) {
        if (assetName.length() < 100) {
            return true;
        }
        return false;
    }

    public void onShowRegulationFile(String regulationFilePath) {
        this.regulationFilePath = regulationFilePath;
    }

    @Override
    protected EScope getMenuId() {
        return EScope.ASSIGN_AUCTIONEER;
    }
}
