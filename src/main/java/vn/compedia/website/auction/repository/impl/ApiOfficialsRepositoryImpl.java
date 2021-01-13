package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import vn.compedia.website.auction.dto.api.ApiCategoryPositionSearchDto;
import vn.compedia.website.auction.dto.api.ApiOfficialsSearchDto;
import vn.compedia.website.auction.model.api.ApiCategoryPosition;
import vn.compedia.website.auction.model.api.ApiOfficials;
import vn.compedia.website.auction.repository.ApiCategoryPositionRepositoryCustom;
import vn.compedia.website.auction.repository.ApiOfficialsRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiOfficialsRepositoryImpl implements ApiOfficialsRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ApiOfficials> search(ApiOfficialsSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "off.api_officials_id, "
                + "off.phone, "
                + "off.identity_card, "
                + "off.organization_id, "
                + "off.position, "
                + "off.place_identity_card, "
                + "off.organization, "
                + "off.name, "
                + "off.position_id, "
                + "off.code, "
                + "off.date, "
                + "off.id_api, "
                + "off.email, "
                + "off.date_identity_card, "
                + "off.create_date,"
                + "off.create_by,"
                + "off.update_date,"
                + "off.update_by ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY off.api_officials_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("name")) {
                sb.append(" off.name collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("updateDate")) {
                sb.append(" off.update_date ");
            } else if (searchDto.getSortField().equals("createDate")) {
                sb.append(" off.update_by ");
            } else if (searchDto.getSortField().equals("updateBy")) {
                sb.append(" off.update_date ");
            } else if (searchDto.getSortField().equals("creteBy")) {
                sb.append(" off.crete_by ");
            } else if (searchDto.getSortField().equals("phone")) {
                sb.append(" off.phone ");
            } else if (searchDto.getSortField().equals("identityCard")) {
                sb.append(" off.identity_card ");
            }  else if (searchDto.getSortField().equals("position")) {
                sb.append(" off.position ");
            } else if (searchDto.getSortField().equals("placeIdentityCard")) {
                sb.append(" off.place_identity_card ");
            } else if (searchDto.getSortField().equals("organization")) {
                sb.append(" off.organization ");
            } else if (searchDto.getSortField().equals("date")) {
                sb.append(" off.date ");
            } else if (searchDto.getSortField().equals("email")) {
                sb.append(" off.email ");
            } else if (searchDto.getSortField().equals("dateIdentityCard")) {
                sb.append(" off.date_identity_card ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY off.api_officials_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<ApiOfficials> apiOfficialsList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ApiOfficials dto = new ApiOfficials();
            dto.setApiOfficialsId(ValueUtil.getLongByObject(obj[0]));
            dto.setPhone(ValueUtil.getStringByObject(obj[1]));
            dto.setIdentityCard(ValueUtil.getStringByObject(obj[2]));
            dto.setOrganizationId(ValueUtil.getLongByObject(obj[3]));
            dto.setPosition(ValueUtil.getStringByObject(obj[4]));
            dto.setPlaceIdentityCard(ValueUtil.getStringByObject(obj[5]));
            dto.setOrganization(ValueUtil.getStringByObject(obj[6]));
            dto.setName(ValueUtil.getStringByObject(obj[7]));
            dto.setPositionId(ValueUtil.getStringByObject(obj[8]));
            dto.setCode(ValueUtil.getStringByObject(obj[9]));
            dto.setDate(ValueUtil.getDateByObject(obj[10]));
            dto.setIdApi(ValueUtil.getLongByObject(obj[11]));
            dto.setEmail(ValueUtil.getStringByObject(obj[12]));
            dto.setDateIdentityCard(ValueUtil.getDateByObject(obj[13]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[14]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[15]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[16]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[17]));
            apiOfficialsList.add(dto);
        }
        return apiOfficialsList;
    }

    @Override
    public Long countSearch(ApiOfficialsSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(off.api_officials_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }


    @Transactional
    @Modifying
    @Override
    public void deleteAllRecords() {
        Query query = entityManager.createNativeQuery("DELETE FROM api_officials");
        query.executeUpdate();
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, ApiOfficialsSearchDto searchDto) {
        sb.append(" FROM api_officials off ");
        sb.append(" WHERE 1 = 1 ");

        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND off.name LIKE :keyword ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb,  ApiOfficialsSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword().trim() + "%");
        }

        return query;
    }

}
