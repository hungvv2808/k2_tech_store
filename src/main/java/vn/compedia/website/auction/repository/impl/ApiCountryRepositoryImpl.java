package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import vn.compedia.website.auction.dto.api.ApiCountrySearchDto;
import vn.compedia.website.auction.model.api.ApiCountry;
import vn.compedia.website.auction.repository.ApiCountryRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiCountryRepositoryImpl implements ApiCountryRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ApiCountry> search(ApiCountrySearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "n.country_id, "
                + "n.name, "
                + "n.create_date,"
                + "n.create_by,"
                + "n.update_date,"
                + "n.update_by ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<ApiCountry> apiNationList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ApiCountry dto = new ApiCountry();
            dto.setCountryId(ValueUtil.getLongByObject(obj[0]));
            dto.setName(ValueUtil.getStringByObject(obj[1]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[2]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[4]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[5]));

            apiNationList.add(dto);
        }
        return apiNationList;
    }

    @Override
    public Long countSearch(ApiCountrySearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(n.country_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    public void appendQueryFromAndWhereForSearch(StringBuilder sb, ApiCountrySearchDto searchDto) {
        sb.append(" FROM api_country n ");
        sb.append(" WHERE 1 = 1 ");

        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND n.name LIKE :name ");
        }
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("name")) {
                sb.append(" n.name collate utf8_vietnamese_ci ");
                sb.append(searchDto.getSortOrder());
            } else {
                sb.append(" ORDER BY n.country_id DESC ");
            }
        }
    }

    public Query createQueryObjForSearch(StringBuilder sb,ApiCountrySearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            query.setParameter("name", "%" + searchDto.getKeyword().trim() + "%");
        }
        return query;
    }
    @Transactional
    @Modifying
    @Override
    public void deleteAllRecords() {
        Query query = entityManager.createNativeQuery("DELETE FROM api_country");
        query.executeUpdate();
    }



}
