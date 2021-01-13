package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.assign_auction.RunTheAuctionDto;
import vn.compedia.website.auction.dto.assign_auction.RunTheAuctionSearchDto;
import vn.compedia.website.auction.repository.RunTheAuctionRepositoryCustom;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RunTheAuctionRepositoryImpl implements RunTheAuctionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RunTheAuctionDto> search(RunTheAuctionSearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "n.regulation_id,"
                + "n.code, "
                + "rf.file_path, "
                + "n.auction_formality_id, "
                + "(SELECT af.name FROM auction_formality af WHERE af.auction_formality_id = n.auction_formality_id) as auctionFormalityName, "
                + "n.auction_method_id, "
                + "(SELECT am.name FROM auction_method am WHERE am.auction_method_id = n.auction_method_id) as auctionMethodName, "
                + "n.start_time,"
                + "n.auction_status, "
                + "group_concat(ass.name order by ass.serial_number SEPARATOR', ') ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY n.regulation_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("code")) {
                sb.append(" n.code ");
            } else if (searchDto.getSortField().equals("startTime")) {
                sb.append(" n.start_time ");
            } else if (searchDto.getSortField().equals("auctionMethodName")) {
                sb.append(" auctionMethodName ");
            } else if (searchDto.getSortField().equals("auctionFormalityName")) {
                sb.append(" auctionFormalityName ");
            }   else if (searchDto.getSortField().equals("auctionStatus")) {
                sb.append(" n.auction_status ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY n.regulation_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<RunTheAuctionDto> decisionDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            RunTheAuctionDto dto = new RunTheAuctionDto();
            dto.setRegulationId(ValueUtil.getLongByObject(obj[0]));
            dto.setCode(ValueUtil.getStringByObject(obj[1]));
            dto.setFilePath(ValueUtil.getStringByObject(obj[2]));
            dto.setAuctionFormalityId(ValueUtil.getLongByObject(obj[3]));
            dto.setAuctionFormalityName(ValueUtil.getStringByObject(obj[4]));
            dto.setAuctionMethodId(ValueUtil.getLongByObject(obj[5]));
            dto.setAuctionMethodName(ValueUtil.getStringByObject(obj[6]));
            dto.setStartTime(ValueUtil.getDateByObject(obj[7]));
            dto.setAuctionStatus(ValueUtil.getIntegerByObject(obj[8]));
            dto.setAssetName(ValueUtil.getStringByObject(obj[9]));
            decisionDtoList.add(dto);
        }
        return decisionDtoList;
    }

    @Override
    public Long countSearch(RunTheAuctionSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(T.regulation_id) ");
        sb.append(" FROM (SELECT n.regulation_id ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY n.regulation_id) AS T ");
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, RunTheAuctionSearchDto searchDto) {
        sb.append(" FROM regulation n " +
                "INNER JOIN regulation_file rf ON n.regulation_id = rf.regulation_id " +
                "INNER JOIN account acc ON acc.account_id = n.auctioneer_id " +
                "INNER JOIN asset ass ON n.regulation_id = ass.regulation_id ");
        sb.append(" WHERE 1 = 1 " +
                " AND (n.status = 1 or n.status = 2) " +
                " AND rf.type = 1 ");
        sb.append("AND n.auctioneer_id = :auctioneer_id");
        if(StringUtils.isNotBlank(searchDto.getCode())) {
            sb.append(" AND n.code LIKE :code ");
        }
        if(searchDto.getAuctionMethodId() != null) {
            sb.append(" AND n.auction_method_id = :auction_method_id ");
        }
        if(searchDto.getAuctionFormalityId() != null) {
            sb.append(" AND n.auction_formality_id = :auction_formality_id ");
        }
        if(searchDto.getAuctionStatus() != null) {
            sb.append(" AND n.auction_status = :auction_status ");
        }
        if (searchDto.getTimeStart() != null) {
            sb.append(" AND n.start_time >= :startTime ");
        }
        if (searchDto.getTimeEnd() != null) {
            sb.append(" AND n.start_time <= :timeEnd ");
        }
    }


    private Query createQueryObjForSearch(StringBuilder sb, RunTheAuctionSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("auctioneer_id", searchDto.getAuctioneerId());
        if(StringUtils.isNotBlank(searchDto.getCode())) {
            query.setParameter("code", "%" + searchDto.getCode().trim() + "%");
        }

        if(searchDto.getAuctionFormalityId() != null) {
            query.setParameter("auction_formality_id", searchDto.getAuctionFormalityId() );
        }

        if(searchDto.getAuctionMethodId() != null) {
            query.setParameter("auction_method_id", searchDto.getAuctionMethodId() );
        }
        if(searchDto.getAuctionStatus() != null) {
            query.setParameter("auction_status", searchDto.getAuctionStatus() );
        }
        if (searchDto.getTimeStart() != null) {
            query.setParameter("startTime", DateUtil.formatFromDate(searchDto.getTimeStart()));
        }
        if (searchDto.getTimeEnd() != null) {
            query.setParameter("timeEnd", DateUtil.formatToDate(searchDto.getTimeEnd()));
        }
        return query;
    }
}
