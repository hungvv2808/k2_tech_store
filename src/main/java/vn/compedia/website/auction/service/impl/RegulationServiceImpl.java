package vn.compedia.website.auction.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.service.AssetService;
import vn.compedia.website.auction.service.RegulationService;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegulationServiceImpl implements RegulationService {
    @Autowired
    private AssetService assetService;
    @Autowired
    private RegulationRepository regulationRepository;
    @Autowired
    private RegulationFileRepository regulationFileRepository;
    @Autowired
    private RegulationReportFileRepository regulationReportFileRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssetFileRepository assetFileRepository;
    @Autowired
    private AssetImageRepository assetImageRepository;

    private List<RegulationFile> regulationFileList;

    @Override
    @Transactional
    public void save(Regulation regulation, List<String> fileList, List<AssetDto> assetDtoList) {

        if (regulation.getRegulationId() != null) {
            deleteListAsset(regulation);
        }

        //save regulation
        regulationRepository.save(regulation);
        String regulationCode = String.format("QC%06d", regulation.getRegulationId());
        regulation.setCode(regulationCode);
        regulationRepository.saveAndFlush(regulation);
        //build regulation file
        regulationFileList = regulationFileRepository.findAllByRegulationId(regulation.getRegulationId());
        if (regulationFileList == null) {
            regulationFileList = new ArrayList<>();
        }
        //build list to delete
        List<RegulationFile> regulationFileListDelete = regulationFileList.stream().filter(e -> !fileList.contains(e.getFilePath())).collect(Collectors.toList());
        //
        for (String pathFile : fileList) {
            if (regulationFileList.stream().filter(e -> e.getFilePath().equals(pathFile)).findAny().orElse(null) != null) {
                continue;
            }
            //
            RegulationFile regulationFile = new RegulationFile();
            regulationFile.setRegulationId(regulation.getRegulationId());
            regulationFile.setFilePath(pathFile);
            regulationFile.setType(DbConstant.REGULATION_FILE_TYPE_QUY_CHE);
            regulationFileList.add(regulationFile);
        }
        //remove old regulation files
        if (!regulationFileListDelete.isEmpty()) {
            regulationFileRepository.deleteAll(regulationFileListDelete);
        }
        //save regulation files
        for (RegulationFile regulationFile : regulationFileListDelete) {
            if (regulationFileList.contains(regulationFile)) {
                regulationFileList.remove(regulationFile);
            }
        }
        regulationFileRepository.saveAll(regulationFileList);
        //save assets list
        for (AssetDto assetDto : assetDtoList) {
            assetDto.setRegulationId(regulation.getRegulationId());
        }

        assetService.save(assetDtoList);
    }

    @Override
    @Transactional
    public void deleteRegulation(Long regulationId) {
    }

    @Override
    @Transactional
    public void saveSingleFile(Regulation regulation, String fileName, String filePath, List<AssetDto> assetDtoList) {

        if (regulation.getRegulationId() != null) {
            deleteListAsset(regulation);
        }
        // format date
        regulation.setStartRegistrationDate(DateUtil.formatDate(regulation.getStartRegistrationDate(), DateUtil.DATE_FORMAT_MINUTE));
        regulation.setEndRegistrationDate(DateUtil.formatDate(regulation.getEndRegistrationDate(), DateUtil.DATE_FORMAT_SECOND_END));
        if (regulation.getPaymentStartTime() != null && regulation.getPaymentStartTime() != null) {
            regulation.setPaymentStartTime(DateUtil.formatDate(regulation.getPaymentStartTime(), DateUtil.DATE_FORMAT_MINUTE));
            regulation.setPaymentEndTime(DateUtil.formatDate(regulation.getPaymentEndTime(), DateUtil.DATE_FORMAT_SECOND_END));
        }

        //save regulation
        regulationRepository.save(regulation);
        if (regulation.getCode() == null) {
            String regulationCode = String.format("QC%06d", regulation.getRegulationId());
            regulation.setCode(regulationCode);
            regulationRepository.saveAndFlush(regulation);
        }

        //save regulation file
        RegulationFile regulationFile = regulationFileRepository.getByRegulationId(DbConstant.REGULATION_FILE_TYPE_QUY_CHE, regulation.getRegulationId());

        if (regulationFile == null) {
            regulationFile = new RegulationFile();
        }
        regulationFile.setRegulationId(regulation.getRegulationId());
        regulationFile.setFilePath(filePath);
        regulationFile.setFileName(fileName);
        regulationFile.setType(DbConstant.REGULATION_FILE_TYPE_QUY_CHE);
        regulationFileRepository.save(regulationFile);

        //save list asset
        for (AssetDto assetDto : assetDtoList) {
            assetDto.setRegulationId(regulation.getRegulationId());
        }
        assetService.save(assetDtoList);
    }

    @Override
    @Transactional
    public void endedRegulation(Long regulationId, Integer auctionStatus) {
        // save regulation
        regulationRepository.changeAuctionStatus(regulationId, auctionStatus, new Date());
        // create regulation_report_file
        if (regulationReportFileRepository.findByRegulationId(regulationId) == null) {
            RegulationReportFile regulationReportFile = new RegulationReportFile();
            regulationReportFile.setRegulationId(regulationId);
            regulationReportFile.setStatus(DbConstant.REGULATION_REPORT_FILE_STATUS_NOT_YET);
            regulationReportFileRepository.save(regulationReportFile);
        }
    }

    @Transactional
    public void deleteListAsset(Regulation regulation) {
        if (regulation.getRegulationId() == null) {
            return;
        }
        List<Asset> list = assetRepository.getAllByRegulationId(regulation.getRegulationId());
        if (list.isEmpty()) {
            return;
        }
        for (Asset asset:list) {
            List<AssetFile> assetFileList = assetFileRepository.getByAssetId(asset.getAssetId());
            if (!assetFileList.isEmpty()) {
                assetFileRepository.deleteAll(assetFileList);
            }
            List<AssetImage> assetImageList = assetImageRepository.findAllByAssetId(asset.getAssetId());
            if (!assetImageList.isEmpty()) {
                assetImageRepository.deleteAll(assetImageList);
            }
            assetRepository.deleteById(asset.getAssetId());
        }
    }
}
