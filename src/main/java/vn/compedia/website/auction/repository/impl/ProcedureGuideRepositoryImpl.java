package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.system.DecisionNewsDto;
import vn.compedia.website.auction.dto.system.DecisionNewsSearchDto;
import vn.compedia.website.auction.dto.system.ProcedureGuideDto;
import vn.compedia.website.auction.dto.system.ProcedureGuideSearchDto;
import vn.compedia.website.auction.repository.ProcedureGuideRepositoryCustom;
import vn.compedia.website.auction.util.StringUtil;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProcedureGuideRepositoryImpl implements ProcedureGuideRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ProcedureGuideDto> search(ProcedureGuideSearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "n.procedure_guide_id, "
                + "n.title, "
                + "n.file_path, "
                + "n.status, "
                + "n.create_date, "
                + "n.update_date, "
                + "n.file_name ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY n.procedure_guide_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("title")) {
                sb.append(" n.title collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("content")) {
                sb.append(" n.content ");
            }else if (searchDto.getSortField().equals("status")) {
                sb.append(" n.status ");
            } else if (searchDto.getSortField().equals("updateDate")) {
                sb.append(" n.update_date ");
            }   else if (searchDto.getSortField().equals("filePath")) {
                sb.append(" n.file_path ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY n.procedure_guide_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<ProcedureGuideDto> decisionDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ProcedureGuideDto dto = new ProcedureGuideDto();
            dto.setProcedureGuideId(ValueUtil.getLongByObject(obj[0]));
            dto.setTitle(ValueUtil.getStringByObject(obj[1]));
            dto.setFilePath(ValueUtil.getStringByObject(obj[2]));
            dto.setStatus(ValueUtil.getBooleanByObject(obj[3]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[4]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[5]));
            dto.setFileName(ValueUtil.getStringByObject(obj[6]));

            decisionDtoList.add(dto);
        }
        return decisionDtoList;
    }

    @Override
    public Long countSearch(ProcedureGuideSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(n.procedure_guide_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());

    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, ProcedureGuideSearchDto searchDto) {
        sb.append(" FROM procedure_guide n ");
        sb.append(" WHERE 1 = 1 ");

        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND n.title LIKE :keyword ");
        }
        if(searchDto.getStatus() != null) {
            sb.append(" AND n.status = :status ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, ProcedureGuideSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword().trim() + "%");
        }
        if (searchDto.getStatus() != null) {
            query.setParameter("status", searchDto.getStatus());
        }
        return query;
    }
    @Override
    public List<ProcedureGuideDto> getProcedureGuideDto(ProcedureGuideSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "n.procedure_guide_id,"
                + "n.title, "
                + "n.file_path, "
                + "n.status ");
        appendQueryFromAndWhereForSearchFE(sb, searchDto);
        if (searchDto.getProcedureGuideId() != null) {
            sb.append(" AND n.procedure_guide_id <> :id");
        }
        sb.append(" ORDER BY n.update_date DESC");
        List<ProcedureGuideDto> procedureGuideDtoList = new ArrayList<>();
        Query query = createQueryObjForSearchFE(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ProcedureGuideDto dto = new ProcedureGuideDto();
            dto.setProcedureGuideId(ValueUtil.getLongByObject(obj[0]));
            dto.setTitle(ValueUtil.getStringByObject(obj[1]));
            dto.setFilePath(ValueUtil.getStringByObject(obj[5]));
            dto.setStatus(ValueUtil.getBooleanByObject(obj[4]));
            procedureGuideDtoList.add(dto);
        }
        return procedureGuideDtoList;
    }

    private void appendQueryFromAndWhereForSearchFE(StringBuilder sb, ProcedureGuideSearchDto searchDto) {
        sb.append(" FROM procedure_guide n ");
        sb.append(" WHERE 1 = 1 ");
        if (searchDto.getTitle() != null) {
            sb.append(" AND n.title LIKE :title");
        }
    }

    @Override
    public Long countSearchFE(ProcedureGuideSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(T.procedure_guide_id) ");
        sb.append(" FROM (SELECT n.procedure_guide_id ");
        appendQueryFromAndWhereForSearchFE(sb, searchDto);
        sb.append(" GROUP BY n.procedure_guide_id) AS T ");
        Query query = createQueryObjForSearchFE(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }
    private Query createQueryObjForSearchFE(StringBuilder sb, ProcedureGuideSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (searchDto.getProcedureGuideId() != null) {
            query.setParameter("id", searchDto.getProcedureGuideId());
        }
        if (searchDto.getTitle() != null) {
            query.setParameter("title", "%" + searchDto.getTitle() + "%");
        }
        return query;
    }
}
