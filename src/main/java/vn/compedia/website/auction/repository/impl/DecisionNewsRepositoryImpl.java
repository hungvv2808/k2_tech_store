package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.system.DecisionNewsDto;
import vn.compedia.website.auction.dto.system.DecisionNewsSearchDto;
import vn.compedia.website.auction.repository.DecisionNewsRepositoryCustom;
import vn.compedia.website.auction.util.StringUtil;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DecisionNewsRepositoryImpl implements DecisionNewsRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DecisionNewsDto> search(DecisionNewsSearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "n.decision_news_id,"
                + "n.title, "
                + "n.content, "
                + "n.decision_summary, "
                + "n.type,"
                + "n.status,"
                + "f.image_path, "
                + "n.create_date,"
                + "n.update_date ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY n.decision_news_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("title")) {
                sb.append(" n.title collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("content")) {
                sb.append(" n.content collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("decisionSummary")) {
                sb.append(" n.decision_summary collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("status")) {
                sb.append(" n.status ");
            } else if (searchDto.getSortField().equals("updateDate")) {
                sb.append(" n.update_date ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY n.decision_news_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<DecisionNewsDto> decisionDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            DecisionNewsDto dto = new DecisionNewsDto();
            dto.setDecisionNewsId(ValueUtil.getLongByObject(obj[0]));
            dto.setTitle(ValueUtil.getStringByObject(obj[1]));
            dto.setContent(ValueUtil.getStringByObject(obj[2]));
            dto.setDecisionSummary(ValueUtil.getStringByObject(obj[3]));
            dto.setType(ValueUtil.getBooleanByObject(obj[4]));
            dto.setStatus(ValueUtil.getBooleanByObject(obj[5]));
            dto.setImagePath(ValueUtil.getStringByObject(obj[6]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[7]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[8]));
            decisionDtoList.add(dto);
        }
        return decisionDtoList;
    }

    @Override
    public Long countSearch(DecisionNewsSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(T.decision_news_id) ");
        sb.append(" FROM (SELECT n.decision_news_id ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY n.decision_news_id) AS T ");
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());

    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, DecisionNewsSearchDto searchDto) {
        sb.append(" FROM decision_news n LEFT JOIN decision_news_file f ON n.decision_news_id = f.decision_news_id ");
        sb.append(" WHERE 1 = 1 ");
        sb.append(" AND n.type = :type ");
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND ( n.title LIKE :keyword )");
        }
        if (searchDto.getStatus() != null) {
            sb.append(" AND n.status = :status ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, DecisionNewsSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("type", searchDto.isType());
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword().trim() + "%");
        }
        if (searchDto.getStatus() != null) {
            query.setParameter("status", searchDto.getStatus());
        }
        return query;
    }

    @Override
    public List<DecisionNewsDto> getDecisionNewsDto(DecisionNewsSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "n.decision_news_id,"
                + "n.title, "
                + "n.content, "
                + "n.decision_summary, "
                + "n.type,"
                + "n.status,"
                + "f.image_path, "
                + "n.create_date,"
                + "n.update_date ");
        appendQueryFromAndWhereForSearchFE(sb, searchDto);
        if (searchDto.getDecisionNewsId() != null) {
            sb.append(" AND n.decision_news_id <> :id");
        }
        sb.append(" ORDER BY n.decision_news_id DESC");
        List<DecisionNewsDto> decisionDtoList = new ArrayList<>();
        Query query = createQueryObjForSearchFE(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            DecisionNewsDto dto = new DecisionNewsDto();
            dto.setDecisionNewsId(ValueUtil.getLongByObject(obj[0]));
            dto.setTitle(ValueUtil.getStringByObject(obj[1]));
            dto.setContent(ValueUtil.getStringByObject(obj[2]));
            dto.setDecisionSummary(ValueUtil.getStringByObject(obj[3]));
            dto.setType(ValueUtil.getBooleanByObject(obj[4]));
            dto.setStatus(ValueUtil.getBooleanByObject(obj[5]));
            dto.setImagePath(ValueUtil.getStringByObject(obj[6]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[7]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[8]));
            String shortContent = StringUtils.abbreviate(StringUtil.br2nl(dto.getContent()), 535);
            dto.setShortContent(StringUtil.nl2space(shortContent));
            decisionDtoList.add(dto);
        }
        return decisionDtoList;
    }

    private void appendQueryFromAndWhereForSearchFE(StringBuilder sb, DecisionNewsSearchDto searchDto) {
        sb.append(" FROM decision_news n LEFT JOIN decision_news_file f ON n.decision_news_id = f.decision_news_id ");
        sb.append(" WHERE 1 = 1 ");
        sb.append(" AND n.type = :type ");
        sb.append(" AND n.status = 1 ");
        if (searchDto.getTitle() != null) {
            sb.append(" AND n.title LIKE :title");
        }
    }

    @Override
    public Long countSearchFE(DecisionNewsSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(T.decision_news_id) ");
        sb.append(" FROM (SELECT n.decision_news_id ");
        appendQueryFromAndWhereForSearchFE(sb, searchDto);
        sb.append(" GROUP BY n.decision_news_id) AS T ");
        Query query = createQueryObjForSearchFE(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    private Query createQueryObjForSearchFE(StringBuilder sb, DecisionNewsSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("type", searchDto.isType());
        if (searchDto.getDecisionNewsId() != null) {
            query.setParameter("id", searchDto.getDecisionNewsId());
        }
        if (searchDto.getStatus() != null) {
            query.setParameter("status", searchDto.getStatus());
        }
        if (searchDto.getTitle() != null) {
            query.setParameter("title", "%" + searchDto.getTitle().trim() + "%");
        }
        return query;
    }
}
