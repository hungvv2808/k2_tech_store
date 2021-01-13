package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import vn.compedia.website.auction.dto.api.ApiRegulatoryAgencySearchDto;
import vn.compedia.website.auction.model.api.ApiRegulatoryAgency;
import vn.compedia.website.auction.repository.ApiRegulatoryAgencyRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiRegulatoryAgencyRepositoryImpl implements ApiRegulatoryAgencyRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ApiRegulatoryAgency> search(ApiRegulatoryAgencySearchDto searchDto){

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "ary.regulatory_agency_id, "
                + "ary.name, "
                + "ary.create_date,"
                + "ary.create_by,"
                + "ary.update_date,"
                + "ary.update_by ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("name")) {
                sb.append(" ary.name collate utf8_vietnamese_ci ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY ary.regulatory_agency_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<ApiRegulatoryAgency> apiRegulatoryAgencyList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ApiRegulatoryAgency dto = new ApiRegulatoryAgency();
            dto.setRegulatoryAgencyId(ValueUtil.getLongByObject(obj[0]));
            dto.setName(ValueUtil.getStringByObject(obj[1]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[2]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[4]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[5]));

            apiRegulatoryAgencyList.add(dto);
        }
        return apiRegulatoryAgencyList;
    }

    @Override
    public Long countSearch(ApiRegulatoryAgencySearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(ary.regulatory_agency_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, ApiRegulatoryAgencySearchDto searchDto) {
        sb.append(" FROM api_regulatory_agency ary ");
        sb.append(" WHERE 1 = 1 ");

        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND ary.name LIKE :keyword ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb,  ApiRegulatoryAgencySearchDto searchDto) {
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
        Query query = entityManager.createNativeQuery("DELETE FROM api_regulatory_agency");
        query.executeUpdate();
    }

}
