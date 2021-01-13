package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.base.DistrictDto;
import vn.compedia.website.auction.dto.base.DistrictSearchDto;
import vn.compedia.website.auction.repository.DistrictRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DistrictRepositoryImpl implements DistrictRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DistrictDto> search(DistrictSearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "n.district_id,"
                + "n.province_id,"
                + " n.code, "
                + "n.name as districtName,"
                + " n.status,"
                + " pn.name as provinceName,"
                + "n.district_api_id,"
                + "n.version_id,"
                + "n.create_date,"
                + "n.update_date ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY n.district_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("code")) {
                sb.append(" n.code ");
            } else if (searchDto.getSortField().equals("name")) {
                sb.append(" n.name collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("status")) {
                sb.append(" n.status ");
            } else if (searchDto.getSortField().equals("updateDate")) {
                sb.append(" n.update_date ");
            } else if (searchDto.getSortField().equals("provinceName")) {
                sb.append(" pn.name collate utf8mb4_vietnamese_ci ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY n.district_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<DistrictDto> districtList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            DistrictDto dto = new DistrictDto();
            dto.setDistrictId(ValueUtil.getLongByObject(obj[0]));
            dto.setProvinceId(ValueUtil.getLongByObject(obj[1]));
            dto.setCode(ValueUtil.getStringByObject(obj[2]));
            dto.setName(ValueUtil.getStringByObject(obj[3]));
            dto.setStatus(ValueUtil.getBooleanByObject(obj[4]));
            dto.setProvinceName(ValueUtil.getStringByObject(obj[5]));
            dto.setDistrictApiId(ValueUtil.getLongByObject(obj[6]));
            dto.setVersionId(ValueUtil.getLongByObject(obj[7]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[8]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[9]));

            districtList.add(dto);
        }
        return districtList;

    }

    @Override
    public Long countSearch(DistrictSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(n.district_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());

    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, DistrictSearchDto searchDto) {
        sb.append(" FROM district n ,province pn");
        sb.append(" WHERE 1 = 1 AND n.province_id = pn.province_id ");

        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND (n.code LIKE :keyword ");
            sb.append(" OR n.name LIKE :keyword ");
            sb.append(" OR pn.name LIKE :keyword) ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, DistrictSearchDto searchDto) {
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
        Query query = entityManager.createNativeQuery("TRUNCATE TABLE district");
        query.executeUpdate();
    }
}

