package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import vn.compedia.website.auction.dto.api.ApiCareerGroupSearchDto;
import vn.compedia.website.auction.model.api.ApiCareerGroup;
import vn.compedia.website.auction.repository.ApiCareerGroupRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public class ApiCareerGroupRepositoryImpl implements ApiCareerGroupRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ApiCareerGroup> search(ApiCareerGroupSearchDto apiCareerGroupSearchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "car.career_group_id, "
                + "car.name, "
                + "car.create_date,"
                + "car.create_by,"
                + "car.update_date,"
                + "car.update_by ");
        appendQueryFromAndWhereForSearch(sb, apiCareerGroupSearchDto);
        sb.append(" GROUP BY car.career_group_id ");
        if (apiCareerGroupSearchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (apiCareerGroupSearchDto.getSortField().equals("name")) {
                sb.append(" car.name collate utf8_vietnamese_ci ");
            } else if (apiCareerGroupSearchDto.getSortField().equals("updateDate")) {
                sb.append(" car.update_date ");
            } else if (apiCareerGroupSearchDto.getSortField().equals("createDate")) {
                sb.append(" car.update_by ");
            } else if (apiCareerGroupSearchDto.getSortField().equals("updateBy")) {
                sb.append(" car.update_date ");
            } else if (apiCareerGroupSearchDto.getSortField().equals("creteBy")) {
                sb.append(" car.creteBy ");
            }
            sb.append(apiCareerGroupSearchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY car.career_group_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, apiCareerGroupSearchDto);
        if (apiCareerGroupSearchDto.getPageSize() > 0) {
            query.setFirstResult(apiCareerGroupSearchDto.getPageIndex());
            query.setMaxResults(apiCareerGroupSearchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<ApiCareerGroup> apiCareerGroupList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ApiCareerGroup dto = new ApiCareerGroup();
            dto.setCareerGroupId(ValueUtil.getLongByObject(obj[0]));
            dto.setName(ValueUtil.getStringByObject(obj[1]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[2]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[4]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[5]));

            apiCareerGroupList.add(dto);
        }
        return apiCareerGroupList;
    }

    @Override
    public Long countSearch(ApiCareerGroupSearchDto apiCareerGroupSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(car.career_group_id) ");
        appendQueryFromAndWhereForSearch(sb, apiCareerGroupSearchDto);
        Query query = createQueryObjForSearch(sb, apiCareerGroupSearchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    @Transactional
    @Modifying
    @Override
    public void deleteAllRecords() {
        Query query = entityManager.createNativeQuery("DELETE FROM api_career_group");
        query.executeUpdate();
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, ApiCareerGroupSearchDto apiCareerGroupSearchDto) {
        sb.append(" FROM api_career_group car ");
        sb.append(" WHERE 1 = 1 ");

        if (StringUtils.isNotBlank(apiCareerGroupSearchDto.getKeyword())) {
            sb.append(" AND ( car.name LIKE :keyword ");
            sb.append(" ) ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb,  ApiCareerGroupSearchDto apiCareerGroupSearchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(apiCareerGroupSearchDto.getKeyword())) {
            query.setParameter("keyword", "%" + apiCareerGroupSearchDto.getKeyword().trim() + "%");
        }

        return query;
    }


}
