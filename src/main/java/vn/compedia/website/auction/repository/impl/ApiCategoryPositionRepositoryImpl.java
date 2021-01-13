package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import vn.compedia.website.auction.dto.api.ApiCategoryPositionSearchDto;
import vn.compedia.website.auction.model.api.ApiCategoryPosition;
import vn.compedia.website.auction.repository.ApiCategoryPositionRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiCategoryPositionRepositoryImpl implements ApiCategoryPositionRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ApiCategoryPosition> search(ApiCategoryPositionSearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "acp.category_position_id, "
                + "acp.name, "
                + "acp.create_date,"
                + "acp.create_by,"
                + "acp.update_date,"
                + "acp.update_by ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY acp.category_position_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("name")) {
                sb.append(" acp.name collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("updateDate")) {
                sb.append(" acp.update_date ");
            } else if (searchDto.getSortField().equals("createDate")) {
                sb.append(" acp.update_by ");
            } else if (searchDto.getSortField().equals("updateBy")) {
                sb.append(" acp.update_date ");
            } else if (searchDto.getSortField().equals("creteBy")) {
                sb.append(" acp.creteBy ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY acp.category_position_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<ApiCategoryPosition> apiCategoryPositionList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ApiCategoryPosition dto = new ApiCategoryPosition();
            dto.setCategoryPositionId(ValueUtil.getLongByObject(obj[0]));
            dto.setName(ValueUtil.getStringByObject(obj[1]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[2]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[4]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[5]));

            apiCategoryPositionList.add(dto);
        }
        return apiCategoryPositionList;
    }

    @Override
    public Long countSearch(ApiCategoryPositionSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(acp.category_position_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    @Transactional
    @Modifying
    @Override
    public void deleteAllRecords() {
        Query query = entityManager.createNativeQuery("DELETE FROM api_category_position");
        query.executeUpdate();
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, ApiCategoryPositionSearchDto searchDto) {
        sb.append(" FROM api_category_position acp ");
        sb.append(" WHERE 1=1 ");

        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND ( acp.name LIKE :keyword ");
            sb.append(" ) ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb,  ApiCategoryPositionSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword().trim() + "%");
        }

        return query;
    }

}
