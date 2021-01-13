package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import vn.compedia.website.auction.dto.api.ApiRegulatoryAgenciesSearchDto;
import vn.compedia.website.auction.model.api.ApiRegulatoryAgencies;
import vn.compedia.website.auction.repository.ApiRegulatoryAgenciesRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiRegulatoryAgenciesRepositoryImpl implements ApiRegulatoryAgenciesRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ApiRegulatoryAgencies> search(ApiRegulatoryAgenciesSearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "arai.regulatory_agencies_id, "
                + "arai.name, "
                + "arai.create_date,"
                + "arai.create_by,"
                + "arai.update_date,"
                + "arai.update_by ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY arai.regulatory_agencies_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("name")) {
                sb.append(" arai.name collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("updateDate")) {
                sb.append(" arai.update_date ");
            } else if (searchDto.getSortField().equals("createDate")) {
                sb.append(" arai.update_by ");
            } else if (searchDto.getSortField().equals("updateBy")) {
                sb.append(" arai.update_date ");
            } else if (searchDto.getSortField().equals("creteBy")) {
                sb.append(" arai.creteBy ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY arai.regulatory_agencies_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }
        List<ApiRegulatoryAgencies> apiRegulatoryAgenciesList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ApiRegulatoryAgencies dto = new ApiRegulatoryAgencies();
            dto.setRegulatoryAgenciesId(ValueUtil.getLongByObject(obj[0]));
            dto.setName(ValueUtil.getStringByObject(obj[1]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[2]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[4]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[5]));
            apiRegulatoryAgenciesList.add(dto);
        }
        return apiRegulatoryAgenciesList;
    }

    @Override
    public Long countSearch (ApiRegulatoryAgenciesSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(arai.regulatory_agencies_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, ApiRegulatoryAgenciesSearchDto searchDto) {
        sb.append(" FROM api_regulatory_agencies arai ");
        sb.append(" WHERE 1 = 1 ");

        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND arai.name LIKE :keyword ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, ApiRegulatoryAgenciesSearchDto searchDto) {
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
        Query query = entityManager.createNativeQuery("DELETE FROM api_regulatory_agencies");
        query.executeUpdate();
    }
}
