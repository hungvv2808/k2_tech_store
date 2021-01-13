package vn.compedia.website.auction.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.compedia.website.auction.model.AssetManagement;
import vn.compedia.website.auction.model.AuctionRegister;
import vn.compedia.website.auction.model.Regulation;
import vn.compedia.website.auction.repository.AssetManagementRepository;
import vn.compedia.website.auction.repository.AuctionRegisterRepository;
import vn.compedia.website.auction.repository.RegulationRepository;
import vn.compedia.website.auction.service.AssetManagementService;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.util.DbConstant;

import java.util.ArrayList;
import java.util.Date;

@Service
public class AssetManagementServiceImpl implements AssetManagementService {
    @Autowired
    private AssetManagementRepository assetManagementRepository;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private RegulationRepository regulationRepository;

    @Override
    @Transactional
    public void save(AssetDtoQueue assetDtoQueue, boolean ending) {
        // create record
        AssetManagement assetManagement = new AssetManagement();
        assetManagement.setAssetId(assetDtoQueue.getAssetId());
        assetManagement.setStartTime(assetDtoQueue.getStartTime());
        assetManagement.setEndTime(new Date());
        assetManagement.setEnding(ending);
        assetDtoQueue.setAssetManagementEnding(ending);
        if (ending == DbConstant.ASSET_MANAGEMENT_ENDING_GOOD) {
            assetManagement.setAuctionRegisterId(assetDtoQueue.getWinner().getAuctionRegisterId());
            assetManagement.setMoney(assetDtoQueue.getWinner().getMoney());
        }
        assetManagementRepository.saveAndFlush(assetManagement);
        // save regulation
        Regulation regulation = regulationRepository.findById(assetDtoQueue.getAssetId()).orElse(null);
        if (regulation != null) {
            regulation.setRealStartTime(assetDtoQueue.getStartTime());
            regulation.setRealEndTime(new Date());
            regulationRepository.saveAndFlush(regulation);
        }
        // set ended
        assetDtoQueue.setEnded(true);
    }
}
