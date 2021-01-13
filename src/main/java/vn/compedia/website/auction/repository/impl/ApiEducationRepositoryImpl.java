package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import vn.compedia.website.auction.dto.api.ApiEducationSearchDto;
import vn.compedia.website.auction.dto.api.ApiRegularSearchDto;
import vn.compedia.website.auction.model.api.ApiEducation;
import vn.compedia.website.auction.model.api.ApiRegular;
import vn.compedia.website.auction.repository.ApiEducationRepositoryCustom;
import vn.compedia.website.auction.repository.ApiRegularRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiEducationRepositoryImpl implements ApiEducationRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ApiEducation> search(ApiEducationSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "n.education_id, "
                + "n.name, "
                + "n.create_date,"
                + "n.create_by,"
                + "n.update_date,"
                + "n.update_by ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY n.education_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("name")) {
                sb.append(" n.name collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("updateDate")) {
                sb.append(" n.update_date  ");
            } else if (searchDto.getSortField().equals("createDate")) {
                sb.append(" n.update_by  ");
            } else if (searchDto.getSortField().equals("updateBy")) {
                sb.append(" n.update_date  ");
            } else if (searchDto.getSortField().equals("creteBy")) {
                sb.append(" n.creteBy ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY n.education_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<ApiEducation> apiEducationList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ApiEducation dto = new ApiEducation();
            dto.setEducationId(ValueUtil.getLongByObject(obj[0]));
            dto.setName(ValueUtil.getStringByObject(obj[1]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[2]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[4]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[5]));

            apiEducationList.add(dto);
        }
        return apiEducationList;
    }

    @Override
    public  Long countSearch(ApiEducationSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(n.education_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, ApiEducationSearchDto searchDto) {
        sb.append(" FROM api_education n ");
        sb.append(" WHERE 1 = 1 ");

        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND ( n.name LIKE :keyword ");
            sb.append(" ) ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb,   ApiEducationSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword().trim() + "%");
        }

        return query;
    }

    @Transactional
    @Modifying
    @Override
    public void deleteAllRecords() {
        Query query = entityManager.createNativeQuery("DELETE FROM api_education");
        query.executeUpdate();
    }
}
