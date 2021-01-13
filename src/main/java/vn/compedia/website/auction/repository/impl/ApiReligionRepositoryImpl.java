package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import vn.compedia.website.auction.dto.api.ApiReligionSearchDto;
import vn.compedia.website.auction.model.api.ApiReligion;
import vn.compedia.website.auction.repository.ApiReligionRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiReligionRepositoryImpl implements ApiReligionRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<ApiReligion> search(ApiReligionSearchDto apiReligionSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("select " +
                "ar.religion_id," +
                "ar.name," +
                "ar.create_date," +
                "ar.create_by," +
                "ar.update_date," +
                "ar.update_by " );
        appendForQuery(sb,apiReligionSearchDto);
        Query query = createQuery(sb, apiReligionSearchDto);
        if (apiReligionSearchDto.getPageSize() > 0) {
            query.setFirstResult(apiReligionSearchDto.getPageIndex());
            query.setMaxResults(apiReligionSearchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<ApiReligion> apiReligionList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ApiReligion dto = new ApiReligion();
            dto.setReligionId(ValueUtil.getLongByObject(obj[0]));
            dto.setName(ValueUtil.getStringByObject(obj[1]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[2]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[4]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[5]));
            apiReligionList.add(dto);
        }
        return apiReligionList;
    }

    @Override
    public Long countSearch(ApiReligionSearchDto apiReligionSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(ar.religion_id) ");
        appendForQuery(sb,apiReligionSearchDto);
        Query query = createQuery(sb, apiReligionSearchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    public void appendForQuery(StringBuilder sb, ApiReligionSearchDto apiReligionSearchDto){
        sb.append(" from api_religion ar where 1 = 1 ");
        if(StringUtils.isNotBlank(apiReligionSearchDto.getName())){
            sb.append(" and ar.name like :name ");
        }
        if (apiReligionSearchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (apiReligionSearchDto.getSortField().equals("name")) {
                sb.append(" ar.name collate utf8_vietnamese_ci ");
            }
            sb.append(apiReligionSearchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY ar.religion_id DESC ");
        }

    }
    public Query createQuery(StringBuilder sb, ApiReligionSearchDto apiReligionSearchDto){
        Query query = entityManager.createNativeQuery(sb.toString());
        if(StringUtils.isNotBlank(apiReligionSearchDto.getName())){
            query.setParameter("name","%" + apiReligionSearchDto.getName().trim() + "%");
        }
        return query;
    }

    @Transactional
    @Modifying
    @Override
    public void deleteAllRecords() {
        Query query = entityManager.createNativeQuery("DELETE FROM api_religion");
        query.executeUpdate();
    }
}
