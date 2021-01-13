package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.base.HistorySystemDto;
import vn.compedia.website.auction.dto.base.HistorySystemSearchDto;
import vn.compedia.website.auction.repository.HistorySystemRepositoryCustom;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HistorySystemRepositoryImpl implements HistorySystemRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<HistorySystemDto> search(HistorySystemSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT"
                + " his.history_system_id, "
                + " his.content_detail_id, "
                + " his.create_date, "
                + " his.create_by, "
                + " ct.content, "
                + " ac.full_name ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY his.history_system_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equalsIgnoreCase("createDate")) {
                sb.append(" his.create_date ");
            } else if (searchDto.getSortField().equalsIgnoreCase("content")) {
                sb.append(" ct.content ");
            } else if (searchDto.getSortField().equalsIgnoreCase("fullName")) {
                sb.append(" ac.full_name ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY his.history_system_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<HistorySystemDto> historySystemDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            HistorySystemDto dto = new HistorySystemDto();
            dto.setHistorySystemId(ValueUtil.getLongByObject(obj[0]));
            dto.setContentDetailId(ValueUtil.getLongByObject(obj[1]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[2]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setContent(ValueUtil.getStringByObject(obj[4]));
            dto.setFullName(ValueUtil.getStringByObject(obj[5]));
            historySystemDtoList.add(dto);
        }
        return historySystemDtoList;

    }

    @Override
    public BigInteger countSearch(HistorySystemSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(his.history_system_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return (BigInteger) query.getSingleResult();
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, HistorySystemSearchDto searchDto) {
        sb.append("FROM history_system his " +
                "INNER JOIN content_detail ct ON his.content_detail_id = ct.content_detail_id " +
                "INNER JOIN account ac ON his.create_by = ac.account_id " +
                "Where 1 = 1 ");
        if(searchDto.getCreateBy() != null){
            sb.append(" and ac.account_id = :createBy");
        }
        if (searchDto.getFromDate() == null && searchDto.getToDate() != null) {
            sb.append(" and his.create_date <= :ngayDenNgay ");
        } else if (searchDto.getFromDate() != null && searchDto.getToDate() == null) {
            sb.append(" and his.create_date >= :ngayTuNgay ");
        } else if (searchDto.getFromDate() != null && searchDto.getToDate() != null) {
            sb.append(" and his.create_date >= :ngayTuNgay and his.create_date <= :ngayDenNgay ");
        } else
            sb.append(" ");
        if (StringUtils.isNotBlank(searchDto.getContent())) {
            sb.append(" and ct.content LIKE :content ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, HistorySystemSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (searchDto.getCreateBy() != null) {
            query.setParameter("createBy", searchDto.getCreateBy());
        }
        if (StringUtils.isNotBlank(searchDto.getContent())) {
            query.setParameter("content", "%" + searchDto.getContent().trim() + "%");
        }
        if (searchDto.getFromDate() != null) {
            query.setParameter("ngayTuNgay", DateUtil.formatDate(searchDto.getFromDate(), DateUtil.FROM_DATE_FORMAT));
        }
        if (searchDto.getToDate() != null) {
            query.setParameter("ngayDenNgay", DateUtil.formatDate(searchDto.getToDate(), DateUtil.TO_DATE_FORMAT));
        }
        return query;
    }

}
