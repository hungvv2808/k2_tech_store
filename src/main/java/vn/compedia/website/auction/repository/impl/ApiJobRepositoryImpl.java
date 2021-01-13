package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import vn.compedia.website.auction.dto.api.ApiJobSearchDto;
import vn.compedia.website.auction.model.api.ApiJob;
import vn.compedia.website.auction.repository.ApiJobRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public class ApiJobRepositoryImpl implements ApiJobRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<ApiJob> search(ApiJobSearchDto apiJobSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("select "
               + "j.job_id, "
                + "j.create_date,"
                + "j.update_date,"
                + "j.create_by,"
                + "j.update_by, "
                + "j.name ");
        appendForQuery(sb,apiJobSearchDto);
        Query query = createQuery(sb, apiJobSearchDto);
        if (apiJobSearchDto.getPageSize() > 0) {
            query.setFirstResult(apiJobSearchDto.getPageIndex());
            query.setMaxResults(apiJobSearchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<ApiJob> apiJobList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ApiJob dto = new ApiJob();
            dto.setJobId(ValueUtil.getLongByObject(obj[0]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[1]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[2]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[4]));
            dto.setName(ValueUtil.getStringByObject(obj[5]));
            apiJobList.add(dto);
        }
        return apiJobList;
    }

    @Override
    public Long countSearch(ApiJobSearchDto apiJobSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(j.job_id) ");
        appendForQuery(sb,apiJobSearchDto);
        Query query = createQuery(sb, apiJobSearchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    @Transactional
    @Modifying
    @Override
    public void deleteAllRecords() {
        Query query = entityManager.createNativeQuery("DELETE FROM api_job");
        query.executeUpdate();
    }

    public void appendForQuery(StringBuilder sb, ApiJobSearchDto apiJobSearchDto){
        sb.append(" from api_job j where 1 = 1");
        if(StringUtils.isNotBlank(apiJobSearchDto.getName())){
            sb.append(" and j.name like :name ");
        }
        if (apiJobSearchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (apiJobSearchDto.getSortField().equals("name")) {
                sb.append(" j.name collate utf8_vietnamese_ci ");
            }
            sb.append(apiJobSearchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY j.job_id DESC ");
        }

    }
    public Query createQuery(StringBuilder sb, ApiJobSearchDto apiJobSearchDto){
        Query query = entityManager.createNativeQuery(sb.toString());
        if(StringUtils.isNotBlank(apiJobSearchDto.getName())){
            query.setParameter("name","%" + apiJobSearchDto.getName().trim() + "%");
        }
        return query;
    }
}
