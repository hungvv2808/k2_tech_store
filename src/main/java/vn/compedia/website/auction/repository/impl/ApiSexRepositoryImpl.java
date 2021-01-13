package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import vn.compedia.website.auction.dto.api.ApiSexSearchDto;
import vn.compedia.website.auction.model.api.ApiSex;
import vn.compedia.website.auction.repository.ApiSexRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiSexRepositoryImpl implements ApiSexRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ApiSex> search(ApiSexSearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "sex.sex_id, "
                + "sex.name, "
                + "sex.create_date,"
                + "sex.create_by,"
                + "sex.update_date,"
                + "sex.update_by ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY sex.sex_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("name")) {
                sb.append(" sex.name collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("updateDate")) {
                sb.append(" sex.update_date ");
            } else if (searchDto.getSortField().equals("createDate")) {
                sb.append(" sex.update_by ");
            } else if (searchDto.getSortField().equals("updateBy")) {
                sb.append(" sex.update_date ");
            } else if (searchDto.getSortField().equals("creteBy")) {
                sb.append(" sex.creteBy ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY sex.sex_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<ApiSex> apiSexList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ApiSex dto = new ApiSex();
            dto.setSexId(ValueUtil.getIntegerByObject(obj[0]));
            dto.setName(ValueUtil.getStringByObject(obj[1]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[2]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[4]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[5]));

            apiSexList.add(dto);
        }
        return apiSexList;
    }

    @Override
    public Long countSearch (ApiSexSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(sex.sex_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, ApiSexSearchDto searchDto) {
        sb.append(" FROM api_sex sex ");
        sb.append(" WHERE 1 = 1 ");

        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND sex.name LIKE :keyword ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb,  ApiSexSearchDto searchDto) {
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
        Query query = entityManager.createNativeQuery("DELETE FROM api_sex");
        query.executeUpdate();
    }
}
