package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.auction.AuctionRegisterDto;
import vn.compedia.website.auction.dto.auction.AuctionRegisterSearchDto;
import vn.compedia.website.auction.model.AuctionRegisterFile;
import vn.compedia.website.auction.repository.AuctionRegisterRepositoryCustom;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.StringUtil;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Repository
public class AuctionRegisterRepositoryImpl implements AuctionRegisterRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AuctionRegisterDto> search(AuctionRegisterSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "ar.auction_register_id, "
                + "ar.asset_id, "
                + "rl.regulation_id, "
                + "ar.account_id, "
                + "ar.status, "
                + "ar.reason_deposition, "
                + "ar.file_path, "
                + "ar.reason_refuse, "
                + "ar.status_deposit, "
                + "ar.auctioneer_id, "
                + "ar.create_date, "
                + "ar.create_by, "
                + "ar.update_date, "
                + "ar.update_by, "
                + "ac.is_org, "
                + "ac.org_name, "
                + "ac.full_name, "
                + "case when ac1.role_id = 1 then ac1.full_name else 'N/A' end, "
                + "ac.email, "
                + "ac.phone, "
                + "am1.start_time, "
                + "ase.name, "
                + "ase.starting_price, "
                + "rl.code AS regulationCode, "
                + "rl.auction_status, "
                + "ar.status_refund, "
                + "ar.code, "
                + "ase.price_step,"
                + "am.ending, "
                + "b.bid_id, "
                + "ar.status_refuse_win, "
                + "ar.status_joined, "
                + "ar.time_joined, "
                + "ac.org_phone, "
                + "rf.file_path as regulationFilePath, "
                + "GROUP_CONCAT(DISTINCT arf.file_path), "
                + "GROUP_CONCAT(DISTINCT arf.file_name), "
                + "np.amount as amount_reserve, "
                + "np.file_path as file_path_reserve, "
                + "np.file_name as file_name_reserve, "
                + "b1.winner_sn, "
                + "am1.auction_register_id as amAuctionRegisterId, "
                + "rl.payment_end_time,"
                + "ase.status as assetStatus, "
                + "am1.ending as endingFinal, "
                + "am1.asset_management_id, "
                + "ase.is_cancel_playing, "
                + "cmnd.image_card_id_front, "
                + "cmnd.image_card_id_back, "
                + "GROUP_CONCAT(DISTINCT cmnd.accuracy_company_file_path), "
                + "GROUP_CONCAT(DISTINCT cmnd.accuracy_company_file_name) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);

        sb.append("GROUP BY ar.auction_register_id ");

        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equalsIgnoreCase("fullNameNguoiDangKy")) {
                sb.append(" ac.full_name ");
            } else if (searchDto.getSortField().equalsIgnoreCase("email")) {
                sb.append(" ac.email ");
            } else if (searchDto.getSortField().equalsIgnoreCase("phone")) {
                sb.append(" ac.phone ");
            } else if (searchDto.getSortField().equalsIgnoreCase("reasonDeposition")) {
                sb.append(" ar.reason_deposition ");
            } else if (searchDto.getSortField().equalsIgnoreCase("startTime")) {
                sb.append(" rl.start_time ");
            } else if (searchDto.getSortField().equalsIgnoreCase("name")) {
                sb.append(" ase.name ");
            } else if (searchDto.getSortField().equalsIgnoreCase("status")) {
                sb.append(" ar.status ");
            } else if (searchDto.getSortField().equalsIgnoreCase("reasonRefuse")) {
                sb.append(" ar.reason_refuse ");
            } else if (searchDto.getSortField().equalsIgnoreCase("statusDeposit")) {
                sb.append(" ar.status_deposit ");
            } else if (searchDto.getSortField().equalsIgnoreCase("fullNameDauGiaVien")) {
                sb.append(" ac1.full_name ");
            } else if (searchDto.getSortField().equalsIgnoreCase("code")) {
                sb.append(" ar.code ");
            }else if (searchDto.getSortField().equalsIgnoreCase("regulationCode")) {
                    sb.append(" rl.code ");
            } else if (searchDto.getSortField().equalsIgnoreCase("createDate")) {
                sb.append(" ar.create_date ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY ar.auction_register_id DESC ");
        }
        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<AuctionRegisterDto> auctionRegisterList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            AuctionRegisterDto dto = new AuctionRegisterDto();
            dto.setAuctionRegisterId(ValueUtil.getLongByObject(obj[0]));
            dto.setAssetId(ValueUtil.getLongByObject(obj[1]));
            dto.setRegulationId(ValueUtil.getLongByObject(obj[2]));
            dto.setAccountId(ValueUtil.getLongByObject(obj[3]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[4]));
            dto.setReasonDeposition(ValueUtil.getStringByObject(obj[5]));
            dto.setFilePath(ValueUtil.getStringByObject(obj[6]));
            dto.setReasonRefuse(ValueUtil.getStringByObject(obj[7]));
            dto.setStatusDeposit(ValueUtil.getIntegerByObject(obj[8]));
            dto.setAuctioneerId(ValueUtil.getLongByObject(obj[9]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[10]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[11]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[12]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[13]));
            dto.setOrg(ValueUtil.getBooleanByObject(obj[14]));
            if(ValueUtil.getBooleanByObject(obj[14])){
                dto.setFullNameNguoiDangKy(ValueUtil.getStringByObject(obj[15]));
            } else {
                dto.setFullNameNguoiDangKy(ValueUtil.getStringByObject(obj[16]));
            }
            dto.setFullNameDauGiaVien(ValueUtil.getStringByObject(obj[17]));
            dto.setEmail(ValueUtil.getStringByObject(obj[18]));
            dto.setPhone(ValueUtil.getStringByObject(obj[19]));
            dto.setStartTime(ValueUtil.getDateByObject(obj[20]));
            dto.setName(ValueUtil.getStringByObject(obj[21]));
            String shortContent = StringUtils.abbreviate(StringUtil.br2nl(dto.getName()), 50);
            dto.setShortNameAsset(shortContent);
            dto.setStartingPrice(ValueUtil.getLongByObject(obj[22]));
            dto.setRegulationCode(ValueUtil.getStringByObject(obj[23]));
            dto.setAuctionStatus(ValueUtil.getIntegerByObject(obj[24]));
            dto.setStatusRefund(ValueUtil.getIntegerByObject(obj[25]));
            dto.setCode(ValueUtil.getStringByObject(obj[26]));
            dto.setPriceStep(ValueUtil.getLongByObject(obj[27]));
            dto.setEnding(ValueUtil.getBooleanByObject(obj[28]));
            dto.setStatusRetract(ValueUtil.getLongByObject(obj[29]) != null);
            dto.setStatusRefuseWin(ValueUtil.getBooleanByObject(obj[30]));
            dto.setStatusJoined(ValueUtil.getBooleanByObject(obj[31]));
            dto.setTimeJoined(ValueUtil.getDateByObject(obj[32]));
            dto.setOrgPhone(ValueUtil.getStringByObject(obj[33]));
            dto.setRegulationFilePath(ValueUtil.getStringByObject(obj[34]));
            String filePaths = ValueUtil.getStringByObject(obj[35]);
            String fileNames = ValueUtil.getStringByObject(obj[36]);
            if (filePaths != null) {
                List<String> fp = Arrays.asList(filePaths.split(","));
                List<String> fn = Arrays.asList(fileNames.split(","));
                List<AuctionRegisterFile> auctionRegisterFileList = new ArrayList<>();
                for (int i = 0; i < fp.size(); i++) {
                    auctionRegisterFileList.add(new AuctionRegisterFile(null, dto.getAuctionRegisterId(), fn.get(i), fp.get(i)));
                }
                dto.setAuctionRegisterFileList(auctionRegisterFileList);
            }
            dto.setAmountReserve(ValueUtil.getLongByObject(obj[37]));
            dto.setFilePathReserve(ValueUtil.getStringByObject(obj[38]));
            dto.setFileNameReserve(ValueUtil.getStringByObject(obj[39]));
            dto.setWinnerSn(ValueUtil.getIntegerByObject(obj[40]));
            dto.setAssetManagementWinnerId(ValueUtil.getLongByObject(obj[41]));
            dto.setRegulationPaymentEndTime(ValueUtil.getDateByObject(obj[42]));
            dto.setAssetStatus(ValueUtil.getIntegerByObject(obj[43]));
            dto.setAssetManagementEndingFinal(ValueUtil.getBooleanByObject(obj[44]));
            dto.setAssetManagementId(ValueUtil.getLongByObject(obj[45]));
            dto.setAssetCancelPlaying(ValueUtil.getBooleanByObject(obj[46]));
            if (!dto.isOrg()) {
                dto.setImageCardIdFrontPath(ValueUtil.getStringByObject(obj[47]));
                dto.setImageCardIdBackPath(ValueUtil.getStringByObject(obj[48]));
            } else {
                String accuracyCompanyFilePath = ValueUtil.getStringByObject(obj[49]);
                String accuracyCompanyFileName = ValueUtil.getStringByObject(obj[50]);
                if (accuracyCompanyFilePath != null) {
                    String[] filePathList = accuracyCompanyFilePath.split(",");
                    String[] fileNameList = accuracyCompanyFileName.split(",");
                    int i = 0;
                    dto.setAccuracyCompanyFile(new HashMap<>());
                    for (String filePath : filePathList) {
                        dto.getAccuracyCompanyFile().put(filePath, fileNameList[i++]);
                    }
                }
            }
            auctionRegisterList.add(dto);
        }
        return auctionRegisterList;
    }

    @Override
    public BigInteger countSearch(AuctionRegisterSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(T.auction_register_id) ");
        sb.append(" FROM (SELECT ar.auction_register_id ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY ar.auction_register_id) AS T ");
        Query query = createQueryObjForSearch(sb, searchDto);
        return (BigInteger) query.getSingleResult();
    }

    @Override
    public List<AuctionRegisterDto> searchForGuest(AuctionRegisterSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "ai.file_path, "
                + "ac.full_name, "
                + "ase.start_time, "
                + "ase.name, "
                + "ase.starting_price, "
                + "ase.price_step,"
                + "ase.status,"
                + "ac.role_id,"
                + "ar.status as ParticipantStatus,"
                + "ase.asset_id,"
                + "ac.account_id, "
                + "ar.code, "
                + "rl.time_per_round, "
                + "rl.number_of_rounds, "
                + "ase.serial_number, "
                + "rl.code as regulationCode, "
                + "ar.auction_register_id, "
                + "ar.status_deposit, "
                + "ar.status_refuse_win, "
                + "b.status_retract, "
                + "ar.status_joined, "
                + "amg.ending, "
                + "b1.winner_sn, "
                + "ar.file_path as arFilePath ");
        appendQueryFormAndWhereForGuestUserSearch(sb, searchDto);
        sb.append(" GROUP BY ar.auction_register_id ");
        sb.append(" ORDER BY ase.status ASC, ase.start_time ASC ");

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<AuctionRegisterDto> auctionRegisterList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            AuctionRegisterDto dto = new AuctionRegisterDto();
            dto.setAssetImage(Arrays.asList(ValueUtil.getStringByObject(obj[0]).split(",")));
            dto.setFullNameNguoiDangKy(ValueUtil.getStringByObject(obj[1]));
            dto.setStartTime(ValueUtil.getDateByObject(obj[2]));
            dto.setName(ValueUtil.getStringByObject(obj[3]));
            dto.setStartingPrice(ValueUtil.getLongByObject(obj[4]));
            dto.setPriceStep(ValueUtil.getLongByObject(obj[5]));
            dto.setAssetStatus(ValueUtil.getIntegerByObject(obj[6]));
            dto.setRoleId(ValueUtil.getLongByObject(obj[7]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[8]));
            dto.setAssetId(ValueUtil.getLongByObject(obj[9]));
            dto.setAccountId(ValueUtil.getLongByObject(obj[10]));
            dto.setCode(ValueUtil.getStringByObject(obj[11]));
            dto.setTimePerRound(ValueUtil.getIntegerByObject(obj[12]));
            dto.setNumberOfRounds(ValueUtil.getIntegerByObject(obj[13]));
            dto.setSerialNumber(ValueUtil.getIntegerByObject(obj[14]));
            dto.setRegulationCode(ValueUtil.getStringByObject(obj[15]));
            dto.setAuctionRegisterId(ValueUtil.getLongByObject(obj[16]));
            dto.setStatusDeposit(ValueUtil.getIntegerByObject(obj[17]));
            dto.setStatusRefuseWin(ValueUtil.getBooleanByObject(obj[18]));
            dto.setStatusRetract(ValueUtil.getBooleanByObject(obj[19]));
            dto.setStatusJoined(ValueUtil.getBooleanByObject(obj[20]));
            dto.setEnding(ValueUtil.getBooleanByObject(obj[21]));
            dto.setWinnerSn(ValueUtil.getIntegerByObject(obj[22]));
            dto.setFilePath(ValueUtil.getStringByObject(obj[23]));
            auctionRegisterList.add(dto);
        }
        return auctionRegisterList;
    }

    @Override
    public BigInteger countSearchForGuest(AuctionRegisterSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(T.auction_register_id) ");
        sb.append("FROM (SELECT ar.auction_register_id ");
        appendQueryFormAndWhereForGuestUserSearch(sb, searchDto);
        sb.append("GROUP BY ar.auction_register_id) as T ");
        Query query = createQueryObjForSearch(sb, searchDto);
        return (BigInteger) query.getSingleResult();
    }

    private void appendQueryFormAndWhereForGuestUserSearch(StringBuilder sb, AuctionRegisterSearchDto searchDto){
        sb.append(" FROM auction_register ar "
                + "inner JOIN account ac ON ar.account_id = ac.account_id "
                + "INNER JOIN regulation_file rf ON rf.regulation_id = ar.regulation_id AND rf.type = :regulationFileType "
                + "inner JOIN (asset ase inner join (regulation rl "
                + "left join account ac1 on rl.auctioneer_id = ac1.account_id "
                + "inner join auction_formality auf on auf.auction_formality_id = rl.auction_formality_id "
                + "inner join auction_method aum on aum.auction_method_id = rl.auction_method_id) on ase.regulation_id = rl.regulation_id "
                + "inner join asset_image ai on ase.asset_id = ai.asset_id) on ar.asset_id = ase.asset_id "
                + "LEFT JOIN (SELECT * FROM bid b WHERE b.status_retract = :statusRetract) b ON b.asset_id = ar.asset_id AND b.asset_id = ar.asset_id AND ar.auction_register_id = b.auction_register_id "
                + "LEFT JOIN (SELECT * FROM bid b1 WHERE b1.winner_sn <> '') b1 ON b1.asset_id = ar.asset_id AND b1.asset_id = ar.asset_id AND ar.auction_register_id = b1.auction_register_id "
                + "LEFT JOIN asset_management amg ON amg.auction_register_id = ar.auction_register_id "
                + "WHERE 1 = 1 ");
        if (searchDto.getStatusList() != null) {
            sb.append(" and ase.status not in (:statusList) ");
        }
        if (searchDto.getAssetId() != null) {
            sb.append(" and ase.asset_id = :assetId ");
        }
        if (searchDto.getAccountId() != null) {
            sb.append(" and ac.account_id = :accountId ");
        }
        if (searchDto.getStatusDeposit() != null) {
            sb.append(" and ar.status_deposit = :statusDeposit ");
        }
        if (searchDto.getStatus() != null) {
            sb.append(" and ar.status = :status ");
        }
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, AuctionRegisterSearchDto searchDto) {
        sb.append("FROM auction_register ar "
                + "LEFT JOIN accuracy cmnd on cmnd.account_id = ar.account_id "
                + "LEFT JOIN auction_register_file arf ON arf.auction_register_id = ar.auction_register_id "
                + "INNER JOIN account ac ON ar.account_id = ac.account_id "
                + "INNER JOIN regulation_file rf ON rf.regulation_id = ar.regulation_id AND rf.type = :regulationFileType "
                + "INNER JOIN (asset ase inner join (regulation rl left join account ac1 on rl.auctioneer_id = ac1.account_id) on ase.regulation_id = rl.regulation_id) on ar.asset_id = ase.asset_id "
                + "LEFT JOIN asset_management am on ar.auction_register_id = am.auction_register_id and am.ending = 1 "
                + "LEFT JOIN asset_management am1 on ar.asset_id = am1.asset_id "
                + "LEFT JOIN (SELECT * FROM bid b WHERE b.status_retract = :statusRetract) b ON b.asset_id = ar.asset_id AND b.asset_id = ar.asset_id AND ar.auction_register_id = b.auction_register_id "
                + "LEFT JOIN (SELECT * FROM bid b1 WHERE b1.winner_sn <> '') b1 ON b1.asset_id = ar.asset_id AND b1.asset_id = ar.asset_id AND ar.auction_register_id = b1.auction_register_id "
                + "LEFT JOIN notify_payment np ON np.auction_register_id = ar.auction_register_id "
                + "WHERE 1 = 1 ");

        if (searchDto.isAuctionRegisterRefundPrice()) {
            sb.append("AND ar.status_refuse_win = 0 "
                    + "AND (am1.asset_management_id IS NULL OR am1.auction_register_id <> ar.auction_register_id OR am1.ending = false) "
                    + "AND ar.auction_register_id NOT IN (SELECT b.auction_register_id FROM bid b WHERE b.asset_id = ar.asset_id AND b.status_retract = true) "
                    + "AND ar.file_path IS NULL ");
        }
        if (StringUtils.isNotBlank(searchDto.getFullName())) {
            sb.append(" AND (ac.full_name LIKE :fullName OR ac.org_name LIKE :fullName) ");
        }
        if (StringUtils.isNotBlank(searchDto.getEmail())) {
            sb.append(" AND ac.email LIKE :email ");
        }
        if (searchDto.getAssetId() != null) {
            sb.append(" AND ar.asset_id = :assetId ");
        }
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND (ar.code LIKE :keyword OR ac.full_name LIKE :keyword OR ac.org_name LIKE :keyword)");
        }
        if (StringUtils.isNotBlank(searchDto.getPhone())) {
            sb.append(" AND ac.phone LIKE :phone ");
        }
        if (searchDto.getFromDate() != null) {
            sb.append(" and ar.create_date >= :ngayTuNgay ");
        }
        if (searchDto.getToDate() != null) {
            sb.append(" and ar.create_date <= :ngayDenNgay ");
        }
        if (StringUtils.isNotBlank(searchDto.getRegulationCode())) {
            sb.append(" AND rl.code LIKE :regulationCode ");
        }
        if (StringUtils.isNotBlank(searchDto.getName())) {
            sb.append(" AND ase.name LIKE :name ");
        }
        if (searchDto.getRegulationAuctionStatusList() != null) {
            sb.append(" AND rl.auction_status IN (:auctionStatus) ");
        }
        if (searchDto.getRegulationId() != null) {
            sb.append(" and rl.regulation_id = :regulationId ");
        }
        if (searchDto.getStatusDeposit() != null) {
            sb.append(" and ar.status_deposit = :statusDeposit ");
        }

        if (searchDto.getStatus() != null) {
            sb.append(" AND ar.status = :status ");
        }
        if (searchDto.getStatusRefund() != null) {
            sb.append(" and ar.status_refund = :statusRefund ");
        }
        if (searchDto.getAssetStatus() != null && searchDto.getAssetStatusCancel() != null) {
            sb.append(" and ase.status in (:assetStatus, :assetStatusCancel)");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, AuctionRegisterSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("statusRetract", DbConstant.BID_STATUS_RETRACT_TRUE);
        query.setParameter("regulationFileType", DbConstant.REGULATION_FILE_TYPE_QUY_CHE);
        if (searchDto.getRegulationId() != null) {
            query.setParameter("regulationId", searchDto.getRegulationId());
        }
        if(searchDto.getAccountId() != null){
            query.setParameter("accountId",searchDto.getAccountId());
        }
        if(searchDto.getAssetId() != null){
            query.setParameter("assetId",searchDto.getAssetId());
        }
        if(!StringUtils.isBlank(searchDto.getKeyword())){
            query.setParameter("keyword","%"+searchDto.getKeyword().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getFullName())) {
            query.setParameter("fullName", "%" + searchDto.getFullName().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getEmail())) {
            query.setParameter("email", "%" + searchDto.getEmail().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getPhone())) {
            query.setParameter("phone", "%" + searchDto.getPhone().trim() + "%");
        }
        if(searchDto.getFromDate() != null) {
            query.setParameter("ngayTuNgay", DateUtil.formatFromDate(searchDto.getFromDate()));
        }
        if(searchDto.getToDate() != null) {
            query.setParameter("ngayDenNgay", DateUtil.formatToDate(searchDto.getToDate()));
        }
        if (StringUtils.isNotBlank(searchDto.getCode())) {
            query.setParameter("code", "%" + searchDto.getCode().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getRegulationCode())) {
            query.setParameter("regulationCode", "%" + searchDto.getRegulationCode().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getName())) {
            query.setParameter("name", "%" + searchDto.getName().trim() + "%");
        }
        if(searchDto.getAuctionMethodId() != null){
            query.setParameter("auctionMethodId", searchDto.getAuctionMethodId());
        }
        if(searchDto.getStatusList() != null){
            query.setParameter("statusList", searchDto.getStatusList());
        }
        if (searchDto.getRegulationAuctionStatusList() != null) {
            query.setParameter("auctionStatus", searchDto.getRegulationAuctionStatusList());
        }
        if (searchDto.getStatusDeposit() != null) {
            query.setParameter("statusDeposit", searchDto.getStatusDeposit());
        }
        if(searchDto.getStatus() != null) {
            query.setParameter("status", searchDto.getStatus());
        }
        if (searchDto.getStatusRefund() != null) {
            query.setParameter("statusRefund", searchDto.getStatusRefund());
        }
        if (searchDto.getAssetStatus() != null && searchDto.getAssetStatusCancel() != null) {
            query.setParameter("assetStatus", searchDto.getAssetStatus());
            query.setParameter("assetStatusCancel", searchDto.getAssetStatusCancel());
        }
        return query;
    }
}

