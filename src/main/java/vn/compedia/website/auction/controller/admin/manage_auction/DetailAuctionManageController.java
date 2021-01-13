package vn.compedia.website.auction.controller.admin.manage_auction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.controller.admin.common.UploadSingleFileController;
import vn.compedia.website.auction.dto.auction.*;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.system.socket.SocketServer;
import vn.compedia.website.auction.system.socket.SocketUpdater;
import vn.compedia.website.auction.system.task.AuctionTask;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.system.task.dto.Bargain;
import vn.compedia.website.auction.system.task.helper.RedisHelper;
import vn.compedia.website.auction.system.util.SysConstant;
import vn.compedia.website.auction.util.*;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Log4j2
@Named
@Scope(value = "session")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetailAuctionManageController extends BaseController {
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private UploadSingleFileController uploadSingleFileController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private AuctionTask auctionTask;
    @Inject
    private SocketUpdater socketUpdater;

    @Autowired
    private AssetFileRepository assetFileRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private PriceRoundRepository priceRoundRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AssetManagementRepository assetManagementRepository;
    @Autowired
    private AuctionFormalityRepository auctionFormalityRepository;
    @Autowired
    private AuctionMethodRepository auctionMethodRepository;
    @Autowired
    private RunTheAuctionRepository runTheAuctionRepository;
    @Autowired
    private RegulationRepository regulationRepository;
    @Autowired
    private SystemConfigRepository systemConfigRepository;

    // lazy data
    private LazyDataModel<BidDto> bidDtoList;
    private List<BidDto> bidDtoListTemp;
    private LazyDataModel<AuctionRegisterDto> auctionRegisterList;
    private List<AuctionRegisterDto> auctionRegisterTemp;
    private List<BidDto> bidDtoWinnerList;
    private List<SelectItem> bidName;
    // Dto
    private LazyDataModel<AssetDto> assetDtoList;
    private AssetDto assetDtoBackupView;
    private AssetSearchDto assetSearchDto;
    private AssetDto assetDto;
    private AssetDto assetDtoCancel;
    private BidDto bidDtoUpdate;
    private BidSearchDto bidSearchDto;
    private RegulationDto regulationDto;
    private AuctionRegisterSearchDto auctionRegisterSearchDto;
    // file
    private AssetFile assetFile;
    // model
    private Regulation regulation;
    private AuctionRegister auctionRegister;
    private Asset asset;
    private PriceRound priceRound;
    private Account account;
    private AssetManagement assetManagement;
    // status
    private boolean reasonCancelAssetStatus;
    private boolean stateOnline;
    // time
    private Date nowday;
    private Long auctionRegisterId;
    private Date now;
    private String name;
    private String depositFilePath;
    private String cancelFilePath;
    // update result auction
    private boolean showAuctionOnlyFail;
    private List<SelectItem> bidListUpdate;
    private AssetDto assetDtoUpdate;
    // auction processing
    private AssetDtoQueue assetDtoQueue;
    private AssetManagement amChangeResult;
    // online state
    private String onlineStateString;

    @Value("${auction.random.time}")
    private int secondsRandom;

    public void prepareData(Long regulationId) {
        regulation = runTheAuctionRepository.findById(regulationId).orElse(null);
        regulationDto = new RegulationDto();
        regulationDto.setAuctionFormalityName(auctionFormalityRepository.findAuctionFormalityByAuctionFormalityId(regulation.getAuctionFormalityId()).getName());
        regulationDto.setAuctionMethodName(auctionMethodRepository.findAuctionMethodByAuctionMethodId(regulation.getAuctionMethodId()).getName());
    }

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            if (regulation == null) {
                FacesUtil.redirect("/admin/dashboard.xhtml");
                return;
            }
            resetAll();
        }
    }

    public void resetAll() {
        depositFilePath = null;
        nowday = new Date();
        assetSearchDto = new AssetSearchDto();
        assetDto = new AssetDto();
        assetDtoUpdate = new AssetDto();
        priceRound = new PriceRound();
        auctionRegister = new AuctionRegister();
        account = new Account();
        bidSearchDto = new BidSearchDto();
        assetManagement = new AssetManagement();
        name = "";
        stateOnline = false;
        bidListUpdate = new ArrayList<>();
        assetDtoBackupView = null;
        onSearch();
    }

    public void onSearch() {
        onSearchAssetList();
    }

    public void onSearchAssetList() {
        if (regulation == null) {
            FacesUtil.redirect("/admin/dashboard.xhtml");
            return;
        }
        regulation = runTheAuctionRepository.findById(regulation.getRegulationId()).orElse(null);
        if (regulation == null) {
            FacesUtil.redirect("/admin/dashboard.xhtml");
            return;
        }
        assetSearchDto.setRegulationId(regulation.getRegulationId());
        assetSearchDto.setRegulationStatus(regulation.getAuctionStatus());
        if (StringUtils.isNotBlank(name)) {
            assetSearchDto.setName(name);
        } else {
            assetSearchDto.setName(null);
        }
        assetDtoList = new LazyDataModel<AssetDto>() {
            @Override
            public List<AssetDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                assetSearchDto.setPageIndex(first);
                assetSearchDto.setPageSize(pageSize);
                assetSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                assetSearchDto.setSortOrder(sort);
                List<AssetDto> assets = assetRepository.search(assetSearchDto);
                if (!assets.isEmpty() && assetDtoBackupView == null) {
                    assetDtoBackupView = assets.get(0);
                    showDetailAssetInfoPanel(assetDtoBackupView.getAssetId());
                }
                return assets;
            }
        };
        int count = assetRepository.countSearch(assetSearchDto).intValue();
        assetDtoList.setRowCount(count);
    }

    public void onSaveAuctionRegister() {
        if (StringUtils.isBlank(auctionRegister.getReasonDeposition())) {
            FacesUtil.addErrorMessage("Lý do truất quyền là trường bắt buộc");
            return;
        }
        if (StringUtils.isBlank(uploadSingleFileController.getFilePath())) {
            FacesUtil.addErrorMessage("File văn bản lý do truất quyền là trường bắt buộc");
            return;
        }
        AuctionRegister oldAuctionRegister = auctionRegisterRepository.findAuctionRegisterByAuctionRegisterId(auctionRegister.getAuctionRegisterId());
        oldAuctionRegister.setReasonDeposition(auctionRegister.getReasonDeposition());
        oldAuctionRegister.setStatusDeposit(DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_REFUSE);
        oldAuctionRegister.setFilePath(uploadSingleFileController.getFilePath());
        auctionRegisterRepository.save(oldAuctionRegister);
        FacesUtil.addSuccessMessage("Truất quyền thành công");
        FacesUtil.closeDialog("dialogReasonDeposition");
        FacesUtil.updateView("tblSearchResult1");

        // logging
        String str = String.format("Truất quyền người tham gia %s, tài sản %s (%s)",
                oldAuctionRegister.getCode(), assetDto.getName(), assetDto.getRegulationCode());
        actionSystemController.onSave(str, authorizationController.getAccountDto().getAccountId());

        // deposit
        auctionTask.onDeposition(oldAuctionRegister.getAssetId(), oldAuctionRegister.getAccountId());

        // reset
        searchAuctionRegister();
    }

    public void showDepositionReasonPopup(Long auctionRegisterId) {
        auctionRegister = auctionRegisterRepository.findAuctionRegisterByAuctionRegisterId(auctionRegisterId);
        uploadSingleFileController.resetAll(auctionRegister.getFilePath(), auctionRegister.getFileName());
        //BeanUtils.copyProperties(auctionRegister, bidDto);
    }

    public StreamedContent downloadDepositionReasonFile() {
        return getFileDownload(auctionRegister.getFilePath());
    }

    public void onSaveCancelAsset() {
        if (StringUtils.isBlank(assetDtoCancel.getReasonCancelAsset())) {
            FacesUtil.addErrorMessage("Bạn vui lòng nhập lý do tạm dừng đấu giá tài sản");
            return;
        }
        if (StringUtils.isBlank(uploadSingleFileController.getFilePath())) {
            FacesUtil.addErrorMessage("Bạn vui lòng tải file văn bản lý do tạm dừng đấu giá");
            return;
        }

        boolean assetPlaying = assetDtoCancel.getStatus() == DbConstant.ASSET_STATUS_PLAYING;

        if (assetDtoCancel.getStatus() == DbConstant.ASSET_STATUS_WAITING || assetDtoCancel.getStatus() == DbConstant.ASSET_STATUS_PLAYING) {
            List<AuctionRegister> auctionRegisters = auctionRegisterRepository.findAuctionRegistersByAssetIdForCancel1(assetDtoCancel.getAssetId());
            for (AuctionRegister ar : auctionRegisters) {
                Account account = accountRepository.findAccountByAccountId(ar.getAccountId());
                String regulationCode = regulationRepository.getCodeByRegulationId(ar.getRegulationId());
                if (account.getAccountId() != null) {
                    EmailUtil.getInstance().sendReasonCancelAsset(account.getEmail(),
                            FacesUtil.getRealPath(FileUtil.getFilePathFromDatabase(uploadSingleFileController.getFilePath())),
                            uploadSingleFileController.getFileName(),
                            account.isOrg() ? account.getOrgName() : account.getFullName(),
                            assetDtoCancel.getName(),
                            regulationCode,
                            assetDtoCancel.getReasonCancelAsset());
                }
            }
        }
        assetRepository.changeStatus(assetDtoCancel.getAssetId(), DbConstant.ASSET_STATUS_CANCELED, assetPlaying);
        assetDtoCancel.setStatus(DbConstant.ASSET_STATUS_CANCELED);

        assetFile = assetFileRepository.findAssetFileByAssetIdAndType(assetDtoCancel.getAssetId(), DbConstant.ASSET_FILE_TYPE_CANCEL);
        if (assetFile == null) {
            assetFile = new AssetFile();
        }
        assetFile.setFileName(uploadSingleFileController.getFileName());
        assetFile.setFilePath(uploadSingleFileController.getFilePath());
        assetFile.setAssetId(assetDtoCancel.getAssetId());
        assetFile.setReasonCancelAsset(assetDtoCancel.getReasonCancelAsset());
        assetFile.setType(DbConstant.ASSET_FILE_TYPE_CANCEL);
        assetFileRepository.save(assetFile);
        actionSystemController.onSave("Tạm dừng đấu giá tài sản \"" + assetDtoCancel.getName() + "\"", authorizationController.getAccountDto().getAccountId());
        FacesUtil.addSuccessMessage("Tạm dừng thành công");
        FacesUtil.closeDialog("dialogReasonCancelAsset");
        onSearch();

        // reload detail asset
        showDetailAssetInfoPanel(assetDtoCancel.getAssetId());

        // update startTime
        updateStartTimeAsset(assetPlaying);

        // cancel asset in queue
        auctionTask.cancelAsset(assetDtoCancel.getAssetId());
        if (auctionTask.getAssetDtoList().get(assetDtoCancel.getAssetId()) != null) {
            RedisHelper.syncSocketAuction(SysConstant.SYNC_ACTION_CANCEL_ASSET, assetDtoCancel.getAssetId());
        }
        // notify
        socketUpdater.updateEndedToClients(assetDtoCancel);
        RedisHelper.syncSocketAll(assetDtoCancel.getAssetId(), SysConstant.ACTION_UPDATE_ENDED);
    }

    private void updateStartTimeAsset(boolean assetPlaying) {

        // update startTime
        List<Asset> assets = assetRepository.getAllByRegulationId(assetDtoCancel.getRegulationId());
        Regulation regulation = regulationRepository.findRegulationByRegulationId(assetDtoCancel.getRegulationId());

        // load setup
        Map<Long, SystemConfig> systemConfigList = new HashMap<>();
        systemConfigRepository.findAll().forEach(e -> systemConfigList.put(e.getSystemConfigId(), e));
        SystemConfig configConfirm1th = systemConfigList.get(DbConstant.SYSTEM_CONFIG_ID_WAITING_1TH);
        SystemConfig configConfirm2th = systemConfigList.get(DbConstant.SYSTEM_CONFIG_ID_WAITING_2TH);
        int timeConfirm1th = configConfirm1th == null ? 0 : Math.round(configConfirm1th.getValue());
        int timeConfirm2th = configConfirm2th == null ? 0 : Math.round(configConfirm2th.getValue());
        long timeOneAsset = (regulation.getNumberOfRounds() * regulation.getTimePerRound() + timeConfirm1th + timeConfirm2th) * 60 + secondsRandom * 2;

        Date date = assetDtoCancel.getStartTime();
        if (assetPlaying) {
            date = DateUtil.plusSecond(date, timeOneAsset);
        }
        for (Asset asset : assets) {
            if (asset.getNumericalOrder() <= assetDtoCancel.getNumericalOrder()) {
                continue;
            }
            if (asset.getStatus() == DbConstant.ASSET_STATUS_WAITING) {
                asset.setStartTime(date);
            }
            if (asset.getStatus() != DbConstant.ASSET_STATUS_CANCELED) {
                date = DateUtil.plusSecond(date, timeOneAsset);
            }
        }
        // save
        assetRepository.saveAll(assets);
    }

    public void showReasonCancelAsset(Long assetId) {
        assetDtoCancel = new AssetDto();
        assetFile = new AssetFile();
        Asset asset = assetRepository.findAssetByAssetId(assetId);
        assetFile = assetFileRepository.findAssetFileByAssetIdAndType(assetId, DbConstant.ASSET_FILE_TYPE_CANCEL);
        BeanUtils.copyProperties(asset, assetDtoCancel);
        if (assetFile != null) {
            assetDtoCancel.setReasonCancelAsset(assetFile.getReasonCancelAsset());
            cancelFilePath = assetFile.getFilePath();
            uploadSingleFileController.resetAll(assetFile.getFilePath(), assetFile.getFileName());
        } else {
            uploadSingleFileController.onRemoveFile();
        }
    }

    public void reloadBasicAssetDetail() {
        assetDto = new AssetDto();
        priceRound = new PriceRound();
        account = new Account();
        assetDto = assetRepository.getAssetDtoByAssetId(assetSearchDto);
        // asset ended
        if (assetDto != null && assetDto.getStatus() == DbConstant.ASSET_STATUS_ENDED) {
            assetManagement = assetManagementRepository.findAssetManagementByAssetId(assetDto.getAssetId());
//            priceRound = priceRoundRepository.findFirstByAssetIdOrderByNumberOfRoundDesc(assetDto.getAssetId());
            if (assetManagement != null && assetManagement.isEnding() == DbConstant.ASSET_MANAGEMENT_ENDING_GOOD) {
                auctionRegister = auctionRegisterRepository.findAuctionRegisterByAuctionRegisterId(assetManagement.getAuctionRegisterId());
                if (auctionRegister == null) {
                    account = null;
                } else {
                    account = accountRepository.findAccountByAccountId(auctionRegister.getAccountId());
                }
            }
        }
    }

    public void showDetailAssetInfoPanel(Long assetId) {
        stateOnline = false;

        // load assetDto
        //assetSearchDto = new AssetSearchDto();
        assetSearchDto.setAssetId(assetId);

        reloadBasicAssetDetail();

        // bid & register list
        auctionRegisterSearchDto = new AuctionRegisterSearchDto();
        searchAuctionRegister();
        searchBid();

        // assetDtoQueue
        assetDtoQueue = auctionTask.getAssetDtoList().get(assetId);
    }

    public void searchAuctionRegister() {
        stateOnline = false;
        auctionRegisterSearchDto.setAssetId(assetDto.getAssetId());
        auctionRegisterSearchDto.setStatus(DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED);
        auctionRegisterList = new LazyDataModel<AuctionRegisterDto>() {
            @Override
            public List<AuctionRegisterDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                if (auctionRegisterSearchDto.getPageIndex() != first) {
                    stateOnline = false;
                }
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
                List<AuctionRegisterDto> result;
                if (!stateOnline) {
                    result = auctionRegisterRepository.search(auctionRegisterSearchDto);
                    auctionRegisterTemp = result;
                } else {
                    result = auctionRegisterTemp;
                }

                // check in time confirm
                buildInfoConfirm(result);

                return result;
            }
        };
        int count = auctionRegisterRepository.countSearch(auctionRegisterSearchDto).intValue();
        auctionRegisterList.setRowCount(count);
        assetManagement = assetManagementRepository.findAssetManagementByAssetId(assetDto.getAssetId());
    }

    public void searchBid() {
        stateOnline = false;
        bidSearchDto.setMaxNumberOfRound(null);
        bidSearchDto.setAssetId(assetDto.getAssetId());
        if (assetDto.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_POLL) {
            AssetDtoQueue assetDtoQueue = auctionTask.getAssetDtoList().get(assetDto.getAssetId());
            if (assetDtoQueue != null
                    && !assetDtoQueue.isEnded()
                    && !assetDtoQueue.isInTimeConfirm()
                    && assetDtoQueue.getCurrentRound() != null) {
                if (assetDtoQueue.getCurrentRound() <= 1) {
                    bidSearchDto.setMaxNumberOfRound(0L);
                } else {
                    bidSearchDto.setMaxNumberOfRound((long) (assetDtoQueue.getCurrentRound() - 1));
                }
            }
        }

        bidDtoList = new LazyDataModel<BidDto>() {
            @Override
            public List<BidDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                if (bidSearchDto.getPageIndex() != first) {
                    stateOnline = false;
                }
                bidSearchDto.setPageIndex(first);
                bidSearchDto.setPageSize(pageSize);
                bidSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                bidSearchDto.setSortOrder(sort);
                if (!stateOnline) {
                    bidDtoListTemp = bidRepository.search(bidSearchDto);
                }
                return bidDtoListTemp;
            }
        };
        int count = bidRepository.countSearch(bidSearchDto).intValue();
        bidDtoList.setRowCount(count);
    }

    public void showUpdateStatus(Long assetId) {
        bidName = new ArrayList<>();
        assetDtoUpdate = new AssetDto();
        bidDtoUpdate = new BidDto();
        bidListUpdate = new ArrayList<>();
        amChangeResult = assetManagementRepository.findAssetManagementByAssetId(assetId);
        bidDtoWinnerList = bidRepository.loadBidWinnerByAssetId(assetId);
        if (!bidDtoWinnerList.isEmpty()) {
            for (BidDto bidDto : bidDtoWinnerList) {
                if (amChangeResult.getAuctionRegisterId() == null || !amChangeResult.getAuctionRegisterId().equals(bidDto.getAuctionRegisterId())) {
                    bidListUpdate.add(new SelectItem(bidDto.getAuctionRegisterId(), bidDto.getFullName()));
                }
            }
            if (!bidListUpdate.isEmpty()) {
                bidDtoUpdate.setMoney(bidDtoWinnerList.get(0).getMoney());
                showAuctionOnlyFail = false;
                assetDtoUpdate.setStatus(DbConstant.ASSET_STATUS_ENDED);
                return;
            }
        }
        showAuctionOnlyFail = true;
        amChangeResult.setNumberOfUpdateResult(1);
        assetDtoUpdate.setStatus(DbConstant.ASSET_STATUS_NOT_SUCCESS);
    }

    public void reloadAssetDetail() {
        onSearchAssetList();
        stateOnline = false;
        reloadBasicAssetDetail();
        searchBid();
        // assetDtoQueue
        assetDtoQueue = auctionTask.getAssetDtoList().get(assetDto.getAssetId());
        regulation = runTheAuctionRepository.findById(regulation.getRegulationId()).orElse(null);
    }

    public void onSaveUpdateResult() {
        // change status
        assetRepository.changeStatus(assetDto.getAssetId(), assetDtoUpdate.getStatus());

        AuctionRegister auctionRegister = auctionRegisterRepository.findById(amChangeResult.getAuctionRegisterId()).orElse(null);
        if (auctionRegister != null) {
            auctionRegister.setStatusRefuseWin(DbConstant.AUCTION_REGISTER_STATUS_REFUSE_WIN_REFUSED);
            auctionRegisterRepository.save(auctionRegister);
        }

        // save auction management
        amChangeResult.setNumberOfUpdateResult(1);
        amChangeResult.setUpdateBy(authorizationController.getAccountDto().getAccountId());

        if (assetDtoUpdate.getStatus() == DbConstant.ASSET_STATUS_NOT_SUCCESS) {
            amChangeResult.setEnding(DbConstant.ASSET_MANAGEMENT_ENDING_BAD);
        } else {
            amChangeResult.setMoney(bidDtoUpdate.getMoney());
        }
        amChangeResult.setAuctionRegisterId(auctionRegisterId);
        assetManagementRepository.save(amChangeResult);

        if (auctionRegisterId == null) {
            account = null;
        } else {
            AuctionRegister auctionChangeWinner = auctionRegisterRepository.findById(amChangeResult.getAuctionRegisterId()).orElse(null);
            if (auctionChangeWinner == null) {
                account = null;
            } else {
                account = accountRepository.findAccountByAccountId(auctionChangeWinner.getAccountId());
                auctionChangeWinner.setStatusRefuseWin(DbConstant.AUCTION_REGISTER_STATUS_REFUSE_WIN_NORMAL);
                auctionRegisterRepository.save(auctionChangeWinner);
            }
        }
        actionSystemController().onSave("Cập nhật trạng thái đấu giá của tài sản: " + assetDto.getName(), authorizationController.getAccountDto().getAccountId());

        FacesUtil.addSuccessMessage("Cập nhật kết quả thành công!");
        FacesUtil.closeDialog("dialogUpdate");
        showDetailAssetInfoPanel(assetDto.getAssetId());
    }

    public void onSaveFailResult() {
        // save asset
        asset = assetRepository.findById(assetDto.getAssetId()).orElse(null);
        if (asset != null) {
            asset.setStatus(assetDtoUpdate.getStatus());
            assetRepository.save(asset);
        }
        // save auction register
        AuctionRegister ar = auctionRegisterRepository.findAuctionRegisterByAuctionRegisterId(assetManagement.getAuctionRegisterId());
        ar.setStatusRefuseWin(DbConstant.AUCTION_REGISTER_STATUS_REFUSE_WIN_REFUSED);
        auctionRegisterRepository.save(ar);
        assetManagementRepository.save(assetManagement);

        // save asset management
        AssetManagement assetManagement = assetManagementRepository.findAssetManagementByAssetId(asset.getAssetId());
        if (assetDtoUpdate.getStatus() == DbConstant.ASSET_STATUS_NOT_SUCCESS) {
            assetManagement.setEnding(DbConstant.ASSET_MANAGEMENT_ENDING_BAD);
        }
        assetManagementRepository.save(assetManagement);
        //
        actionSystemController().onSave("Cập nhật trạng thái đấu giá của tài sản :" + asset.getName(), authorizationController.getAccountDto().getAccountId());
        assetManagement.setEnding(DbConstant.ASSET_MANAGEMENT_ENDING_BAD);
        FacesUtil.addSuccessMessage("Cập nhật kết quả thành công!");
        FacesUtil.closeDialog("dialogUpdate");
        showDetailAssetInfoPanel(assetDto.getAssetId());
    }

    private void buildInfoConfirm(List<AuctionRegisterDto> auctionRegisterDtoList) {
        AssetDtoQueue assetDtoQueue = auctionTask.getAssetDtoList().get(assetDto.getAssetId());
        Bargain bargain = null;
        if (assetDtoQueue != null) {
            bargain = assetDtoQueue.getWinner();
        }
        for (AuctionRegisterDto auctionRegisterDto : auctionRegisterDtoList) {
            // online state
            auctionRegisterDto.setOnlineStatus(SocketServer.isOnline(auctionRegisterDto.getAccountId()));
            // build
            if (assetDtoQueue != null) {
                auctionRegisterDto.setStartTime(assetDtoQueue.getStartTime());
                auctionRegisterDto.setTimeJoined(assetDtoQueue.getAccountJoined().get(auctionRegisterDto.getAccountId()));
            }
            // info confirm
            if (assetDtoQueue != null && assetDtoQueue.isInTimeConfirm() && bargain != null) {
                // check doing confirm?
                if (auctionRegisterDto.getAccountId().equals(bargain.getAccountId())) {
                    auctionRegisterDto.setInConfirm(!assetDtoQueue.isEnded());
                }
                // check 1th refused?
                if (assetDtoQueue.getRoundConfirm() == 2
                        && !assetDtoQueue.isDeposit1th()
                        && auctionRegisterDto.getAccountId().equals(assetDtoQueue.getWinner1th().getAccountId())
                        && !auctionRegisterDto.getAccountId().equals(assetDtoQueue.getWinner().getAccountId())
                ) {
                    auctionRegisterDto.setRefuseWinner(true);
                }
            }
            // set notes
            auctionRegisterDto.setNotes(getNotes(assetDtoQueue, auctionRegisterDto));
        }
    }

    private String getNotes(AssetDtoQueue assetDtoQueue, AuctionRegisterDto auctionRegisterDto) {
        List<String> str = new ArrayList<>();

        if (auctionRegisterDto.getTimeJoined() != null && auctionRegisterDto.getStartTime() != null) {
            long[] tDate = DateUtil.countdown(auctionRegisterDto.getStartTime(), auctionRegisterDto.getTimeJoined());
            long hours = tDate[1];
            long minutes = tDate[2];
            if (hours > 0 || minutes > 0) {
                str.add(String.format(
                        "Vào muộn %s%s phút",
                        hours > 0 ? hours + " giờ " : "",
                        minutes));
            }
        }
        if (auctionRegisterDto.isInConfirm() && assetDtoQueue != null && !assetDtoQueue.isInTimeRandom()) {
            str.add("Đang xác nhận trúng đấu giá");
        }
        if (auctionRegisterDto.isStatusRetract()) {
            str.add("Đã rút lại giá");
        } else if (auctionRegisterDto.getStatusDeposit() == DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_NORMAL
                || (assetDtoQueue != null && assetDtoQueue.hasDepositionBySystem(auctionRegisterDto.getAccountId()))) {
            if (auctionRegisterDto.isRefuseWinner() || auctionRegisterDto.isStatusRefuseWin()) {
                str.add("Đã từ chối trúng đấu giá");
            } else if (DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_NORMAL == auctionRegisterDto.getStatusDeposit()
                    && auctionRegisterDto.getAssetManagementWinnerId() != null
                    && !auctionRegisterDto.getAuctionRegisterId().equals(auctionRegisterDto.getAssetManagementWinnerId())
                    && auctionRegisterDto.getWinnerSn() != null
                    && auctionRegisterDto.getWinnerSn() == DbConstant.BID_WINNER_SN_1TH) {
                str.add("Không thanh toán tiền trúng đấu giá");
            }
        }

        if (auctionRegisterDto.isEnding()) {
            str.add("Người thắng đấu giá");
        }

        return String.join(", ", str);
    }

    public void reloadStateOnline() {
        if (auctionRegisterTemp == null || auctionRegisterTemp.isEmpty()) {
            onlineStateString = "[]";
            return;
        }
        List<Long> auctionRegisterOnline = new ArrayList<>();
        for (AuctionRegisterDto auctionRegisterDto : auctionRegisterTemp) {
            // online state
            auctionRegisterDto.setOnlineStatus(SocketServer.isOnline(auctionRegisterDto.getAccountId()));
            if (auctionRegisterDto.isOnlineStatus()) {
                auctionRegisterOnline.add(auctionRegisterDto.getAuctionRegisterId());
            }

        }
        stateOnline = true;
        onlineStateString = StringUtil.toJson(auctionRegisterOnline);
    }

    public void setShowFileDeposit(String fileDeposit) {
        depositFilePath = fileDeposit;
    }

    // AREA RENDERED
    public boolean renderInTimeConfirm() {
        return assetDto != null
                && assetDto.getStatus() != null
                && assetDto.getStatus() == DbConstant.ASSET_STATUS_PLAYING
                && assetDtoQueue != null
                && assetDtoQueue.isInTimeConfirm()
                && !assetDtoQueue.isInTimeRandom();
    }

    public boolean renderInTimeRandom() {
        return assetDto != null
                && assetDto.getStatus() != null
                && assetDto.getStatus() == DbConstant.ASSET_STATUS_PLAYING
                && assetDtoQueue != null
                && assetDtoQueue.isInTimeConfirm()
                && assetDtoQueue.isInTimeRandom();
    }

    public boolean renderRegisterConfirming() {
        if (assetDtoQueue != null) {
            return !assetDtoQueue.isInTimeRandom() && assetDtoQueue.isInTimeConfirm() && !assetDtoQueue.isEnded();
        }
        return false;
    }

    public String valueRegisterConfirming() {
        try {
            Long accountId = assetDtoQueue.getWinner().getAccountId();
            if (auctionRegisterList.getWrappedData() == null) {
                return "Đang lấy dữ liệu...";
            }
            for (AuctionRegisterDto auctionRegisterDto : auctionRegisterList.getWrappedData()) {
                if (auctionRegisterDto.getAccountId().equals(accountId)) {
                    return auctionRegisterDto.getFullNameNguoiDangKy() + " (" + auctionRegisterDto.getCode() + ")";
                }
            }
            return assetDtoQueue.getInfoAuctionRegister(accountId).getCode();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public long valueCountdownRandom() {
        if (assetDtoQueue == null) {
            return 0;
        }
        long output = assetDtoQueue.getEndTimeRandom().getTime() - getNow().getTime();
        return output / 1000;
    }

    public long valueCountdownPlaying() {
        if (assetDtoQueue == null || assetDtoQueue.getEndTimeRound() == null) {
            return 0;
        }
        long output = assetDtoQueue.getEndTimeRound().getTime() - getNow().getTime();
        return output / 1000;
    }

    public long valueCountdownConfirm() {
        long output;
        if (assetDtoQueue.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_DOWN) {
            output = assetDtoQueue.endTimeAcceptPrice().getTime() - getNow().getTime();
        } else {
            output = assetDtoQueue.getEndTimeRound().getTime() - getNow().getTime();
        }
        return output / 1000;
    }

    public boolean renderCurrentRound() {
        return assetDtoQueue != null
                && !assetDtoQueue.isCancel()
                && !assetDtoQueue.isEnded()
                && assetDtoQueue.isInProcessing()
                && assetDtoQueue.getAuctionFormalityId() == DbConstant.AUCTION_FORMALITY_ID_POLL
                && assetDtoQueue.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_UP
                && !assetDtoQueue.isInTimeConfirm();
    }

    public boolean renderCurrentRoundConfirm() {
        return assetDtoQueue != null
                && !assetDtoQueue.isEnded()
                && assetDtoQueue.isInProcessing()
                && assetDtoQueue.getAuctionMethodId() == DbConstant.AUCTION_METHOD_ID_UP
                && assetDtoQueue.isInTimeConfirm();
    }

    public boolean renderCountdownPlaying() {
        return assetDto != null
                && assetDto.getStatus() != null
                && assetDto.getStatus() == DbConstant.ASSET_STATUS_PLAYING
                && assetDtoQueue != null
                && !assetDtoQueue.isEnded()
                && !assetDtoQueue.isInTimeConfirm()
                && assetDtoQueue.isInProcessing();
    }

    public boolean renderLblCountdownConfirm() {
        return assetDtoQueue != null
                && assetDtoQueue.isInTimeConfirm()
                && !assetDtoQueue.isEnded();
    }

    public boolean renderLblCountdownPlaying() {
        return assetDtoQueue != null
                && !assetDtoQueue.isInTimeConfirm()
                && assetDtoQueue.isEnded();
    }

    public boolean renderWinner() {
        return assetDto != null
                && assetDto.getStatus() != null
                && DbConstant.ASSET_STATUS_ENDED == assetDto.getStatus()
                && assetManagement.isEnding();
    }

    public String valueWinner() {
        if (account == null) {
            return null;
        }
        String fullName = account.isOrg() ? account.getOrgName() : account.getFullName();
        try {
            fullName += " (" + auctionRegister.getCode() + ")";
        } catch (Exception e) {
            log.error(e);
        }
        return fullName;
    }

    public boolean renderPriceWinner() {
        return assetDtoQueue != null
                && assetDtoQueue.isInTimeConfirm()
                && !assetDtoQueue.isEnded();
    }

    public boolean renderDepositColumn(AuctionRegisterDto auctionRegisterDto) {
        return (DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_REFUSE == auctionRegisterDto.getStatusDeposit()
                && (auctionRegisterDto.getFilePath() != null
                    || (assetDto != null && assetDto.getStatus() != null && assetDto.getStatus() == DbConstant.ASSET_STATUS_PLAYING)
                    || auctionRegisterDto.isStatusJoined()));
    }

    public Date getNow() {
        return new Date();
    }

    public boolean showReasonCancel(AssetDto asset) {
        List<AssetFile> assetFiles  = assetFileRepository.findAllByAssetId(asset.getAssetId());

        for (AssetFile asf : assetFiles) {
            if (asf.getType() != null && asf.getType() == DbConstant.ASSET_FILE_TYPE_CANCEL) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected EScope getMenuId() {
        return EScope.AUCTION_MANAGE;
    }
}
