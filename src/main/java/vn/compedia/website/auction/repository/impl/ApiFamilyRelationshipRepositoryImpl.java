package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import vn.compedia.website.auction.dto.api.ApiFamilyRelationshipSearchDto;
import vn.compedia.website.auction.model.api.ApiFamilyRelationship;
import vn.compedia.website.auction.repository.ApiFamilyRelationshipRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public class ApiFamilyRelationshipRepositoryImpl implements ApiFamilyRelationshipRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ApiFamilyRelationship> search(ApiFamilyRelationshipSearchDto apiFamilyRelationshipSearchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "fr.family_relationship_id, "
                + "fr.name, "
                + "fr.create_date,"
                + "fr.create_by,"
                + "fr.update_date,"
                + "fr.update_by ");
        appendQueryFromAndWhereForSearch(sb, apiFamilyRelationshipSearchDto);
        sb.append(" GROUP BY fr.family_relationship_id ");
        if (apiFamilyRelationshipSearchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (apiFamilyRelationshipSearchDto.getSortField().equals("name")) {
                sb.append(" fr.name collate utf8_vietnamese_ci ");
            } else if (apiFamilyRelationshipSearchDto.getSortField().equals("updateDate")) {
                sb.append(" fr.update_date ");
            } else if (apiFamilyRelationshipSearchDto.getSortField().equals("createDate")) {
                sb.append(" fr.update_by ");
            } else if (apiFamilyRelationshipSearchDto.getSortField().equals("updateBy")) {
                sb.append(" fr.update_date ");
            } else if (apiFamilyRelationshipSearchDto.getSortField().equals("creteBy")) {
                sb.append(" fr.creteBy ");
            }
            sb.append(apiFamilyRelationshipSearchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY fr.family_relationship_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, apiFamilyRelationshipSearchDto);
        if (apiFamilyRelationshipSearchDto.getPageSize() > 0) {
            query.setFirstResult(apiFamilyRelationshipSearchDto.getPageIndex());
            query.setMaxResults(apiFamilyRelationshipSearchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<ApiFamilyRelationship> apiCareerGroupList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ApiFamilyRelationship dto = new ApiFamilyRelationship();
            dto.setFamilyRegulationId(ValueUtil.getLongByObject(obj[0]));
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
    public Long countSearch (ApiFamilyRelationshipSearchDto apiFamilyRelationshipSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(fr.family_relationship_id) ");
        appendQueryFromAndWhereForSearch(sb, apiFamilyRelationshipSearchDto);
        Query query = createQueryObjForSearch(sb, apiFamilyRelationshipSearchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, ApiFamilyRelationshipSearchDto apiFamilyRelationshipSearchDto) {
        sb.append(" FROM api_family_relationship fr ");
        sb.append(" WHERE 1 = 1 ");

        if (StringUtils.isNotBlank(apiFamilyRelationshipSearchDto.getKeyword())) {
            sb.append(" AND fr.name LIKE :keyword ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb,  ApiFamilyRelationshipSearchDto apiFamilyRelationshipSearchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(apiFamilyRelationshipSearchDto.getKeyword())) {
            query.setParameter("keyword", "%" + apiFamilyRelationshipSearchDto.getKeyword().trim() + "%");
        }

        return query;
    }
    @Transactional
    @Modifying
    @Override
    public void deleteAllRecords() {
        Query query = entityManager.createNativeQuery("DELETE FROM api_family_relationship");
        query.executeUpdate();
    }
}
