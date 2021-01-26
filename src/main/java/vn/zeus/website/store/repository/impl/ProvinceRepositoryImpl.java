package vn.zeus.website.store.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import vn.zeus.website.store.dto.base.ProvinceSearchDto;
import vn.zeus.website.store.model.Province;
import vn.zeus.website.store.repository.ProvinceRepositoryCustom;
import vn.zeus.website.store.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProvinceRepositoryImpl implements ProvinceRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Province> search(ProvinceSearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "n.province_id,"
                + " n.code, "
                + "n.name,"
                + "n.status,"
                + "n.province_api_id,"
                + "n.version_id,"
                + "n.create_date,"
                + "n.update_date ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY n.province_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("code")) {
                sb.append(" n.code ");
            } else if (searchDto.getSortField().equals("name")) {
                sb.append(" n.name ");
            } else if (searchDto.getSortField().equals("status")) {
                sb.append(" n.status ");
            } else if (searchDto.getSortField().equals("updateDate")) {
                sb.append(" n.update_date ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY n.province_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<Province> provinceList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            Province dto = new Province();
            dto.setProvinceId(ValueUtil.getLongByObject(obj[0]));
            dto.setCode(ValueUtil.getStringByObject(obj[1]));
            dto.setName(ValueUtil.getStringByObject(obj[2]));
            dto.setStatus(ValueUtil.getBooleanByObject(obj[3]));
            dto.setProvinceApiId(ValueUtil.getLongByObject(obj[4]));
            dto.setVersionId(ValueUtil.getLongByObject(obj[5]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[6]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[7]));

            provinceList.add(dto);
        }
        return provinceList;
    }

    @Override
    public Long countSearch(ProvinceSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(n.province_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());

    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, ProvinceSearchDto searchDto) {
        sb.append(" FROM province n ");
        sb.append(" WHERE 1 = 1 ");

        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND (n.code LIKE :keyword ");
            sb.append(" OR n.name LIKE :keyword) ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, ProvinceSearchDto searchDto) {
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
        Query query = entityManager.createNativeQuery("TRUNCATE TABLE province");
        query.executeUpdate();
    }

}
