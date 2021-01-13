package vn.compedia.website.auction.service.impl;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.service.AssetService;
import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.system.task.dto.RegulationDtoQueue;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssetServiceImpl implements AssetService {
    @Autowired
    protected AssetImageRepository assetImageRepository;
    @Autowired
    protected AssetRepository assetRepository;
    @Autowired
    private AssetFileRepository assetFileRepository;
    @Autowired
    private RegulationRepository regulationRepository;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Value("${task.scan.range.minute}")
    private int scanRange;
    @Value("${auction.random.time}")
    private int secondsRandom;
    private Asset asset;
    private List<AssetFile> assetFileList;
    private List<AssetImage> assetImageList;
    private List<AssetImage> assetImageDeleteAllList;
    private List<String> assetImageHolder;
    private Regulation regulation;

    @Override
    @Transactional
    public void save(List<AssetDto> assetDtoList) {
        assetFileList = new ArrayList<>();
        assetImageList = new ArrayList<>();
        assetImageHolder = new ArrayList<>();
        assetImageDeleteAllList = new ArrayList<>();
        regulation = regulationRepository.findRegulationByRegulationId(assetDtoList.get(0).getRegulationId());

        // load setup
        Map<Long, SystemConfig> systemConfigList = new HashMap<>();
        systemConfigRepository.findAll().forEach(e -> systemConfigList.put(e.getSystemConfigId(), e));
        SystemConfig configConfirm1th = systemConfigList.get(DbConstant.SYSTEM_CONFIG_ID_WAITING_1TH);
        SystemConfig configConfirm2th = systemConfigList.get(DbConstant.SYSTEM_CONFIG_ID_WAITING_2TH);
        int timeConfirm1th = configConfirm1th == null ? 0 : Math.round(configConfirm1th.getValue());
        int timeConfirm2th = configConfirm2th == null ? 0 : Math.round(configConfirm2th.getValue());
        long timeOneAsset = (regulation.getNumberOfRounds() * regulation.getTimePerRound() + timeConfirm1th + timeConfirm2th) * 60 + secondsRandom * 2;

        int i = 1;
        int minutePlus = 0;
        for (AssetDto tempAsset : assetDtoList) {
            asset = new Asset();
            BeanUtils.copyProperties(tempAsset, asset);
            asset.setNumericalOrder(i);
            if (asset.getNumericalOrder() == 1) {
                asset.setStartTime(regulation.getStartTime());
            } else {
                asset.setStartTime(DateUtil.plusSecond(regulation.getStartTime(), minutePlus));
            }
            minutePlus += timeOneAsset;
            asset = assetRepository.save(asset);
            for (AssetFile tempAssetFile : tempAsset.getAssetFileList()) {
                tempAssetFile.setAssetId(asset.getAssetId());
                assetFileList.add(tempAssetFile);
            }
            List<AssetImage> oldAssetImageList = assetImageRepository.findAllByAssetId(asset.getAssetId());
            for (AssetImage tempAssetImage : tempAsset.getAssetImageList()) {
                if (oldAssetImageList.stream()
                        .noneMatch(e -> e.getFilePath().equals(tempAssetImage.getFilePath()))) {
                    tempAssetImage.setAssetId(asset.getAssetId());
                    assetImageList.add(tempAssetImage);
                } else {
                    assetImageHolder.add(tempAssetImage.getFilePath());
                }
            }
            List<AssetImage> assetImageDeleteList = oldAssetImageList.stream()
                    .filter(e -> !assetImageHolder.contains(e.getFilePath()))
                    .collect(Collectors.toList());
            if (!assetImageDeleteList.isEmpty()) {
                assetImageDeleteAllList.addAll(assetImageDeleteList);
            }
            i++;
        }
        if (!assetImageDeleteAllList.isEmpty()) {
            assetImageRepository.deleteAll(assetImageDeleteAllList);
        }
        assetFileRepository.deleteAssetFileByAssetId(asset.getAssetId());
        assetFileRepository.saveAll(assetFileList);
        assetImageRepository.saveAll(assetImageList);
    }

    @Override
    public Queue<RegulationDtoQueue> findAllByCurrentTime() {
        Queue<RegulationDtoQueue> outputList = new PriorityQueue<>();
        Date now = new Date();
        Date fromTime = DateUtil.parseDatePattern(now, DateUtil.DATE_FORMAT_MINUTE);
        Date toTime = DateUtil.parseDatePattern(DateUtil.plusMinute(now, scanRange), DateUtil.DATE_FORMAT_MINUTE);
        List<Regulation> regulationList = regulationRepository.findAllByStatusAndAuctionStatusAndStartTimeBetween(DbConstant.REGULATION_STATUS_PUBLIC, DbConstant.REGULATION_AUCTION_STATUS_WAITING, fromTime, toTime);
        for (Regulation regulation : regulationList) {
            Queue<AssetDtoQueue> assetDtoQueueList = new PriorityQueue<>();
            boolean flagResetStartTime = true;
            // load assets
            List<Asset> assetList = assetRepository.findAssetsByRegulationIdAndStatus(regulation.getRegulationId(), DbConstant.ASSET_STATUS_WAITING);
            Date startTime = new Date();
            // build assets
            for (Asset asset : assetList) {
                //
                AssetDtoQueue assetDtoQueue = new AssetDtoQueue();
                BeanUtils.copyProperties(asset, assetDtoQueue);
                if (flagResetStartTime) {
                    assetDtoQueue.setFirstAsset(true);
                    flagResetStartTime = false;
                }
                assetDtoQueue.setTimePerRound(regulation.getTimePerRound());
                assetDtoQueue.setNumberOfRounds(regulation.getNumberOfRounds());
                assetDtoQueue.setRetractPrice(regulation.isRetractPrice());
                assetDtoQueue.setAuctionFormalityId(regulation.getAuctionFormalityId());
                assetDtoQueue.setAuctionMethodId(regulation.getAuctionMethodId());
                assetDtoQueue.setRegulationCode(regulation.getCode());
                // add to list
                assetDtoQueueList.offer(assetDtoQueue);
            }
            RegulationDtoQueue regulationDtoQueue = new RegulationDtoQueue();
            BeanUtils.copyProperties(regulation, regulationDtoQueue);
            regulationDtoQueue.setAssetDtoQueueList(assetDtoQueueList);
            regulationDtoQueue.setAuctionRegisterList(auctionRegisterRepository.findAllByRegulationIdAndStatus(regulation.getRegulationId(), DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED));
            outputList.offer(regulationDtoQueue);
        }
        return outputList;
    }
}
