package vn.compedia.website.auction.repository.impl;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.auction.AssetSearchDto;
import vn.compedia.website.auction.dto.auction.AuctionRegisterSearchDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.entity.DashboardDoughnut;
import vn.compedia.website.auction.model.SystemConfig;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;

@Repository
public class AssetRepositoryImpl implements AssetRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AssetImageRepository assetImageRepository;
    @Autowired
    private AssetFileRepository assetFileRepository;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;

    @Override
    public List<AssetDto> search(AssetSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "a.asset_id,"
                + "a.regulation_id,"
                + "GROUP_CONCAT(ai.file_path),"
                + "a.name, "
                + "a.description,"
                + "a.starting_price,"
                + "a.start_time,"
                + "n.number_of_rounds, "
                + "n.time_per_round,"
                + "a.price_step,"
                + "a.status,"
                + "a.serial_number,"
                + "am.ending,"
                + "(SELECT pr.number_of_round FROM price_round pr WHERE a.asset_id = pr.asset_id ORDER BY pr.number_of_round DESC LIMIT 1) AS current_round, "
                + "n.code, "
                + "a.create_date,"
                + "n.auction_status ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY a.asset_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("regulationCode")) {
                sb.append(" n.code ");
            } else if (searchDto.getSortField().equals("name")) {
                sb.append(" a.name collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("createDate")) {
                sb.append(" a.create_date ");
            } else if (searchDto.getSortField().equals("assetId")) {
                sb.append(" a.asset_id ");
            }
            sb.append(searchDto.getSortOrder());
        } else if (searchDto.getRegulationStatus() != null && searchDto.getRegulationStatus() == DbConstant.REGULATION_AUCTION_STATUS_PLAYING) {
            sb.append(" ORDER BY FIELD(a.status, :statusPlaying, :statusWaiting, :statusEnded, :statusNotSuccess, :statusCancel), a.asset_id ASC ");
        } else {
            sb.append(" ORDER BY a.asset_id ASC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);

        // sort :statusPlaying, :statusWaiting, :statusEnded, :statusNotSuccess, :statusCancel
        if (searchDto.getSortField() == null && searchDto.getRegulationStatus() != null
                && searchDto.getRegulationStatus() == DbConstant.REGULATION_AUCTION_STATUS_PLAYING) {
            query.setParameter("statusPlaying", DbConstant.ASSET_STATUS_PLAYING);
            query.setParameter("statusWaiting", DbConstant.ASSET_STATUS_WAITING);
            query.setParameter("statusEnded", DbConstant.ASSET_STATUS_ENDED);
            query.setParameter("statusNotSuccess", DbConstant.ASSET_STATUS_NOT_SUCCESS);
            query.setParameter("statusCancel", DbConstant.ASSET_STATUS_CANCELED);
        }

        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<AssetDto> assetDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            AssetDto dto = new AssetDto();
            dto.setAssetId(ValueUtil.getLongByObject(obj[0]));
            dto.setRegulationId(ValueUtil.getLongByObject(obj[1]));
            if (ValueUtil.getStringByObject(obj[2]) != null) {
                dto.setFilePaths(Arrays.asList(ValueUtil.getStringByObject(obj[2]).split(",")));
            }
            dto.setName(ValueUtil.getStringByObject(obj[3]));
            dto.setDescription(ValueUtil.getStringByObject(obj[4]));
            dto.setStartingPrice(ValueUtil.getLongByObject(obj[5]));
            dto.setNumberOfRounds(ValueUtil.getIntegerByObject(obj[7]));
            dto.setTimePerRound(ValueUtil.getIntegerByObject(obj[8]));
            dto.setStartTime(ValueUtil.getDateByObject(obj[6]));
            dto.setPriceStep(ValueUtil.getLongByObject(obj[9]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[10]));
            dto.setNumericalOrder(ValueUtil.getIntegerByObject(obj[11]));
            dto.setAssetManagementEnding(ValueUtil.getBooleanByObject(obj[12]));
            dto.setCurrentRound(ValueUtil.getIntegerByObject(obj[13]));
            dto.setRegulationCode(ValueUtil.getStringByObject(obj[14]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[15]));
            dto.setAuctionStatus(ValueUtil.getIntegerByObject(obj[16]));
            assetDtoList.add(dto);
        }
        return assetDtoList;
    }

    @Override
    public Long countSearch(AssetSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(T.asset_id) ");
        sb.append(" FROM (SELECT a.asset_id ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY a.asset_id) AS T ");
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    @Override
    public List<AssetDto> searchForGuestUser(AssetSearchDto assetSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * ");
        appendQueryFromAndWhereForSearchGuestUser(sb, assetSearchDto);

        Query query = createQueryObjForUserSearch(sb, assetSearchDto);

        if (assetSearchDto.getPageSize() > 0) {
            query.setFirstResult(assetSearchDto.getPageIndex());
            query.setMaxResults(assetSearchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }
        List<AssetDto> assetDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            AssetDto dto = new AssetDto();
            dto.setAssetId(ValueUtil.getLongByObject(obj[0]));
            dto.setName(ValueUtil.getStringByObject(obj[1]));
            dto.setStartingPrice(ValueUtil.getLongByObject(obj[2]));
            dto.setPriceStep(ValueUtil.getLongByObject(obj[3]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[4]));
            if (obj[5] != null) {
                dto.setFilePaths(Arrays.asList(ValueUtil.getStringByObject(obj[5]).split(",")));
            }
            dto.setStartTime(ValueUtil.getDateByObject(obj[6]));
            dto.setTimePerRound(ValueUtil.getIntegerByObject(obj[7]));
            dto.setNumberOfRounds(ValueUtil.getIntegerByObject(obj[8]));
            dto.setHighestPrice(ValueUtil.getLongByObject(obj[9]));
            dto.setRegulationId(ValueUtil.getLongByObject(obj[10]));
            dto.setRegulationCode(ValueUtil.getStringByObject(obj[11]));
            dto.setRegulationStatus(ValueUtil.getIntegerByObject(obj[12]));
            dto.setAuctionMethodName(ValueUtil.getStringByObject(obj[13]));
            dto.setAuctionFormalityName(ValueUtil.getStringByObject(obj[14]));
            dto.setAuctionStatus(ValueUtil.getIntegerByObject(obj[15]));
            dto.setAuctionMethodId(ValueUtil.getLongByObject(obj[16]));
            dto.setAuctionFormalityId(ValueUtil.getLongByObject(obj[17]));
            dto.setHistoryStatus((ValueUtil.getBooleanByObject(obj[18])));
            dto.setNumericalOrder(ValueUtil.getIntegerByObject(obj[19]));
            dto.setAssetManagementEnding(ValueUtil.getBooleanByObject(obj[20]));
            dto.setMoney(ValueUtil.getLongByObject(obj[21]));
            dto.setStartTime(ValueUtil.getDateByObject(obj[22]));
            dto.setStartRegistrationDate(ValueUtil.getDateByObject(obj[23]));
            dto.setEndRegistrationDate(ValueUtil.getDateByObject(obj[24]));
            dto.setTypeAssetId(ValueUtil.getLongByObject(obj[25]));
            dto.setAuctionRegisterId(ValueUtil.getLongByObject(obj[27]));
            assetDtoList.add(dto);
        }
        return assetDtoList;
    }

    @Override
    public BigInteger countSearchForGuest(AssetSearchDto assetSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(a.asset_id) ");
        appendQueryFromAndWhereForSearchGuestUser(sb, assetSearchDto);
        Query query = createQueryObjForUserSearch(sb, assetSearchDto);
        return (BigInteger) query.getSingleResult();
    }

    @Override
    public List<AssetDto> getAssetForFE(Long regulationId, Long accountId) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT a.asset_id, " +
                "       a.regulation_id, " +
                "       a.name, " +
                "       a.starting_price, " +
                "       a.price_step, " +
                "       (select ass.file_path from asset_image ass where ass.asset_id = a.asset_id order by ass.asset_id asc limit 1), " +
                "       ar.auction_register_id, " +
                "       ar.status as auctionRegisterStatus, " +
                "       a.status " +
                " from asset a " +
                "    left join auction_register ar ON a.asset_id = ar.asset_id AND ar.account_id = :accountId " +
                "    inner join regulation r on a.regulation_id = r.regulation_id and a.regulation_id = :regulationId limit 2");
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("accountId", accountId);
        query.setParameter("regulationId", regulationId);
        List<AssetDto> assetDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            AssetDto dto = new AssetDto();
            dto.setAssetId(ValueUtil.getLongByObject(obj[0]));
            dto.setRegulationId(ValueUtil.getLongByObject(obj[1]));
            dto.setName(ValueUtil.getStringByObject(obj[2]));
            dto.setStartingPrice(ValueUtil.getLongByObject(obj[3]));
            dto.setPriceStep(ValueUtil.getLongByObject(obj[4]));
            dto.setAssetImage(ValueUtil.getStringByObject(obj[5]));
            dto.setAuctionRegisterId(ValueUtil.getLongByObject(obj[6]));
            dto.setAuctionRegisterStatus(ValueUtil.getIntegerByObject(obj[7]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[8]));
            assetDtoList.add(dto);
        }
        return assetDtoList;
    }

    @Override
    public AssetDto searchDetailForGuestUser(AssetSearchDto assetSearchDto) {
        return null;
    }

    @Override
    public AssetDto getAssetDtoByAssetId(AssetSearchDto assetSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "a.asset_id, "
                + "a.name as assetName, "
                + "a.min_price, "
                + "af.name as formalityName, "
                + "am.name as methodName, "
                + "r.regulation_id, "
                + "r.number_of_rounds, "
                + "r.time_per_round, "
                + "a.starting_price, "
                + "a.price_step, "
                + "af.auction_formality_id, "
                + "am.auction_method_id, "
                + "pr.create_date, "
                + "a.description, "
                + "r.code, "
                + "rf.file_path, "
                + "pr.highest_price, "
                + "pr.number_of_round,"
                + "a.status,"
                + "r.start_time as regulationStartTime, "
                + "ar.auction_register_id, "
                + "ar.status as auctionRegisterStatus, "
                + "r.history_status, "
                + "tmp.account_id, "
                + "r.start_registration_date, "
                + "r.end_registration_date, "
                + "a.start_time, "
                + "pr.price_round_id, "
                + "a.serial_number, "
                + "r.retract_price,"
                + "r.payment_start_time,"
                + "r.payment_end_time, "
                + "r.auction_status, "
                + "a.deposit, "
                + "r.status as regulationStatus, "
                + "tmp.asset_management_id "
                + "FROM asset a "
                + "LEFT JOIN auction_register ar ON ar.asset_id = a.asset_id AND ar.account_id = :accountId "
                + "LEFT JOIN (SELECT m.asset_id, m.asset_management_id, ar1.account_id FROM asset_management m INNER JOIN auction_register ar1 ON m.auction_register_id = ar1.auction_register_id) tmp ON tmp.asset_id = a.asset_id "
                + "LEFT JOIN asset_file f ON f.asset_id = a.asset_id AND f.type = :assetFileType "
                + "INNER JOIN regulation r ON r.regulation_id = a.regulation_id "
                + "INNER JOIN regulation_file rf ON rf.regulation_id = r.regulation_id "
                + "INNER JOIN auction_formality af ON af.auction_formality_id = r.auction_formality_id "
                + "INNER JOIN auction_method am ON am.auction_method_id = r.auction_method_id "
                + "LEFT JOIN price_round pr ON pr.price_round_id = (SELECT pr.price_round_id FROM price_round pr WHERE pr.asset_id = a.asset_id ORDER BY pr.price_round_id DESC LIMIT 1) "
                + "WHERE 1 = 1 AND rf.type = :typeRegulation ");
        if (assetSearchDto.getAssetId() != null) {
            sb.append("AND a.asset_id = :assetId ");
        }
        Query query = entityManager.createNativeQuery(sb.toString());
        if (assetSearchDto.getAssetId() != null) {
            query.setParameter("assetId", assetSearchDto.getAssetId());
        }

        query.setParameter("accountId", assetSearchDto.getAccountId());
        query.setParameter("typeRegulation", DbConstant.REGULATION_FILE_TYPE_QUY_CHE);
        query.setParameter("assetFileType", DbConstant.ASSET_FILE_TYPE_ATTACH);
        AssetDto assetDto = new AssetDto();
        List<Object[]> objTemp = query.getResultList();
        if (objTemp.isEmpty()) {
            return null;
        }
        Object[] obj = objTemp.get(0);

        assetDto.setAssetId(ValueUtil.getLongByObject(obj[0]));
        assetDto.setName(ValueUtil.getStringByObject(obj[1]));
        assetDto.setMinPrice(ValueUtil.getLongByObject(obj[2]));
        assetDto.setAuctionFormalityName(ValueUtil.getStringByObject(obj[3]));
        assetDto.setAuctionMethodName(ValueUtil.getStringByObject(obj[4]));
        assetDto.setRegulationId(ValueUtil.getLongByObject(obj[5]));
        assetDto.setNumberOfRounds(ValueUtil.getIntegerByObject(obj[6]));
        assetDto.setTimePerRound(ValueUtil.getIntegerByObject(obj[7]));
        assetDto.setStartingPrice(ValueUtil.getLongByObject(obj[8]));
        assetDto.setPriceStep(ValueUtil.getLongByObject(obj[9]));
        assetDto.setAuctionFormalityId(ValueUtil.getLongByObject(obj[10]));
        assetDto.setAuctionMethodId(ValueUtil.getLongByObject(obj[11]));
        assetDto.setDescription(ValueUtil.getStringByObject(obj[13]));
        assetDto.setRegulationCode(ValueUtil.getStringByObject(obj[14]));
        assetDto.setFilePath(ValueUtil.getStringByObject(obj[15]));
        assetDto.setHighestPrice(ValueUtil.getLongByObject(obj[16]));
        assetDto.setCurrentRound(ValueUtil.getIntegerByObject(obj[17]));
        assetDto.setStatus(ValueUtil.getIntegerByObject(obj[18]));
        assetDto.setRegulationStartTime(ValueUtil.getDateByObject(obj[19]));
        assetDto.setAuctionRegisterId(ValueUtil.getLongByObject(obj[20]));
        assetDto.setAuctionRegisterStatus(ValueUtil.getIntegerByObject(obj[21]));
        assetDto.setHistoryStatus(ValueUtil.getBooleanByObject(obj[22]));
        assetDto.setAccountIdWinner(ValueUtil.getLongByObject(obj[23]));
        assetDto.setStartRegistrationDate(ValueUtil.getDateByObject(obj[24]));
        assetDto.setEndRegistrationDate(ValueUtil.getDateByObject(obj[25]));
        assetDto.setStartTime(ValueUtil.getDateByObject(obj[26]));
        assetDto.setPriceRoundId(ValueUtil.getLongByObject(obj[27]));
        assetDto.setNumericalOrder(ValueUtil.getIntegerByObject(obj[28]));
        assetDto.setRetractPrice(ValueUtil.getBooleanByObject(obj[29]));
        assetDto.setPaymentStartTime(ValueUtil.getDateByObject(obj[30]));
        assetDto.setPaymentEndTime(ValueUtil.getDateByObject(obj[31]));
        assetDto.setAuctionStatus(ValueUtil.getIntegerByObject(obj[32]));
        assetDto.setDeposit(ValueUtil.getLongByObject(obj[33]));
        assetDto.setRegulationStatus(ValueUtil.getIntegerByObject(obj[34]));
        assetDto.setAssetManagementId(ValueUtil.getLongByObject(obj[35]));
        if (ValueUtil.getDateByObject(obj[12]) != null) {
            assetDto.setEndTime(DateUtil.plusMinute(ValueUtil.getDateByObject(obj[12]), assetDto.getTimePerRound()));
        }

        // get asset file
        assetDto.setAssetFileList(assetFileRepository.findAllByAssetIdAndType(assetDto.getAssetId(), DbConstant.ASSET_FILE_TYPE_ATTACH));
        // get asset image
        assetDto.setAssetImageList(assetImageRepository.findAllByAssetId(assetDto.getAssetId()));
        // get auction register
        assetDto.setAuctionRegisterList(auctionRegisterRepository.findAllByAssetIdAndStatus(assetDto.getAssetId(), DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED));
        return assetDto;
    }

    @Override
    public DashboardDoughnut dashboard() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "COUNT(*) as total, "
                + "SUM(CASE WHEN r.auction_status = :statusWaiting then 1 else 0 end) AS totalWaiting, "
                + "SUM(CASE WHEN r.auction_status = :statusPlaying then 1 else 0 end) AS totalPlaying, "
                + "SUM(CASE WHEN r.auction_status = :statusEnded then 1 else 0 end) AS totalEnded, "
                + "SUM(CASE WHEN r.auction_status = :statusCanceled then 1 else 0 end) AS totalCanceled "
                + "FROM regulation r where r.auction_status <> :statusNotHappen ");

        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("statusWaiting", DbConstant.REGULATION_AUCTION_STATUS_WAITING);
        query.setParameter("statusPlaying", DbConstant.REGULATION_AUCTION_STATUS_PLAYING);
        query.setParameter("statusEnded", DbConstant.REGULATION_AUCTION_STATUS_ENDED);
        query.setParameter("statusCanceled", DbConstant.REGULATION_AUCTION_STATUS_CANCELLED);
        query.setParameter("statusNotHappen", DbConstant.REGULATION_AUCTION_STATUS_NOT_HAPPEN);

        DashboardDoughnut dashboardDoughnut = new DashboardDoughnut();
        List<Object[]> objTemp = query.getResultList();
        Object[] obj = objTemp.get(0);
        dashboardDoughnut.setTotal(ValueUtil.getIntegerByObject(obj[0]));
        dashboardDoughnut.setTotalWaiting(ValueUtil.getIntegerByObject(obj[1]));
        dashboardDoughnut.setTotalPlaying(ValueUtil.getIntegerByObject(obj[2]));
        dashboardDoughnut.setTotalEnded(ValueUtil.getIntegerByObject(obj[3]));
        dashboardDoughnut.setTotalCanceled(ValueUtil.getIntegerByObject(obj[4]));
        return dashboardDoughnut;
    }

    @Override
    public List<AssetDto> searchForReportAuction(Long regulationId) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "a.asset_id,"
                + "a.regulation_id,"
                + "a.name, "
                + "a.description,"
                + "a.starting_price,"
                + "a.start_time,"
                + "n.number_of_rounds, "
                + "n.time_per_round,"
                + "a.price_step,"
                + "a.status,"
                + "a.serial_number,"
                + "am.ending,"
                + "(SELECT pr.number_of_round FROM price_round pr WHERE a.asset_id = pr.asset_id ORDER BY pr.number_of_round DESC LIMIT 1) AS current_round, "
                + "n.code, "
                + "a.create_date, "
                + "acc.is_org, "
                + "acc.org_name, "
                + "acc.full_name, "
                + "am.money, "
                + "am.end_time, "
                + "rr.status as 'status_report', "
                + "rr.file_path as 'file_path_report', "
                + "rr.file_name as 'file_name_report' "
                + " FROM asset a " +
                "INNER JOIN regulation n ON n.regulation_id = a.regulation_id " +
                "LEFT JOIN asset_management am ON am.asset_id = a.asset_id " +
                "LEFT JOIN auction_register ar ON ar.auction_register_id = am.auction_register_id " +
                "LEFT JOIN account acc ON acc.account_id = ar.account_id " +
                "LEFT JOIN regulation_report_file rf ON rf.regulation_id = n.regulation_id " +
                "LEFT JOIN regulation_report_user rr ON ( rf.regulation_report_file_id = rr.regulation_report_file_id and acc.account_id = rr.create_by and a.asset_id = rr.asset_id ) " +
                "WHERE 1 = 1 AND a.regulation_id = :regulation_id ORDER BY a.asset_id asc");
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("regulation_id", regulationId);
        List<AssetDto> assetDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            AssetDto dto = new AssetDto();
            dto.setAssetId(ValueUtil.getLongByObject(obj[0]));
            dto.setRegulationId(ValueUtil.getLongByObject(obj[1]));
            dto.setName(ValueUtil.getStringByObject(obj[2]));
            dto.setDescription(ValueUtil.getStringByObject(obj[3]));
            dto.setStartingPrice(ValueUtil.getLongByObject(obj[4]));
            dto.setStartTime(ValueUtil.getDateByObject(obj[5]));
            dto.setNumberOfRounds(ValueUtil.getIntegerByObject(obj[6]));
            dto.setTimePerRound(ValueUtil.getIntegerByObject(obj[7]));
            dto.setPriceStep(ValueUtil.getLongByObject(obj[8]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[9]));
            dto.setNumericalOrder(ValueUtil.getIntegerByObject(obj[10]));
            dto.setAssetManagementEnding(ValueUtil.getBooleanByObject(obj[11]));
            dto.setCurrentRound(ValueUtil.getIntegerByObject(obj[12]));
            dto.setRegulationCode(ValueUtil.getStringByObject(obj[13]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[14]));
            dto.setOrg(ValueUtil.getBooleanByObject(obj[15]));
            if (ValueUtil.getBooleanByObject(obj[15])) {
                dto.setFullNameRegister(ValueUtil.getStringByObject(obj[16]));
            } else {
                dto.setFullNameRegister(ValueUtil.getStringByObject(obj[17]));
            }
            dto.setWinPrice(ValueUtil.getLongByObject(obj[18]));
            dto.setTimeRecorded(ValueUtil.getDateByObject(obj[19]));
            dto.setStatusSigned(ValueUtil.getIntegerByObject(obj[20]));
            dto.setFilePathReport(ValueUtil.getStringByObject(obj[21]));
            dto.setFileNameReport(ValueUtil.getStringByObject(obj[22]));
            assetDtoList.add(dto);
        }
        return assetDtoList;
    }

    private void appendQueryFromAndWhereForSearchGuestUser(StringBuilder sb, AssetSearchDto searchDto) {
        sb.append(" FROM (select a.asset_id," +
                "a.name," +
                " a.starting_price," +
                " a.price_step," +
                "a.status," +
                "GROUP_CONCAT(ai.file_path)," +
                "a.start_time," +
                "re.time_per_round," +
                "re.number_of_rounds," +
                "pr.highest_price," +
                "re.regulation_id," +
                "re.code," +
                "re.status as regulationStatus," +
                "aum.name as aumName," +
                "auf.name as aufName," +
                "re.auction_status, " +
                "re.auction_method_id, " +
                "re.auction_formality_id," +
                "re.history_status," +
                "a.serial_number, " +
                "am.ending as ending," +
                "am.money," +
                "a.start_time as assetStartTime," +
                "re.start_registration_date," +
                "re.end_registration_date, " +
                "a.type_asset_id, " +
                "re.start_time as regulationStartTime, " +
                "ar.auction_register_id " +
                " FROM asset a " +
                " INNER JOIN asset_image ai ON a.asset_id = ai.asset_id " +
                " LEFT JOIN asset_management am ON a.asset_id = am.asset_id " +
                " LEFT JOIN auction_register ar ON a.asset_id = ar.asset_id AND ar.account_id = :accountId " +
                " INNER join (regulation re inner join auction_method aum on re.auction_method_id = aum.auction_method_id inner join auction_formality auf on re.auction_formality_id = auf.auction_formality_id)" +
                " on re.regulation_id  = a.regulation_id " +
                " LEFT JOIN price_round pr ON pr.price_round_id = (SELECT pr.price_round_id FROM price_round pr WHERE pr.asset_id = a.asset_id ORDER BY pr.price_round_id DESC LIMIT 1)" +
                " group by a.asset_id) a " +
                " WHERE 1 = 1 ");
        if (StringUtils.isNotBlank(searchDto.getName())) {
            sb.append(" and a.name like :name");
        }
        if (searchDto.getRegulationStatus() != null) {
            sb.append(" and a.regulationStatus <> :regulationStatus");
        }
        if (searchDto.getRegulationId() != null) {
            sb.append(" and a.regulation_id =:regulationId");
        }
        if (searchDto.getTypeAssetId() != null) {
            sb.append(" and a.type_asset_id =:typeAssetId");
        }
        if (searchDto.getAuctionStatus() != null) {
            sb.append(" and a.auction_status in (:auctionStatus)");
        }
        if (searchDto.getAssetId() != null) {
            sb.append(" and a.asset_id = :assetId");
        }
        if (searchDto.getStatus() != null) {
            sb.append(" and a.status in (:status)");
        }
        if (searchDto.getAssetStatus() != null) {
            sb.append(" and a.status =:assetStatus");
        }
        if (searchDto.getFromPrice() != null) {
            sb.append(" and a.starting_price >= :fromPrice ");
        }
        if (searchDto.getToPrice() != null) {
            sb.append(" and a.starting_price <= :toPrice");
        }
        if (searchDto.getFromTime() != null) {
            sb.append(" and a.start_time >= :fromTime ");
        }
        if (searchDto.getToTime() != null) {
            sb.append(" and a.start_time <= :toTime");
        }
        if (searchDto.getFromTime() == null && searchDto.getToTime() != null) {
            sb.append(" and a.start_time <= :toTime ");
        } else if (searchDto.getFromTime() != null && searchDto.getToTime() == null) {
            sb.append(" and a.start_time >= :fromTime ");
        } else if (searchDto.getFromTime() != null && searchDto.getToTime() != null) {
            sb.append(" and a.start_time >= :fromTime and a.start_time <= :toTime ");
        } else
            sb.append(" ");
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" and (a.name like :keyword ");
            if (DbConstant.ASSET_STATUS_1_NFD.contains(searchDto.getKeyword().toLowerCase())) {
                sb.append(" or a.status = :statusWaiting");
            }
            if (DbConstant.ASSET_STATUS_2_NFD.contains(searchDto.getKeyword().toLowerCase())) {
                sb.append(" or a.status = :statusPlaying");
            }
            if (DbConstant.ASSET_STATUS_3_NFD.contains(searchDto.getKeyword().toLowerCase())) {
                sb.append(" or a.status = :statusEnded");
            }
            if (DbConstant.ASSET_STATUS_4_NFD.contains(searchDto.getKeyword().toLowerCase())) {
                sb.append(" or a.status = :statusCanceled");
            }
            sb.append(" )");
        }

        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("serialNumber")) {
                sb.append(" a.serial_number ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" order by a.status ASC, a.ending DESC, a.auction_status ASC, a.serial_number ASC, a.start_time ASC, ending DESC ");
        }
    }

    private Query createQueryObjForUserSearch(StringBuilder sb, AssetSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("accountId", searchDto.getAccountId());
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword().trim() + "%");
            if (DbConstant.ASSET_STATUS_1_NFD.contains(searchDto.getKeyword().toLowerCase())) {
                query.setParameter("statusWaiting", DbConstant.ASSET_STATUS_WAITING);
            }
            if (DbConstant.ASSET_STATUS_2_NFD.contains(searchDto.getKeyword().toLowerCase())) {
                query.setParameter("statusPlaying", DbConstant.ASSET_STATUS_PLAYING);
            }
            if (DbConstant.ASSET_STATUS_3_NFD.contains(searchDto.getKeyword().toLowerCase())) {
                query.setParameter("statusEnded", DbConstant.ASSET_STATUS_ENDED);
            }
            if (DbConstant.ASSET_STATUS_4_NFD.contains(searchDto.getKeyword().toLowerCase())) {
                query.setParameter("statusCanceled", DbConstant.ASSET_STATUS_CANCELED);
            }
        }
        if (searchDto.getAssetId() != null) {
            query.setParameter("assetId", searchDto.getAssetId());
        }
        if (searchDto.getAssetStatus() != null) {
            query.setParameter("assetStatus", searchDto.getAssetStatus());
        }
        if (searchDto.getTypeAssetId() != null) {
            query.setParameter("typeAssetId", searchDto.getTypeAssetId());
        }
        if (searchDto.getStatus() != null) {
            query.setParameter("status", searchDto.getStatus());
        }
        if (searchDto.getRegulationId() != null) {
            query.setParameter("regulationId", searchDto.getRegulationId());
        }
        if (searchDto.getAuctionStatus() != null) {
            query.setParameter("auctionStatus", searchDto.getAuctionStatus());
        }
        if (searchDto.getStartTime() != null) {
            query.setParameter("startTime", searchDto.getStartTime());
        }
        if (searchDto.getAssetId() != null) {
            query.setParameter("assetId", searchDto.getAssetId());
        }
        if (searchDto.getRegulationStatus() != null) {
            query.setParameter("regulationStatus", searchDto.getRegulationStatus());
        }
        if (searchDto.getFromTime() != null) {
            query.setParameter("fromTime", DateUtil.formatFromDate(searchDto.getFromTime()));
        }
        if (searchDto.getToTime() != null) {
            query.setParameter("toTime", DateUtil.formatToDate(searchDto.getToTime()));
        }
        if (searchDto.getFromPrice() != null) {
            query.setParameter("fromPrice", searchDto.getFromPrice());
        }
        if (searchDto.getToPrice() != null) {
            query.setParameter("toPrice", searchDto.getToPrice());
        }
        if (StringUtils.isNotBlank(searchDto.getName())) {
            query.setParameter("name", "%" + searchDto.getName() + "%");
        }
        return query;
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, AssetSearchDto searchDto) {
        sb.append(" FROM asset a " +
                "left JOIN asset_management am ON am.asset_id = a.asset_id " +
                "left JOIN asset_image ai ON a.asset_id = ai.asset_id " +
                "inner JOIN regulation n ON n.regulation_id = a.regulation_id ");
        sb.append(" WHERE 1 = 1 ");
        if (searchDto.getRegulationId() != null) {
            sb.append(" AND a.regulation_id = :regulation_id");
        }
        if (searchDto.getName() != null) {
            sb.append(" AND a.name LIKE :name");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, AssetSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (searchDto.getRegulationId() != null) {
            query.setParameter("regulation_id", searchDto.getRegulationId());
        }
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword() + "%");
        }
        if (searchDto.getStartTime() != null) {
            query.setParameter("startTime", searchDto.getStartTime());
        }
        if (searchDto.getStatus() != null) {
            query.setParameter("status", searchDto.getStatus());
        }
        if (searchDto.getToTime() != null) {
            query.setParameter("toTime", DateUtil.formatDate(searchDto.getToTime(), DateUtil.TO_DATE_FORMAT));
        }
        if (searchDto.getFromTime() != null) {
            query.setParameter("fromTime", DateUtil.formatDate(searchDto.getFromTime(), DateUtil.FROM_DATE_FORMAT));
        }
        if (searchDto.getFromPrice() != null) {
            query.setParameter("fromPrice", searchDto.getFromPrice());
        }
        if (searchDto.getToPrice() != null) {
            query.setParameter("toPrice", searchDto.getToPrice());
        }
        if (StringUtils.isNotBlank(searchDto.getName())) {
            query.setParameter("name", "%" + searchDto.getName().trim() + "%");
        }

        return query;
    }
}
