package vn.tech.website.store.repository.impl;

import org.apache.commons.lang3.StringUtils;
import vn.tech.website.store.dto.CategoryDto;
import vn.tech.website.store.dto.CategorySearchDto;
import vn.tech.website.store.repository.CategoryRepositoryCustom;
import vn.tech.website.store.util.DbConstant;
import vn.tech.website.store.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CategoryDto> search(CategorySearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "c.category_id, "
                + "c.name, "
                + "c.status, "
                + "c.create_date, "
                + "c.create_by, "
                + "c.update_date, "
                + "acc.full_name as name_update ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY c.category_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("categoryId")) {
                sb.append(" c.category_id ");
            } else if (searchDto.getSortField().equals("categoryName")) {
                sb.append(" c.name collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("status")) {
                sb.append(" c.status ");
            } else if (searchDto.getSortField().equals("updateDate")) {
                sb.append(" c.update_date ");
            } else if (searchDto.getSortField().equals("updateBy")) {
                sb.append(" name_update ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY c.category_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<CategoryDto> dtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            CategoryDto dto = new CategoryDto();
            dto.setCategoryId(ValueUtil.getLongByObject(obj[0]));
            dto.setCategoryName(ValueUtil.getStringByObject(obj[1]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[2]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[3]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[4]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[5]));
            dto.setNameUpdate(ValueUtil.getStringByObject(obj[6]));
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public Long countSearch(CategorySearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(c.category_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, CategorySearchDto searchDto) {
        sb.append("FROM category c " +
                " LEFT JOIN account acc ON c.update_by = acc.account_id ");
        sb.append(" WHERE 1=1  AND c.status = " + DbConstant.STATUS_CATEGORY_ACTIVE);

        if (StringUtils.isNotBlank(searchDto.getCategoryName())) {
            sb.append(" AND c.name LIKE :categoryName ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, CategorySearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(searchDto.getCategoryName())) {
            query.setParameter("categoryName", "%" + searchDto.getCategoryName().trim() + "%");
        }

        return query;
    }
}
