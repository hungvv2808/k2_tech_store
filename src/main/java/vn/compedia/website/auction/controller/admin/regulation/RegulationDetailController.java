package vn.compedia.website.auction.controller.admin.regulation;

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
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.*;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.dto.regulation.RegulationSearchDto;
import vn.compedia.website.auction.dto.request.AuctionReqDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.service.RegulationService;
import vn.compedia.website.auction.system.socket.SocketServer;
import vn.compedia.website.auction.system.socket.push.NotifyPush;
import vn.compedia.website.auction.system.task.helper.RedisHelper;
import vn.compedia.website.auction.system.util.SysConstant;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.EmailUtil;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.util.FileUtil;

import javax.faces.context.FacesContext;
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
public class RegulationDetailController extends BaseController {
    @Inject
    private UploadSingleFileController uploadSingleFileController;
    @Inject
    private UploadSingleFileRegulationController uploadSingleFileRegulationController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private UploadSingleImageController uploadSingleImageController;
    @Inject
    private UploadMultipleFileController uploadMultipleFileController;
    @Inject
    private UploadMultipleImageController uploadMultipleImageController;
    @Inject
    private RegulationController regulationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private NotifyPush notifyPush;

    @Autowired
    protected RegulationService regulationService;
    @Autowired
    private AuctionFormalityRepository auctionFormalityRepository;
    @Autowired
    private AssetFileRepository assetFileRepository;
    @Autowired
    private AssetImageRepository assetImageRepository;
    @Autowired
    protected AuctionReqRepository auctionReqRepository;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    protected AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    protected RegulationRepository regulationRepository;
    @Autowired
    private RegulationFileRepository regulationFileRepository;
    @Autowired
    private AssetRepository assetRepository;

    private Regulation regulation;
    private RegulationDto regulationDto;
    private RegulationSearchDto regulationSearchDto;
    private LazyDataModel<RegulationDto> lazyDataModel;
    private List<SelectItem> auctionFormalityList;
    private AuctionReqDto auctionReqDto;
    private Long auctionReqId;
    private Long accountId;
    private Long regulationId;
    private boolean autionid;
    private AuctionReq auctionReq;
    private Account account;
    private RegulationFile regulationFile;
    private List<RegulationFile> regulationFileList;
    private List<AssetFile> assetFileList;
    private List<AssetImage> assetImageList;
    private AssetFile assetFile;
    private Asset asset;
    private List<Asset> assetList;
    private int assetIndex;
    private List<AssetDto> assetDtoList;
    private AssetDto assetDto;
    private boolean auctionFormality;
    private RegulationFile fileLyDo;
    private boolean statusDisable;
    private Regulation objBackup;
    private String id;
    private String status;
    private Date dateNow;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            id = FacesUtil.getRequestParameter("id");
            status = FacesUtil.getRequestParameter("status");
            resetAll();
            onSearch();
        }
    }

    public void resetAll() {
        assetList = new ArrayList<>();
        assetFileList = new ArrayList<>();
        assetImageList = new ArrayList<>();
        autionid = false;
        regulation = new Regulation();
        regulationFile = new RegulationFile();
        regulationDto = new RegulationDto();
        regulationSearchDto = new RegulationSearchDto();
        auctionFormalityList = new ArrayList<>();
        regulationFileList = new ArrayList<>();
        regulation = regulationRepository.findRegulationByRegulationId(regulationId);
        uploadSingleFileController.resetAll(null, null);
        uploadSingleFileRegulationController.resetAll(null);
        uploadSingleImageController.resetAll(null);
        uploadMultipleImageController.resetAll(null);
        uploadMultipleFileController.resetAll(null);
        assetIndex = -1;
        dateNow= new Date();
        assetDtoList = new ArrayList<>();
        assetDto = new AssetDto();
        actionSystemController.resetAll();
        checkStartTime();
        onSearch();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void checkStartTime(){
        List<Regulation> checkStartTimeList = regulationRepository.findRegulationsByStatus(0);
        List<Regulation> saveNewAuctionStatus = new ArrayList<>();
        if(checkStartTimeList.size() >0) {
            for (Regulation checkStartTime : checkStartTimeList) {
                if (checkStartTime.getStartTime().compareTo(dateNow) < 0 && checkStartTime.getStatus() == DbConstant.REGULATION_STATUS_NOT_PUBLIC) {
                    checkStartTime.setAuctionStatus(DbConstant.REGULATION_STATUS_NOT_HAPPENED);
                    saveNewAuctionStatus.add(checkStartTime);
                }
            }
        }
        regulationRepository.saveAll(saveNewAuctionStatus);
    }
    public void onSearch() {
        /*regulationSearchDto = new RegulationSearchDto();*/
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        if(id != null){
            regulationSearchDto.setAuctionReqId(Long.valueOf(id));
        }
        if(regulationSearchDto.getStatus() == null) {
            if (status != null){
                regulationSearchDto.setStatus(Integer.valueOf(status));
            }
        } else {
            if (status != null && regulationSearchDto.getStatus() == Integer.valueOf(status)){
                regulationSearchDto.setStatus(Integer.valueOf(status));
            }
        }
        lazyDataModel = new LazyDataModel<RegulationDto>() {
            @Override
            public List<RegulationDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                regulationSearchDto.setPageIndex(first);
                regulationSearchDto.setPageSize(pageSize);
                regulationSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                regulationSearchDto.setSortOrder(sort);
                return regulationRepository.search(regulationSearchDto);

            }
            @Override
            public RegulationDto getRowData(String rowKey) {
                List<RegulationDto> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (RegulationDto obj : listNhatKy) {
                    if (obj.getAuctionReqId().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };

        int count;
        count = regulationRepository.countSearch(regulationSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm:tblSearchResult");
    }

    public void getLydoByRegulationId(Regulation r) {
        if(r.getStatus() == DbConstant.DA_HUY_QUY_CHE){
            statusDisable = true;
        }
        regulationFile = regulationFileRepository.findRegulationFileByRegulationIdAndType(r.getRegulationId(), DbConstant.REGULATION_FILE_TYPE_HUY_BO);
        if (regulationFile == null) {
            setErrorForm("File tạm dừng quy chế không tồn tại");
            FacesUtil.updateView("growl");
            return;
        }
        uploadSingleFileController.setFileName(regulationFile.getFileName());
        uploadSingleFileController.setFilePath(regulationFile.getFilePath());
        regulationDto = new RegulationDto();
        BeanUtils.copyProperties(r,regulationDto);
        FacesUtil.showDialog("dialoglidohuybo");
    }

    public void checkConfirm() {
        if (regulationDto.getStatus() != DbConstant.DA_HUY_QUY_CHE) {
            if (StringUtils.isBlank(regulationDto.getReasonCancel().trim())) {
                setErrorForm("Bạn vui lòng nhập lý do huỷ bỏ");
                return;
            }
            if (uploadSingleFileController.getFilePath() == null) {
                setErrorForm("Bạn vui lòng tải file văn bản");
                return;
            }
            regulation = regulationRepository.findById(regulationDto.getRegulationId()).orElse(null);
            if (regulation == null) {
                setErrorForm("Quy chế không tồn tại");
                return;
            }
            FacesUtil.showDialog("confirm");
        }
    }

    public void onSend() {
        // save regulation
        regulation.setReasonCancel(regulationDto.getReasonCancel());
        regulation.setStatus(DbConstant.REGULATION_STATUS_CANCELED);
        regulation.setAuctionStatus(DbConstant.REGULATION_AUCTION_STATUS_CANCELLED);
        regulationRepository.save(regulation);

        // save regulation file
        regulationFile.setFilePath(uploadSingleFileController.getFilePath());
        regulationFile.setFileName(uploadSingleFileController.getFileName());
        regulationFile.setType(DbConstant.REGULATION_FILE_TYPE_HUY_BO);
        regulationFile.setRegulationId(regulation.getRegulationId());
        regulationFileRepository.save(regulationFile);

        List<Asset> assetList = assetRepository.getAllByRegulationId(regulation.getRegulationId());
        for (Asset asset : assetList) {
            asset.setStatus(DbConstant.ASSET_STATUS_CANCELED);
        }
        assetRepository.saveAll(assetList);
        List<AuctionRegister> auctionRegisters = auctionRegisterRepository.findAuctionRegistersByRegulationId(regulation.getRegulationId());
        for (AuctionRegister ar : auctionRegisters) {
            Account account = accountRepository.findAccountByAccountId(ar.getAccountId());
            if (account.getAccountId() != null) {
                EmailUtil.getInstance().sendReasonCancelRegulation(account.getEmail(), FacesUtil.getRealPath(FileUtil.getFilePathFromDatabase(uploadSingleFileController.getFilePath())), uploadSingleFileController.getFileName(), account.getFullName(), regulation.getCode(), regulation.getReasonCancel());
            }
        }

        actionSystemController.onSave(" Tạm dừng quy chế " + regulation.getCode(), authorizationController.getAccountDto().getAccountId());
        setSuccessForm("Tạm dừng quy chế thành công");
        FacesUtil.closeDialog("dialoglidohuybo");
        resetAll();
        FacesUtil.updateView("searchForm");
        onSearch();
    }

    public void onSave(RegulationDto obj) {
        Regulation regulation = regulationRepository.findById(obj.getRegulationId()).orElse(null);
        if (regulation == null) {
            setErrorForm("Quy chế không tồn tại");
            return;
        }
        regulation.setStatus(DbConstant.REGULATION_STATUS_PUBLIC);
        regulation.setAuctionStatus(DbConstant.REGULATION_AUCTION_STATUS_WAITING);
        regulationRepository.save(regulation);
        actionSystemController.onSave(" Đăng tải quy chế " + regulation.getCode(), authorizationController.getAccountDto().getAccountId());
        setSuccessForm("Đăng tải thành công");
        resetAll();
        FacesUtil.updateView("searchForm");
        onSearch();

        // sync to all clients
        Asset asset = new Asset();
        asset.setAssetId(0L);
        log.info("Sync to {} client", SocketServer.getSESSIONS().size());
        notifyPush.notifyToAll(SysConstant.TYPE_UPDATE_SCOPE, SysConstant.ACTION_UPDATE_SCOPE, asset);
        RedisHelper.syncSocketAll(0L, SysConstant.ACTION_UPDATE_SCOPE);
    }

    public void changeAutionFormalityId() {
        if (regulationDto.getAuctionFormalityId() == 0) {
            autionid = true;
        } else {
            autionid = false;
        }
    }

    public void saveLiDo(Regulation regulation, List<String> fileList) {
        //save regulation
        regulation.setStatus(DbConstant.DA_HUY_QUY_CHE);
        regulation = regulationRepository.save(regulation);
        //build regulation file
        regulationFileList = new ArrayList<>();
        for (String pathFile : fileList) {
            RegulationFile regulationFile = new RegulationFile();
            regulationFile.setRegulationId(regulation.getRegulationId());
            regulationFile.setFilePath(pathFile);
            regulationFile.setType(DbConstant.REGULATION_FILE_TYPE_HUY_BO);
            regulationFileList.add(regulationFile);
        }
        regulationFileRepository.saveAll(regulationFileList);
    }

    public void resetDialog() {
        autionid = false;
        regulation = new Regulation();
        regulationFile = new RegulationFile();
        regulationDto = new RegulationDto();
        auctionFormalityList = new ArrayList<>();
        regulationFileList = new ArrayList<>();
        uploadSingleFileController.resetAll(null, null);
        uploadSingleFileRegulationController.resetAll(null);
        uploadSingleImageController.resetAll(null);
        uploadMultipleImageController.resetAll(null);
    }

    public void transferRegulationId(Long regulationId, Long auctionReqId) {
        regulationController.setDisableCRUD(false);
        regulationController.setRegulationId(regulationId);
        regulationController.setAuctionReqId(auctionReqId);
        FacesUtil.redirect("/admin/quan-ly-dau-gia/thong-tin-quy-che.xhtml");
    }

    public void seeRegulationId(Long regulationId, Long auctionReqId) {
        regulationController.setDisableCRUD(true);
        regulationController.setRegulationId(regulationId);
        regulationController.setAuctionReqId(auctionReqId);
        FacesUtil.redirect("/admin/quan-ly-dau-gia/thong-tin-quy-che.xhtml");
    }

    public void onDelete(RegulationDto obj) {
        try {
            regulationRepository.deleteById(obj.getRegulationId());
            assetList = assetRepository.findAssetsByRegulationId(obj.getRegulationId());
            if(assetList.size() != 0){
                for(Asset as : assetList){
                    assetRepository.deleteById(as.getAssetId());
                    assetFileList = assetFileRepository.findAllByAssetId(as.getAssetId());
                    if(assetFileList.size() != 0) {
                        for (AssetFile af : assetFileList) {
                            assetFileRepository.deleteById(af.getAssetFileId());
                        }
                    }
                    assetImageList = assetImageRepository.findAllByAssetId(as.getAssetId());
                    if(assetImageList.size() !=0){
                        for(AssetImage ai : assetImageList){
                            assetImageRepository.deleteById(ai.getAssetImageId());
                        }
                    }
                }
            }
            regulationFileList = regulationFileRepository.findAllByRegulationId(obj.getRegulationId());
            if(regulationFileList.size() != 0){
                for(RegulationFile rf : regulationFileList){
                    regulationFileRepository.deleteById(rf.getRegulationFileId());
                }
            }
            actionSystemController.onSave("Xóa quy chế " + obj.getCode(), authorizationController.getAccountDto().getAccountId());
            setSuccessForm("Xóa thành công");
            onSearch();
        } catch (Exception e) {
            setErrorForm("Xóa thất bại");
        }
    }

    public void onDateSelect() {
        if (null !=  this.regulationSearchDto.getFromDate() && null != this.regulationSearchDto.getToDate()
                && this.regulationSearchDto.getFromDate().compareTo(this.regulationSearchDto.getToDate()) > 0) {
            this.getRegulationSearchDto().setToDate(null);
            setErrorForm("Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
        }
    }


    public void changeAution() {
        if (regulationDto.getAuctionFormalityId().equals(1L)) {
            autionid = true;
        } else {
            autionid = false;
        }
    }
    public void onShow(RegulationDto dto) {
        if (dto.getStatus() == DbConstant.CHUA_DANG_TAI) {
            FacesUtil.addErrorMessage("Không tạm dừng được quy chế " + dto.getCode() + "  vì quy chế này chưa được đăng tải lên Trang đấu giá");
            FacesUtil.updateView("growl");
            return;
        } else {
            statusDisable = false;
            regulationDto = dto;
            FacesUtil.showDialog("dialoglidohuybo");
        }
    }

    public boolean checkStarttimeWithActions(RegulationDto dto){
        if (dto.getStartTime().compareTo(new Date()) < 0){
            return true;
        }
        return false;
    }

    public void onCancel(RegulationDto dto) {
            statusDisable = false;
            regulationDto = dto;
            FacesUtil.showDialog("dialogcancel");
    }

    public boolean checkEditButton(Long regulationId) {
        List<Long> checkRegulation = auctionRegisterRepository.findregulationId();
        if (checkRegulation.contains(regulationId)) {
            return true;
        }
        return false;
    }

    public StreamedContent downloadDepositionReasonFile() {
        return getFileDownload(regulationFile.getFilePath());
    }

    @Override
    protected EScope getMenuId() {
        return EScope.REGULATION;
    }
}
