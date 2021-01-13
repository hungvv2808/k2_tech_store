package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.assign_auction.AssignAuctionDto;
import vn.compedia.website.auction.dto.assign_auction.AssignAuctionSearchDto;
import vn.compedia.website.auction.repository.AssignAuctionRepositoryCustom;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AssignAuctionRepositoryImpl implements AssignAuctionRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AssignAuctionDto> search(AssignAuctionSearchDto regulationSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * ");
        appendQuery(sb, regulationSearchDto);

        Query query = createQuery(sb, regulationSearchDto);
        if (regulationSearchDto.getPageSize() > 0) {
            query.setFirstResult(regulationSearchDto.getPageIndex());
            query.setMaxResults(regulationSearchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<AssignAuctionDto> regulationArrayList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            AssignAuctionDto dto = new AssignAuctionDto();
            dto.setRegulationId(ValueUtil.getLongByObject(obj[0]));
            dto.setAuctionReqId(ValueUtil.getLongByObject(obj[1]));
            dto.setAuctionFormalityId(ValueUtil.getLongByObject(obj[2]));
            dto.setCode(ValueUtil.getStringByObject(obj[3]));
            dto.setStartTime(ValueUtil.getDateByObject(obj[4]));
            dto.setNumberOfRounds(ValueUtil.getIntegerByObject(obj[5]));
            dto.setTimePerRound(ValueUtil.getIntegerByObject(obj[6]));
            dto.setStartRegistrationDate(ValueUtil.getDateByObject(obj[7]));
            dto.setEndRegistrationDate(ValueUtil.getDateByObject(obj[8]));
            dto.setAuctionMethodId(ValueUtil.getLongByObject(obj[9]));
            dto.setHistoryStatus(ValueUtil.getBooleanByObject(obj[10]));
            dto.setAuctionStatus(ValueUtil.getIntegerByObject(obj[11]));
            dto.setAuctioneerId(ValueUtil.getLongByObject(obj[12]));
            dto.setEndTime(ValueUtil.getDateByObject(obj[13]));
            dto.setOrg(ValueUtil.getBooleanByObject(obj[14]));
            dto.setRoleId(ValueUtil.getLongByObject(obj[15]));
            dto.setAuctionMethodName(ValueUtil.getStringByObject(obj[16]));
            dto.setAuctionFormalityName(ValueUtil.getStringByObject(obj[17]));
            if (!dto.isOrg()) {
                dto.setOrgName(ValueUtil.getStringByObject(obj[18]));
            }
            if (dto.isOrg()) {
                dto.setOrgName(ValueUtil.getStringByObject(obj[19]));
            }
            dto.setAuctioneerName(ValueUtil.getStringByObject(obj[20]));
            dto.setFilePath(ValueUtil.getStringByObject(obj[21]));
            dto.setRegulationStatus(ValueUtil.getIntegerByObject(obj[22]));
            if (dto.getAuctioneerId() != null) {
                dto.setAuctioneerStatus(ValueUtil.getIntegerByObject(obj[23]));
            }
            dto.setPeopleWAssetStatus(ValueUtil.getIntegerByObject(obj[24]));
            dto.setAssetName(ValueUtil.getStringByObject(obj[25]));
            regulationArrayList.add(dto);
        }
        return regulationArrayList;
    }


    @Override
    public BigInteger countSearch(AssignAuctionSearchDto regulationSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(r.regulation_id) ");
        appendQuery(sb, regulationSearchDto);
        Query query = createQuery(sb, regulationSearchDto);
        return (BigInteger) query.getSingleResult();
    }

    public void appendQuery(StringBuilder sb, AssignAuctionSearchDto regulationSearchDto) {
        sb.append("FROM (SELECT " +
                "r.regulation_id," +
                "r.auction_req_id," +
                "r.auction_formality_id," +
                "r.code as regulationCode," +
                "r.start_time," +
                "r.number_of_rounds," +
                "r.time_per_round," +
                "r.start_registration_date," +
                "r.end_registration_date," +
                "r.auction_method_id," +
                "r.history_status," +
                "r.auction_status," +
                "r.auctioneer_id," +
                "r.end_time," +
                "a1.is_org," +
                "a.role_id," +
                "aum.name as methodName, " +
                "auf.name as formalityName," +
                "a1.full_name as peopleWAsset," +
                "a1.org_name, " +
                "a.full_name as auctioneerName," +
                "ref.file_path," +
                "r.status," +
                "a.status as auctioneerStatus, " +
                "a1.status as peopleWAssetStatus," +
                "group_concat(ass.name order by ass.serial_number SEPARATOR', ') " +
                "from regulation r " +
                "left join account a on r.auctioneer_id = a.account_id " +
                "inner join auction_method aum on r.auction_method_id = aum.auction_method_id " +
                "inner join asset ass ON r.regulation_id = ass.regulation_id " +
                "inner join auction_formality auf on auf.auction_formality_id = r.auction_formality_id " +
                "inner join (auction_req ar inner join account a1 on ar.account_id = a1.account_id) on r.auction_req_id = ar.auction_req_id " +
                "inner join regulation_file ref on r.regulation_id = ref.regulation_id and ref.type = 1 " +
                "group by r.regulation_id ) r " +
                "where r.status IN (:regulationStatus) and r.peopleWAssetStatus = :accountStatus ");
        if (StringUtils.isNotBlank(regulationSearchDto.getCode())) {
            sb.append(" and r.regulationCode LIKE :code");
        }
        if (regulationSearchDto.getAuctioneerId() != null) {
            sb.append(" and r.auctioneer_id = :auctioneerId");
        }
        if (StringUtils.isNotBlank(regulationSearchDto.getPeopleWAsset())) {
            sb.append(" and (r.peopleWAsset LIKE :peopleWAsset ");
            sb.append(" or r.org_name LIKE :peopleWAsset) ");
        }
        if (regulationSearchDto.getFromDate() != null) {
            sb.append(" and r.start_time >= :ngayTuNgay ");
        }
        if (regulationSearchDto.getToDate() != null) {
            sb.append(" and r.start_time <= :ngayDenNgay");
        }
        if (regulationSearchDto.getAuctionStatus() != null) {
            sb.append(" and r.auction_status = :auctionStatus");
        }
        if (regulationSearchDto.getAuctionStatusList() != null) {
            sb.append(" and r.auction_status IN (:auctionStatusList)");
        }
        if (regulationSearchDto.getNullAuctioneerId() != null) {
            sb.append(" and r.auctioneer_id is null");
        }
        if (regulationSearchDto.getSortField() != null) {
            sb.append(" order by ");
            if (regulationSearchDto.getSortField().equals("code")) {
                sb.append(" r.regulationCode ");
            }
            if (regulationSearchDto.getSortField().equals("auctionMethodName")) {
                sb.append(" r.methodName ");
            }
            if (regulationSearchDto.getSortField().equals("orgName")) {
                sb.append(" r.peopleWAsset and r.org_name ");
            }
            if (regulationSearchDto.getSortField().equals("auctioneerName")) {
                sb.append(" r.auctioneerName ");
            }
            if (regulationSearchDto.getSortField().equals("auctionFormalityName")) {
                sb.append(" r.formalityName ");
            }
            if (regulationSearchDto.getSortField().equals("auctionStatus")) {
                sb.append(" case when r.auction_status = 1 then 4 when r.auction_status = 2 then 1 when r.auction_status = 3 then 2 when r.auction_status = 4 then 3 end ");
            }
            if (regulationSearchDto.getSortField().equals("startTime")) {
                sb.append(" r.start_time ");
            }
            sb.append(regulationSearchDto.getSortOrder());
        } else {
            sb.append(" order by r.regulation_id DESC");
        }
    }

    public Query createQuery(StringBuilder sb, AssignAuctionSearchDto regulationSearchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(regulationSearchDto.getCode())) {
            query.setParameter("code", "%" + regulationSearchDto.getCode().trim() + "%");
        }
        query.setParameter("accountStatus", regulationSearchDto.getAccountStatus());
        query.setParameter("regulationStatus", regulationSearchDto.getRegulationStatus());
        if (StringUtils.isNotBlank(regulationSearchDto.getPeopleWAsset())) {
            query.setParameter("peopleWAsset", "%" + regulationSearchDto.getPeopleWAsset().trim() + "%");
        }
        if (regulationSearchDto.getAuctionStatus() != null) {
            query.setParameter("auctionStatus", regulationSearchDto.getAuctionStatus());
        }
        if (regulationSearchDto.getAuctionStatusList() != null) {
            query.setParameter("auctionStatusList", regulationSearchDto.getAuctionStatusList());
        }
        if (regulationSearchDto.getFromDate() != null) {
            query.setParameter("ngayTuNgay", DateUtil.formatDate(regulationSearchDto.getFromDate(), DateUtil.FROM_DATE_FORMAT));
        }
        if (regulationSearchDto.getToDate() != null) {
            query.setParameter("ngayDenNgay", DateUtil.formatDate(regulationSearchDto.getToDate(), DateUtil.TO_DATE_FORMAT));
        }
        if (regulationSearchDto.getAuctioneerId() != null) {
            query.setParameter("auctioneerId", regulationSearchDto.getAuctioneerId());
        }
        return query;
    }

}
