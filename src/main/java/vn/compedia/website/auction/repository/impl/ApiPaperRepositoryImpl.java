package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import vn.compedia.website.auction.dto.api.ApiNationSearchDto;
import vn.compedia.website.auction.dto.api.ApiPaperSearchDto;
import vn.compedia.website.auction.dto.api.ApiReligionSearchDto;
import vn.compedia.website.auction.model.api.ApiPaper;
import vn.compedia.website.auction.model.api.ApiReligion;
import vn.compedia.website.auction.repository.ApiPaperRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public class ApiPaperRepositoryImpl implements ApiPaperRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ApiPaper> search(ApiPaperSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("select " +
                "ap.api_paper_id," +
                "ap.api_paper_name," +
                "ap.create_date," +
                "ap.create_by," +
                "ap.update_date," +
                "ap.update_by " );
        appendForQuery(sb,searchDto);
        Query query = createQuery(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<ApiPaper> apiPaperList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ApiPaper dto = new ApiPaper();
            dto.setApiPaperId(ValueUtil.getLongByObject(obj[0]));
            dto.setName(ValueUtil.getStringByObject(obj[1]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[2]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[4]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[5]));
            apiPaperList.add(dto);
        }
        return apiPaperList;
    }

    @Override
    public Long countSearch(ApiPaperSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(ap.api_paper_id) ");
        appendForQuery(sb,searchDto);
        Query query = createQuery(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    public void appendForQuery(StringBuilder sb, ApiPaperSearchDto searchDto){
        sb.append("from api_paper ap where 1 = 1");
        if(StringUtils.isNotBlank(searchDto.getName())){
            sb.append(" and ap.api_paper_name like :name ");
        }
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("name")) {
                sb.append(" ap.api_paper_name collate utf8_vietnamese_ci ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY ap.api_paper_id DESC ");
        }

    }
    public Query createQuery(StringBuilder sb, ApiPaperSearchDto searchDto){
        Query query = entityManager.createNativeQuery(sb.toString());
        if(StringUtils.isNotBlank(searchDto.getName())){
            query.setParameter("name","%" + searchDto.getName().trim() + "%");
        }
        return query;
    }

    @Transactional
    @Modifying
    @Override
    public void deleteAllRecords() {
        Query query = entityManager.createNativeQuery("DELETE FROM api_paper");
        query.executeUpdate();
    }
}
