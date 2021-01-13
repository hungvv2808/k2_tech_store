package vn.compedia.website.auction.repository.impl;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.dto.regulation.RegulationSearchDto;
import vn.compedia.website.auction.repository.RegulationRepositoryCustom;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class RegulationRepositoryImpl implements RegulationRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private Date now;

    @Override
    public List<RegulationDto> search(RegulationSearchDto searchDto) {
        now = new Date();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + " re.create_date, "
                + " re.update_date, "
                + " re.create_by, "
                + " re.update_by, "
                + " re.regulation_id, "
                + " re.auction_req_id, "
                + " re.auction_formality_id, "
                + " re.code, "
                + " re.start_time, "
                + " re.number_of_rounds, "
                + " re.time_per_round, "
                + " re.start_registration_date, "
                + " re.end_registration_date, "
                + " re.auction_method_id, "
                + " re.history_status, "
                + " re.status, "
                + " re.reason_cancel, "
                + " re.auctioneer_id, "
                + " re.real_start_time, "
                + " re.real_end_time, "
                + " re.auction_status, "
                + " re.end_time, "
                + " re.payment_start_time, "
                + " re.payment_end_time, "
                + " re.retract_price, "
                + " aufo.name as nameformality, "
                + " aume.name as namemethod, "
                + " refl.file_path, "
                + " au.full_name, "
                + " au.org_name, "
                + " au.is_org, "
                + " refl.file_name, "
                + " group_concat(ass.name order by ass.serial_number SEPARATOR', ') ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY re.regulation_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("code")) {
                sb.append(" re.code ");
            } else if (searchDto.getSortField().equals("startTime")) {
                sb.append(" re.start_time ");
            } else if (searchDto.getSortField().equals("numberOfRounds")) {
                sb.append(" aufo.name ");
            } else if (searchDto.getSortField().equals("timePerRound")) {
                sb.append(" aume.name ");
            } else if (searchDto.getSortField().equals("status")) {
                sb.append(" re.status ");
            } else if (searchDto.getSortField().equals("auctionStatus")) {
                sb.append(" case when re.auction_status = 1 then 6 when re.auction_status = 2 then 1 when re.auction_status = 3 then 2 when re.auction_status = 4 then 4 when re.auction_status = 5 then 5 end ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY re.regulation_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<RegulationDto> regulationDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            RegulationDto dto = new RegulationDto();
            dto.setCreateDate(ValueUtil.getDateByObject(obj[0]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[1]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[2]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setRegulationId(ValueUtil.getLongByObject(obj[4]));
            dto.setAuctionReqId(ValueUtil.getLongByObject(obj[5]));
            dto.setAuctionFormalityId(ValueUtil.getLongByObject(obj[6]));
            dto.setCode(ValueUtil.getStringByObject(obj[7]));
            dto.setStartTime(ValueUtil.getDateByObject(obj[8]));
            dto.setNumberOfRounds(ValueUtil.getIntegerByObject(obj[9]));
            dto.setTimePerRound(ValueUtil.getIntegerByObject(obj[10]));
            dto.setStartRegistrationDate(ValueUtil.getDateByObject(obj[11]));
            dto.setEndRegistrationDate(ValueUtil.getDateByObject(obj[12]));
            dto.setAuctionMethodId(ValueUtil.getLongByObject(obj[13]));
            dto.setHistoryStatus(ValueUtil.getBooleanByObject(obj[14]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[15]));
            dto.setReasonCancel(ValueUtil.getStringByObject(obj[16]));
            dto.setAuctioneerId(ValueUtil.getLongByObject(obj[17]));
            dto.setRealStartTime(ValueUtil.getDateByObject(obj[18]));
            dto.setRealEndTime(ValueUtil.getDateByObject(obj[19]));
            dto.setAuctionStatus(ValueUtil.getIntegerByObject(obj[20]));
            dto.setEndTime(ValueUtil.getDateByObject(obj[21]));
            dto.setPaymentStartTime(ValueUtil.getDateByObject(obj[22]));
            dto.setPaymentEndTime(ValueUtil.getDateByObject(obj[23]));
            dto.setRetractPrice(ValueUtil.getBooleanByObject(obj[14]));
            dto.setAuctionFormalityName(ValueUtil.getStringByObject(obj[25]));
            dto.setAuctionMethodName(ValueUtil.getStringByObject(obj[26]));
            dto.setFilePathRegulation(ValueUtil.getStringByObject(obj[27]));
            dto.setNameAccount(ValueUtil.getStringByObject(obj[28]));
            dto.setOrgName(ValueUtil.getStringByObject(obj[29]));
            dto.setOrg(ValueUtil.getBooleanByObject(obj[30]));
            dto.setFileNameRegulation(ValueUtil.getStringByObject(obj[31]));
            dto.setNameAsset(ValueUtil.getStringByObject(obj[32]));
            regulationDtoList.add(dto);
        }
        return regulationDtoList;
    }

    @Override
    public BigInteger countSearch(RegulationSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(T.regulation_id) ");
        sb.append(" FROM (SELECT re.regulation_id ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY re.regulation_id) AS T ");
        Query query = createQueryObjForSearch(sb, searchDto);
        return (BigInteger) query.getSingleResult();
    }

    @Override
    public List<RegulationDto> searchForGuest(RegulationSearchDto regulationSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * ");
        appendQueryFromAndWhereForUserSearch(sb, regulationSearchDto);
        Query query = createQueryForUserSearch(sb, regulationSearchDto);

        if (regulationSearchDto.getPageSize() > 0) {
            query.setFirstResult(regulationSearchDto.getPageIndex());
            query.setMaxResults(regulationSearchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<RegulationDto> regulationDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            RegulationDto dto = new RegulationDto();
            dto.setRegulationId(ValueUtil.getLongByObject(obj[0]));
            dto.setCode(ValueUtil.getStringByObject(obj[1]));
            dto.setStartTime(ValueUtil.getDateByObject(obj[2]));
            dto.setNumberOfRounds(ValueUtil.getIntegerByObject(obj[3]));
            dto.setTimePerRound(ValueUtil.getIntegerByObject(obj[4]));
            dto.setAuctionStatus(ValueUtil.getIntegerByObject(obj[5]));
            dto.setAuctionMethodName(ValueUtil.getStringByObject(obj[6]));
            dto.setAuctionFormalityName(ValueUtil.getStringByObject(obj[7]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[8]));
            dto.setNameAsset(ValueUtil.getStringByObject(obj[9]));
            regulationDtoList.add(dto);
        }
        return regulationDtoList;
    }

    private void appendQueryFromAndWhereForUserSearch(StringBuilder sb, RegulationSearchDto regulationSearchDto) {
        sb.append(" FROM (select re.regulation_id, " +
                "re.code, " +
                "re.start_time, " +
                "re.number_of_rounds, " +
                "re.time_per_round, " +
                "re.auction_status, " +
                "aum.name as auctionMethodName, " +
                "auf.name as auctionFormalityName," +
                "re.status, " +
                "GROUP_CONCAT(asset.name ORDER BY asset.asset_id SEPARATOR ', ') " +
                " from regulation re " +
                " left join account a on re.auctioneer_id = a.account_id " +
                " inner join auction_method aum on re.auction_method_id = aum.auction_method_id " +
                " inner join auction_formality auf on auf.auction_formality_id = re.auction_formality_id " +
                " inner join (auction_req ar inner join account a1 on ar.account_id= a1.account_id) on re.auction_req_id = ar.auction_req_id " +
                " inner join regulation_file ref on re.regulation_id = ref.regulation_id and ref.type= 1 " +
                " inner join asset on asset.regulation_id = re.regulation_id " +
                " group by re.regulation_id ) re " +
                " WHERE 1 = 1 ");
        sb.append(" and re.status <> 0 ");
        if (StringUtils.isNotBlank(regulationSearchDto.getKeyword())) {
            sb.append(" and ( re.code like :keyword ");
            if (DbConstant.ASSIGN_AUCTION_STATUS_1_NFD.contains(regulationSearchDto.getKeyword().toLowerCase())) {
                sb.append(" or re.auction_status = :statusWaiting");
            }
            if (DbConstant.ASSIGN_AUCTION_STATUS_2_NFD.contains(regulationSearchDto.getKeyword().toLowerCase())) {
                sb.append(" or re.auction_status = :statusPlaying");
            }
            if (DbConstant.ASSIGN_AUCTION_STATUS_3_NFD.contains(regulationSearchDto.getKeyword().toLowerCase())) {
                sb.append(" or re.auction_status = :statusEnded");
            }
            if (DbConstant.ASSIGN_AUCTION_STATUS_4_NFD.contains(regulationSearchDto.getKeyword().toLowerCase())) {
                sb.append(" or re.auction_status = :statusCanceled");
            }
            sb.append(")");
        }
        if (regulationSearchDto.getStatus() != null) {
            sb.append(" and re.status = :status");
        }
        if (StringUtils.isNotBlank(regulationSearchDto.getCode())) {
            sb.append(" and re.code LIKE :code");
        }
        if (regulationSearchDto.getFromDate() == null && regulationSearchDto.getToDate() != null) {
            sb.append(" and re.start_time <= :ngayDenNgay ");
        } else if (regulationSearchDto.getFromDate() != null && regulationSearchDto.getToDate() == null) {
            sb.append(" and re.start_time >= :ngayTuNgay ");
        } else if (regulationSearchDto.getFromDate() != null && regulationSearchDto.getToDate() != null) {
            sb.append(" and re.start_time >= :ngayTuNgay and re.start_time <= :ngayDenNgay ");
        } else
            sb.append(" ");

        if (StringUtils.isNotBlank(regulationSearchDto.getNameAum())) {
            sb.append(" and re.name LIKE :nameAum ");
        }
        if (StringUtils.isNotBlank(regulationSearchDto.getNameAuf())) {
            sb.append(" and re.name LIKE :nameAuf ");
        }
        if (regulationSearchDto.getRegulationId() != null) {
            sb.append(" and re.regulation_id = :regulationId");
        }
        if (regulationSearchDto.getAuctionStatus() != null) {
            sb.append(" and re.auction_status = :auctionStatus");
        }
        sb.append(" order by re.status ASC, re.auction_status ASC, re.start_time ASC ");
    }

    private Query createQueryForUserSearch(StringBuilder sb, RegulationSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (searchDto.getRegulationId() != null) {
            query.setParameter("regulationId", searchDto.getRegulationId());
        }
        if (searchDto.getAuctionStatus() != null) {
            query.setParameter("auctionStatus", searchDto.getAuctionStatus());
        }
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword().trim() + "%");
            if (DbConstant.ASSIGN_AUCTION_STATUS_1_NFD.contains(searchDto.getKeyword().toLowerCase())) {
                query.setParameter("statusWaiting", DbConstant.REGULATION_AUCTION_STATUS_WAITING);
            }
            if (DbConstant.ASSIGN_AUCTION_STATUS_3_NFD.contains(searchDto.getKeyword().toLowerCase())) {
                query.setParameter("statusEnded", DbConstant.REGULATION_AUCTION_STATUS_ENDED);
            }
            if (DbConstant.ASSIGN_AUCTION_STATUS_4_NFD.contains(searchDto.getKeyword().toLowerCase())) {
                query.setParameter("statusCanceled", DbConstant.REGULATION_AUCTION_STATUS_CANCELLED);
            }
        }
        if (searchDto.getStatus() != null) {
            query.setParameter("status", searchDto.getStatus());
        }
        if (searchDto.getFromDate() != null) {
            query.setParameter("ngayTuNgay", DateUtil.formatFromDate(searchDto.getFromDate()));
        }
        if (searchDto.getToDate() != null) {
            query.setParameter("ngayDenNgay", DateUtil.formatToDate(searchDto.getToDate()));
        }
        if (StringUtils.isNotBlank(searchDto.getCode())) {
            query.setParameter("code", "%" + searchDto.getCode().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getNameAuf())) {
            query.setParameter("nameAuf", "%" + searchDto.getNameAuf().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getNameAum())) {
            query.setParameter("nameAum", "%" + searchDto.getNameAum().trim() + "%");
        }
        return query;
    }

    @Override
    public BigInteger countSearchForGuest(RegulationSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(re.regulation_id) ");
        appendQueryFromAndWhereForUserSearch(sb, searchDto);
        Query query = createQueryForUserSearch(sb, searchDto);
        return (BigInteger) query.getSingleResult();
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, RegulationSearchDto searchDto) {
        sb.append("FROM regulation re " +
                " inner join regulation_file refl ON re.regulation_id = refl.regulation_id and refl.type = 1 " +
                " inner join asset ass ON re.regulation_id = ass.regulation_id " +
                " inner join auction_req aur ON re.auction_req_id = aur.auction_req_id " +
                " left join account au ON au.account_id = aur.account_id " +
                " inner join auction_formality aufo ON aufo.auction_formality_id = re.auction_formality_id " +
                " inner join auction_method aume ON aume.auction_method_id = re.auction_method_id " +
                " WHERE 1 = 1 ");
        if (StringUtils.isNotBlank(searchDto.getCode())) {
            sb.append(" and re.code like :code ");
        }
        if (searchDto.getAuctionReqId() != null) {
            sb.append(" and re.auction_req_id = :auctionReqId");
        }
        if (searchDto.getStartTime() != null) {
            sb.append(" AND re.start_time = :startTime ");
        }
        if (searchDto.getStatus() != null) {
            sb.append(" and re.status = :status ");
        }
        if (searchDto.getAuctionStatus() != null) {
            sb.append(" and re.auction_status = :auctionStatus ");
        }
        if (searchDto.getAccountId() != null) {
            sb.append(" and au.account_id = :accountId ");
        }
        if (StringUtils.isNotBlank(searchDto.getNameAsset())) {
            sb.append(" and ass.name LIKE :name ");
        }
        if (searchDto.getFromDate() == null && searchDto.getToDate() != null) {
            sb.append(" and re.start_time <= :ngayDenNgay ");
        } else if (searchDto.getFromDate() != null && searchDto.getToDate() == null) {
            sb.append(" and re.start_time >= :ngayTuNgay ");
        } else if (searchDto.getFromDate() != null && searchDto.getToDate() != null) {
            sb.append(" and re.start_time >= :ngayTuNgay and re.start_time <= :ngayDenNgay ");
        } else
            sb.append(" ");

       /* if (searchDto.getNgayTuNgay() != null && searchDto.getNgayDenNgay() != null) {
            sb.append(" and (re.start_time between :ngayTuNgay and :ngayDenNgay) ");
        }*/
    }

    private Query createQueryObjForSearch(StringBuilder sb, RegulationSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (searchDto.getAuctionReqId() != null) {
            query.setParameter("auctionReqId", searchDto.getAuctionReqId());
        }
        if (searchDto.getAccountId() != null) {
            query.setParameter("accountId", searchDto.getAccountId());
        }
        if (searchDto.getStartTime() != null) {
            query.setParameter("startTime", searchDto.getStartTime());
        }
        if (searchDto.getStatus() != null) {
            query.setParameter("status", searchDto.getStatus());
        }
        if (searchDto.getAuctionStatus() != null) {
            query.setParameter("auctionStatus", searchDto.getAuctionStatus());
        }
        if (searchDto.getFromDate() != null) {
            query.setParameter("ngayTuNgay", DateUtil.formatFromDate(searchDto.getFromDate()));
        }
        if (searchDto.getToDate() != null) {
            query.setParameter("ngayDenNgay", DateUtil.formatToDate(searchDto.getToDate()));
        }
        if (StringUtils.isNotBlank(searchDto.getCode())) {
            query.setParameter("code", "%" + searchDto.getCode().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getNameAuf())) {
            query.setParameter("nameAuf", "%" + searchDto.getNameAuf().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getNameAum())) {
            query.setParameter("nameAum", "%" + searchDto.getNameAum().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getNameAsset())) {
            query.setParameter("name", "%" + searchDto.getNameAsset().trim() + "%");
        }
  /*      if (searchDto.getDateTo() != null) {
            query.setParameter("dateTo", searchDto.getDateTo());
        }
        if (searchDto.getDateFrom() != null) {
            query.setParameter("dateFrom", searchDto.getDateFrom());
        }*/
        return query;
    }

    @Override
    public List<RegulationDto> searchForAuctionReqId(RegulationSearchDto searchDto, Long auctionReqId) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + " re.create_date, "
                + " re.update_date, "
                + " re.create_by, "
                + " re.update_by, "
                + " re.regulation_id, "
                + " re.auction_req_id, "
                + " re.auction_formality_id, "
                + " re.code, "
                + " re.start_time, "
                + " re.number_of_rounds, "
                + " re.time_per_round, "
                + " re.start_registration_date, "
                + " re.end_registration_date, "
                + " re.auction_method_id, "
                + " re.history_status, "
                + " re.status, "
                + " re.reason_cancel, "
                + " re.auctioneer_id, "
                + " re.real_start_time, "
                + " re.real_end_time, "
                + " re.auction_status, "
                + " re.time_self_of, "
                + " re.end_time, "
                + " re.payment_start_time, "
                + " re.payment_end_time, "
                + " re.retract_price, "
                + " aufo.name as nameformality, "
                + " aume.name as namemethod, "
                + " refl.file_path, "
                + " au.full_name ,"
                + " au.org_name ,"
                + " au.is_org ,"
                + " group_concat(ass.name ORDER BY ass.asset_id SEPARATOR ', ') ");
        appendQueryForSearch(sb, searchDto);
        sb.append(" GROUP BY re.regulation_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("code")) {
                sb.append(" re.code ");
            } else if (searchDto.getSortField().equals("startTime")) {
                sb.append(" re.start_time ");
            } else if (searchDto.getSortField().equals("numberOfRounds")) {
                sb.append(" aufo.name ");
            } else if (searchDto.getSortField().equals("timePerRound")) {
                sb.append(" aume.name ");
            } else if (searchDto.getSortField().equals("status")) {
                sb.append(" re.status ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY re.regulation_id DESC ");
        }

        Query query = createQueryForSearch(sb, searchDto, auctionReqId);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<RegulationDto> regulationDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            RegulationDto dto = new RegulationDto();
            dto.setCreateDate(ValueUtil.getDateByObject(obj[0]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[1]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[2]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setRegulationId(ValueUtil.getLongByObject(obj[4]));
            dto.setAuctionReqId(ValueUtil.getLongByObject(obj[5]));
            dto.setAuctionFormalityId(ValueUtil.getLongByObject(obj[6]));
            dto.setCode(ValueUtil.getStringByObject(obj[7]));
            dto.setStartTime(ValueUtil.getDateByObject(obj[8]));
            dto.setNumberOfRounds(ValueUtil.getIntegerByObject(obj[9]));
            dto.setTimePerRound(ValueUtil.getIntegerByObject(obj[10]));
            dto.setStartRegistrationDate(ValueUtil.getDateByObject(obj[11]));
            dto.setEndRegistrationDate(ValueUtil.getDateByObject(obj[12]));
            dto.setAuctionMethodId(ValueUtil.getLongByObject(obj[13]));
            dto.setHistoryStatus(ValueUtil.getBooleanByObject(obj[14]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[15]));
            dto.setReasonCancel(ValueUtil.getStringByObject(obj[16]));
            dto.setAuctioneerId(ValueUtil.getLongByObject(obj[17]));
            dto.setRealStartTime(ValueUtil.getDateByObject(obj[18]));
            dto.setRealEndTime(ValueUtil.getDateByObject(obj[19]));
            dto.setAuctionStatus(ValueUtil.getIntegerByObject(obj[20]));
            dto.setEndTime(ValueUtil.getDateByObject(obj[21]));
            dto.setPaymentStartTime(ValueUtil.getDateByObject(obj[22]));
            dto.setPaymentEndTime(ValueUtil.getDateByObject(obj[23]));
            dto.setRetractPrice(ValueUtil.getBooleanByObject(obj[24]));
            dto.setAuctionFormalityName(ValueUtil.getStringByObject(obj[25]));
            dto.setAuctionMethodName(ValueUtil.getStringByObject(obj[26]));
            dto.setFilePathRegulation(ValueUtil.getStringByObject(obj[27]));
            dto.setNameAccount(ValueUtil.getStringByObject(obj[28]));
            dto.setOrgName(ValueUtil.getStringByObject(obj[29]));
            dto.setOrg(ValueUtil.getBooleanByObject(obj[30]));
            dto.setNameAsset(ValueUtil.getStringByObject(obj[31]));
            regulationDtoList.add(dto);
        }
        return regulationDtoList;
    }

    private void appendQueryForSearch(StringBuilder sb, RegulationSearchDto searchDto) {
        sb.append("FROM regulation re " +
                " inner join regulation_file refl ON re.regulation_id = refl.regulation_id " +
                " inner join asset ass on ass.regulation_id = re.regulation_id " +
                " left join auction_req aur ON re.auction_req_id = aur.auction_req_id " +
                " left join account au ON au.account_id = aur.account_id " +
                " left join auction_formality aufo ON aufo.auction_formality_id = re.auction_formality_id " +
                " left join auction_method aume ON aume.auction_method_id = re.auction_method_id " +
                " WHERE 1 = 1 and aur.auction_req_id = :auctionReqId");
        if (StringUtils.isNotBlank(searchDto.getCode())) {
            sb.append(" and re.code like :code ");
        }
        if (searchDto.getStatus() != null) {
            sb.append(" and re.status = :status ");
        }
        if (searchDto.getAuctionStatus() != null) {
            sb.append(" and re.auction_status = :auctionStatus ");
        }
        if (searchDto.getAccountId() != null) {
            sb.append(" and au.account_id = :accountId ");
        }
   /*     if (searchDto.getDateFrom() != null) {
            sb.append(" and re.start_time <= :dateFrom ");
        }
        if (searchDto.getDateTo() != null) {
            sb.append(" and re.start_time >= :dateTo ");
        }*/
        if (searchDto.getFromDate() == null && searchDto.getToDate() != null) {
            sb.append(" and re.start_time <= :ngayTuNgay ");
        } else if (searchDto.getFromDate() != null && searchDto.getToDate() == null) {
            sb.append(" and re.start_time >= :ngayDenNgay ");
        } else if (searchDto.getFromDate() != null && searchDto.getToDate() != null) {
            sb.append(" and re.start_time >= :ngayTuNgay and re.start_time <= :ngayDenNgay ");
        } else
            sb.append(" ");


    }

    private Query createQueryForSearch(StringBuilder sb, RegulationSearchDto searchDto, Long auctionReqId) {
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("auctionReqId", auctionReqId);
        if (searchDto.getAccountId() != null) {
            query.setParameter("accountId", searchDto.getAccountId());
        }
        if (searchDto.getStatus() != null) {
            query.setParameter("status", searchDto.getStatus());
        }
        if (searchDto.getAuctionStatus() != null) {
            query.setParameter("auctionStatus", searchDto.getAuctionStatus());
        }
        if (searchDto.getFromDate() != null) {
            query.setParameter("ngayTuNgay", searchDto.getFromDate());
        }
        if (searchDto.getToDate() != null) {
            query.setParameter("ngayDenNgay", searchDto.getToDate());
        }
        if (StringUtils.isNotBlank(searchDto.getCode())) {
            query.setParameter("code", "%" + searchDto.getCode().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getNameAuf())) {
            query.setParameter("nameAuf", "%" + searchDto.getNameAuf().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getNameAum())) {
            query.setParameter("nameAum", "%" + searchDto.getNameAum().trim() + "%");
        }
    /*    if (searchDto.getDateTo() != null) {
            query.setParameter("dateTo", searchDto.getDateTo());
        }
        if (searchDto.getDateFrom() != null) {
            query.setParameter("dateFrom", searchDto.getDateFrom());
        }*/
        return query;
    }

    @Override
    public BigInteger countSearchForAuctionReqId(RegulationSearchDto searchDto, Long auctionReqId) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(T.regulation_id) ");
        sb.append(" FROM (SELECT re.regulation_id ");
        appendQueryForSearch(sb, searchDto);
        sb.append(" GROUP BY re.regulation_id) AS T ");
        Query query = createQueryForSearch(sb, searchDto, auctionReqId);
        return (BigInteger) query.getSingleResult();
    }

    public String getAssetName(Long regulationId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select ass.name from asset ass where ass.regulation_id = :regulationId ");
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("regulationId", regulationId);
        List<String> list = query.getResultList();
        String result = "";
        for (String str : list) {
            result = result + str + ", ";
        }
        return result;
    }

    //Phục vụ cho biên bản cuộc đấu giá
    @Override
    public List<RegulationDto> searchForReport(RegulationSearchDto searchDto, Integer typeFile) {
        now = new Date();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + " re.create_date, "
                + " re.update_date, "
                + " re.create_by, "
                + " re.update_by, "
                + " re.regulation_id, "
                + " re.auction_req_id, "
                + " re.auction_formality_id, "
                + " re.code, "
                + " re.start_time, "
                + " re.number_of_rounds, "
                + " re.time_per_round, "
                + " re.start_registration_date, "
                + " re.end_registration_date, "
                + " re.auction_method_id, "
                + " re.history_status, "
                + " re.status, "
                + " re.reason_cancel, "
                + " re.auctioneer_id, "
                + " re.real_start_time, "
                + " re.real_end_time, "
                + " re.auction_status, "
                + " re.end_time, "
                + " re.payment_start_time, "
                + " re.payment_end_time, "
                + " re.retract_price, "
                + " aufo.name as nameformality, "
                + " aume.name as namemethod, "
                + " au.is_org, "
                + " au.full_name, "
                + " au.org_name, "
                + " refl.file_path, "
                + " refl.file_name, "
                + " group_concat(ass.name order by ass.serial_number SEPARATOR', '), "
                + " refl.regulation_report_file_id, "
                + " refl.status as 'regulation_report_file_status' ");
        appendQueryForReport(sb, searchDto);
        sb.append(" GROUP BY re.regulation_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("code")) {
                sb.append(" re.code ");
            } else if (searchDto.getSortField().equals("auctionStatus")) {
                sb.append(" re.auction_status ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY re.regulation_id DESC ");
        }

        Query query = createQueryForReport(sb, searchDto, typeFile);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<RegulationDto> regulationDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            RegulationDto dto = new RegulationDto();
            dto.setCreateDate(ValueUtil.getDateByObject(obj[0]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[1]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[2]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setRegulationId(ValueUtil.getLongByObject(obj[4]));
            dto.setAuctionReqId(ValueUtil.getLongByObject(obj[5]));
            dto.setAuctionFormalityId(ValueUtil.getLongByObject(obj[6]));
            dto.setCode(ValueUtil.getStringByObject(obj[7]));
            dto.setStartTime(ValueUtil.getDateByObject(obj[8]));
            dto.setNumberOfRounds(ValueUtil.getIntegerByObject(obj[9]));
            dto.setTimePerRound(ValueUtil.getIntegerByObject(obj[10]));
            dto.setStartRegistrationDate(ValueUtil.getDateByObject(obj[11]));
            dto.setEndRegistrationDate(ValueUtil.getDateByObject(obj[12]));
            dto.setAuctionMethodId(ValueUtil.getLongByObject(obj[13]));
            dto.setHistoryStatus(ValueUtil.getBooleanByObject(obj[14]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[15]));
            dto.setReasonCancel(ValueUtil.getStringByObject(obj[16]));
            dto.setAuctioneerId(ValueUtil.getLongByObject(obj[17]));
            dto.setRealStartTime(ValueUtil.getDateByObject(obj[18]));
            dto.setRealEndTime(ValueUtil.getDateByObject(obj[19]));
            dto.setAuctionStatus(ValueUtil.getIntegerByObject(obj[20]));
            dto.setEndTime(ValueUtil.getDateByObject(obj[21]));
            dto.setPaymentStartTime(ValueUtil.getDateByObject(obj[22]));
            dto.setPaymentEndTime(ValueUtil.getDateByObject(obj[23]));
            dto.setRetractPrice(ValueUtil.getBooleanByObject(obj[24]));
            dto.setAuctionFormalityName(ValueUtil.getStringByObject(obj[25]));
            dto.setAuctionMethodName(ValueUtil.getStringByObject(obj[26]));
            dto.setOrg(ValueUtil.getBooleanByObject(obj[27]));
            if (!ValueUtil.getBooleanByObject(obj[27])) {
                dto.setNameOfUser(ValueUtil.getStringByObject(obj[28]));
            } else {
                dto.setNameOfUser(ValueUtil.getStringByObject(obj[29]));
            }
            dto.setFilePathRegulation(ValueUtil.getStringByObject(obj[30]));
            dto.setFileNameRegulation(ValueUtil.getStringByObject(obj[31]));
            dto.setNameAsset(ValueUtil.getStringByObject(obj[32]));
            dto.setRegulationReportFileId(ValueUtil.getLongByObject(obj[33]));
            dto.setRegulationReportFileStatus(ValueUtil.getIntegerByObject(obj[34]));
            regulationDtoList.add(dto);
        }
        return regulationDtoList;
    }

    @Override
    public BigInteger countSearchForReport(RegulationSearchDto searchDto, Integer typeFile) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(T.regulation_id) ");
        sb.append(" FROM (SELECT re.regulation_id ");
        appendQueryForReport(sb, searchDto);
        sb.append(" GROUP BY re.regulation_id) AS T ");
        Query query = createQueryForReport(sb, searchDto, typeFile);
        return (BigInteger) query.getSingleResult();
    }

    private void appendQueryForReport(StringBuilder sb, RegulationSearchDto searchDto) {
        sb.append("FROM regulation re " +
                " inner join asset ass ON re.regulation_id = ass.regulation_id " +
                " inner join auction_req aur ON re.auction_req_id = aur.auction_req_id " +
                " left join account au ON au.account_id = aur.account_id " +
                " inner join auction_formality aufo ON aufo.auction_formality_id = re.auction_formality_id " +
                " inner join auction_method aume ON aume.auction_method_id = re.auction_method_id " +
                " left join regulation_report_file refl ON re.regulation_id = refl.regulation_id " +
                " WHERE 1 = 1 ");
        sb.append(" and (re.auction_status = :auctionStatusEnded or re.auction_status = :auctionStatusCanceled) ");
        if (searchDto.getRegulationId() != null) {
            sb.append(" and re.regulation_id = :regulationId ");
        }
        if (searchDto.getAuctionFormalityId() != null) {
            sb.append(" AND aufo.auction_formality_id = :auctionFormalityId ");
        }
        if (searchDto.getAuctionMethodId() != null) {
            sb.append(" and aume.auction_method_id = :auctionMethodId ");
        }
        if (searchDto.getAuctionStatus() != null) {
            sb.append(" and re.auction_status = :auctionStatus ");
        }
        if (searchDto.getFromDate() != null) {
            sb.append(" and re.start_time >= :fromDate ");
        }
        if (searchDto.getToDate() != null) {
            sb.append(" and re.start_time <= :toDate ");
        }
    }

    private Query createQueryForReport(StringBuilder sb, RegulationSearchDto searchDto, Integer typeFile) {
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("auctionStatusEnded", searchDto.getAuctionStatusEnded());
        query.setParameter("auctionStatusCanceled", searchDto.getAuctionStatusCanceled());
        if (searchDto.getRegulationId() != null) {
            query.setParameter("regulationId", searchDto.getRegulationId());
        }
        if (searchDto.getAuctionFormalityId() != null) {
            query.setParameter("auctionFormalityId", searchDto.getAuctionFormalityId());
        }
        if (searchDto.getAuctionMethodId() != null) {
            query.setParameter("auctionMethodId", searchDto.getAuctionMethodId());
        }
        if (searchDto.getAuctionStatus() != null) {
            query.setParameter("auctionStatus", searchDto.getAuctionStatus());
        }
        if (searchDto.getFromDate() != null ) {
            query.setParameter("fromDate", DateUtil.formatDate(searchDto.getFromDate(), DateUtil.FROM_DATE_FORMAT));
        }
        if (searchDto.getToDate() != null) {
            query.setParameter("toDate", DateUtil.formatDate(searchDto.getToDate(), DateUtil.TO_DATE_FORMAT));
        }
        return query;
    }

    @Override
    public RegulationDto getRegulationDto(Long regulationId, int auctionStatus) {
        StringBuilder sb = new StringBuilder("");
        sb.append("select  re.regulation_id        as col_0_0_, " +
                "        re.code                 as col_1_0_, " +
                "        re.auctioneer_id        as col_2_0_, " +
                "        re.auction_formality_id as col_3_0_, " +
                "        re.auction_method_id    as col_4_0_, " +
                "        re.start_time           as col_5_0_, " +
                "        re.number_of_rounds     as col_6_0_, " +
                "        re.time_per_round       as col_7_0_, " +
                "        re.real_end_time        as col_8_0_, " +
                "        re.auction_status       as col_9_0_, " +
                "        af.name                 as col_10_0_, " +
                "        am.name                 as col_11_0_, " +
                "        acc.full_name           as col_12_0_ " +
                "        from regulation  re " +
                "        inner join auction_formality af on re.auction_formality_id = af.auction_formality_id " +
                "        inner join auction_method am on re.auction_method_id = am.auction_method_id " +
                "        left join account acc on  re.auctioneer_id = acc.account_id " +
                "        where re.regulation_id = :regulationId and re.auction_status = :auctionStatus ");

        Query query = entityManager.createNativeQuery(sb.toString());

        query.setParameter("regulationId", regulationId);
        query.setParameter("auctionStatus", auctionStatus);

        Object[] obj = (Object[]) query.getSingleResult();

        RegulationDto dto = new RegulationDto();
        dto.setRegulationId(ValueUtil.getLongByObject(obj[0]));
        dto.setCode(ValueUtil.getStringByObject(obj[1]));
        dto.setAuctioneerId(ValueUtil.getLongByObject(obj[2]));
        dto.setAuctionFormalityId(ValueUtil.getLongByObject(obj[3]));
        dto.setAuctionMethodId(ValueUtil.getLongByObject(obj[4]));
        dto.setStartTime(ValueUtil.getDateByObject(obj[5]));
        dto.setNumberOfRounds(ValueUtil.getIntegerByObject(obj[6]));
        dto.setTimePerRound(ValueUtil.getIntegerByObject(obj[7]));
        dto.setRealEndTime(ValueUtil.getDateByObject(obj[8]));
        dto.setAuctionStatus(ValueUtil.getIntegerByObject(obj[9]));
        dto.setAuctionFormalityName(ValueUtil.getStringByObject(obj[10]));
        dto.setAuctionMethodName(ValueUtil.getStringByObject(obj[11]));
        dto.setNameAccount(ValueUtil.getStringByObject(obj[12]));
        return dto;
    }

    @Override
    public RegulationDto getRegulationDtoByRegulationId(Long regulationId) {
        StringBuilder sb = new StringBuilder("");
        sb.append("select r.create_date             as col_0_0_," +
                "       r.update_date             as col_1_0_," +
                "       r.create_by               as col_2_0_," +
                "       r.update_by               as col_3_0_," +
                "       r.regulation_id           as col_4_0_," +
                "       r.auction_req_id          as col_5_0_," +
                "       r.auction_formality_id    as col_6_0_," +
                "       r.code                    as col_7_0_," +
                "       r.start_time              as col_8_0_," +
                "       r.number_of_rounds        as col_9_0_," +
                "       r.time_per_round          as col_10_0_," +
                "       r.start_registration_date as col_11_0_," +
                "       r.end_registration_date   as col_12_0_," +
                "       r.auction_method_id       as col_13_0_," +
                "       r.history_status          as col_14_0_," +
                "       r.status                  as col_15_0_," +
                "       r.reason_cancel           as col_16_0_," +
                "       r.auctioneer_id           as col_17_0_," +
                "       r.real_start_time         as col_18_0_," +
                "       r.real_end_time           as col_19_0_," +
                "       r.auction_status          as 'status'," +
                "       r.end_time                as col_21_0_," +
                "       r.payment_start_time      as col_22_0_," +
                "       r.payment_end_time        as col_23_0_," +
                "       r.retract_price           as col_24_0_ " +
                "       from regulation r where r.regulation_id = :regulationId ");

        Query query = entityManager.createNativeQuery(sb.toString());

        query.setParameter("regulationId", regulationId);
        Object[] obj = (Object[]) query.getSingleResult();

        RegulationDto dto = new RegulationDto();
        dto.setCreateDate(ValueUtil.getDateByObject(obj[0]));
        dto.setUpdateDate(ValueUtil.getDateByObject(obj[1]));
        dto.setCreateBy(ValueUtil.getLongByObject(obj[2]));
        dto.setUpdateBy(ValueUtil.getLongByObject(obj[3]));
        dto.setRegulationId(ValueUtil.getLongByObject(obj[4]));
        dto.setAuctionReqId(ValueUtil.getLongByObject(obj[5]));
        dto.setAuctionFormalityId(ValueUtil.getLongByObject(obj[6]));
        dto.setCode(ValueUtil.getStringByObject(obj[7]));
        dto.setStartTime(ValueUtil.getDateByObject(obj[8]));
        dto.setNumberOfRounds(ValueUtil.getIntegerByObject(obj[9]));
        dto.setTimePerRound(ValueUtil.getIntegerByObject(obj[10]));
        dto.setStartRegistrationDate(ValueUtil.getDateByObject(obj[11]));
        dto.setEndRegistrationDate(ValueUtil.getDateByObject(obj[12]));
        dto.setAuctionMethodId(ValueUtil.getLongByObject(obj[13]));
        dto.setHistoryStatus(ValueUtil.getBooleanByObject(obj[14]));
        dto.setStatus(ValueUtil.getIntegerByObject(obj[15]));
        dto.setReasonCancel(ValueUtil.getStringByObject(obj[16]));
        dto.setAuctioneerId(ValueUtil.getLongByObject(obj[17]));
        dto.setRealStartTime(ValueUtil.getDateByObject(obj[18]));
        dto.setRealEndTime(ValueUtil.getDateByObject(obj[19]));
        dto.setAuctionStatus(ValueUtil.getIntegerByObject(obj[20]));
        dto.setEndTime(ValueUtil.getDateByObject(obj[21]));
        dto.setPaymentStartTime(ValueUtil.getDateByObject(obj[22]));
        dto.setPaymentEndTime(ValueUtil.getDateByObject(obj[23]));
        dto.setRetractPrice(ValueUtil.getBooleanByObject(obj[24]));
        return dto;
    }
}

