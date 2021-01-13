package vn.compedia.website.auction.controller.admin;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.system.socket.SocketServer;
import vn.compedia.website.auction.system.task.AuctionTask;
import vn.compedia.website.auction.system.task.SystemTask;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Getter
@Setter
@Log4j2
@Named
@Scope(value = "session")
public class TestingController extends BaseController {
    @Inject
    private SystemTask systemTask;
    @Inject
    private AuctionTask auctionTask;
    @Autowired
    private RegulationRepository regulationRepository;
    @Autowired
    private RegulationFileRepository regulationFileRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private PriceRoundRepository priceRoundRepository;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private AssetManagementRepository assetManagementRepository;
    @Autowired
    private AssetFileRepository assetFileRepository;
    @Autowired
    private RegulationReportFileRepository regulationReportFileRepository;
    @Autowired
    private SystemConfigRepository systemConfigRepository;

    private Long regulationId;
    private Integer minutes;
    private boolean cleanRunning = true;
    private boolean cleanQueue = true;
    @Value("${auction.random.time}")
    private int secondsRandom;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            minutes = null;
            regulationId = null;
        }
    }

    public void setup() {
        try {
            if (minutes < 1) {
                setErrorForm("minute >= 1");
                return;
            }

            Regulation regulation = regulationRepository.findById(regulationId).orElse(null);
            List<Asset> assetList = assetRepository.findAssetsByRegulationId(regulationId);

            if (regulation != null) {
                // load setup
                Map<Long, SystemConfig> systemConfigList = new HashMap<>();
                systemConfigRepository.findAll().forEach(e -> systemConfigList.put(e.getSystemConfigId(), e));
                SystemConfig configConfirm1th = systemConfigList.get(DbConstant.SYSTEM_CONFIG_ID_WAITING_1TH);
                SystemConfig configConfirm2th = systemConfigList.get(DbConstant.SYSTEM_CONFIG_ID_WAITING_2TH);
                int timeConfirm1th = configConfirm1th == null ? 0 : Math.round(configConfirm1th.getValue());
                int timeConfirm2th = configConfirm2th == null ? 0 : Math.round(configConfirm2th.getValue());
                int timeOneAsset = (regulation.getNumberOfRounds() * regulation.getTimePerRound() + timeConfirm1th + timeConfirm2th) * 60 + secondsRandom * 2;

                //
                regulation.setStartTime(DateUtil.plusMinute(new Date(), minutes));
                regulation.setStartRegistrationDate(DateUtil.formatFromDate(new Date()));
                regulation.setEndRegistrationDate(DateUtil.formatToDate(DateUtil.plusDay(new Date(), 10)));
                Date now = null;
                for (Asset asset : assetList) {
                    if (now == null) {
                        asset.setStartTime(regulation.getStartTime());
                    } else if (asset.getStatus() == DbConstant.ASSET_STATUS_WAITING) {
                        asset.setStartTime(DateUtil.plusSecond(now, timeOneAsset));
                    }
                    now = asset.getStartTime();
                }
                assetRepository.saveAll(assetList);
                regulationRepository.saveAndFlush(regulation);
                setSuccessForm("Done", "startTime: " + DateUtil.formatToPattern(regulation.getStartTime(), DateUtil.HHMMSS_DDMMYYYY) + ". Please logout all account and login again.");
            } else {
                setErrorForm("Not found");
            }
        } catch (Exception e) {
            log.error(e);
            setErrorForm(e.toString());
        }
    }

    public void clean() {
        try {
            // clean task
            if (cleanRunning) {
//                auctionTask.getAssetDtoList().clear();
//                auctionTask.getRegulationDtoList().clear();
            }
            if (cleanQueue) {
                systemTask.getRegulationDtoQueues().clear();
            }

            if (regulationId == null) {
                setSuccessForm("Done");
                return;
            }

            Regulation regulation = regulationRepository.findById(regulationId).orElse(null);
            List<Asset> assetList = assetRepository.findAssetsByRegulationId(regulationId);
            List<AssetManagement> assetManagementList = new ArrayList<>();
            List<Bid> bidList = new ArrayList<>();

            if (regulation != null) {

                // asset
                for (Asset asset : assetList) {
                    asset.setStatus(DbConstant.ASSET_STATUS_WAITING);
                    AssetManagement assetManagement = assetManagementRepository.findAssetManagementByAssetId(asset.getAssetId());
                    if (assetManagement != null) {
                        assetManagementList.add(assetManagement);
                    }
                    bidList.addAll(bidRepository.findAllByAssetId(asset.getAssetId()));

                    // asset file
                    assetFileRepository.deleteAssetFileByAssetIdAndType(asset.getAssetId(), DbConstant.ASSET_FILE_TYPE_CANCEL);
                }
                assetRepository.saveAll(assetList);

                // regulation
                regulation.setStatus(DbConstant.REGULATION_STATUS_PUBLIC);
                regulation.setAuctionStatus(DbConstant.REGULATION_AUCTION_STATUS_WAITING);
                regulation.setStartRegistrationDate(DateUtil.formatFromDate(new Date()));
                regulation.setEndRegistrationDate(DateUtil.formatToDate(DateUtil.plusDay(new Date(), 10)));
                regulationRepository.save(regulation);

                // regulation file
                regulationFileRepository.deleteByRegulationIdAndType(regulation.getRegulationId(), DbConstant.REGULATION_FILE_TYPE_HUY_BO);

                // asset management
                if (!assetManagementList.isEmpty()) {
                    assetManagementRepository.deleteAll(assetManagementList);
                }

                // regulation_report_file
                regulationReportFileRepository.deleteByRegulationId(regulationId);

                // bid
                if (!bidList.isEmpty()) {
                    bidRepository.deleteAll(bidList);
                }

                // price round
                priceRoundRepository.deleteAllByRegulationId(regulationId);

                // auction_register
                List<AuctionRegister> auctionRegisterList = new ArrayList<>(auctionRegisterRepository.findAllByRegulationId(regulationId));
                if (!auctionRegisterList.isEmpty()) {
                    for (AuctionRegister auctionRegister : auctionRegisterList) {
                        auctionRegister.setStatusDeposit(DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_NORMAL);
                        auctionRegister.setStatus(DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED);
                        auctionRegister.setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_ZERO);
                        auctionRegister.setStatusRefuseWin(DbConstant.AUCTION_REGISTER_STATUS_REFUSE_WIN_NORMAL);
                        auctionRegister.setReasonDeposition(null);
                        auctionRegister.setStatusJoined(DbConstant.AUCTION_REGISTER_STATUS_JOINED_NOT_JOIN);
                        auctionRegister.setFilePath(null);
                    }
                    auctionRegisterRepository.saveAll(auctionRegisterList);
                }

                setSuccessForm("Done");
            } else {
                setErrorForm("Not found");
            }
        } catch (Exception e) {
            log.error(e);
            setErrorForm(e.toString());
        }
    }

    public int sizeSession() {
        return SocketServer.getSESSIONS().size();
    }

    @Override
    protected EScope getMenuId() {
        return EScope.PUBLIC;
    }
}
