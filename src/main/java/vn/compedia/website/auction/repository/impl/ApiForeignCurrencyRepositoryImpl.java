package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import vn.compedia.website.auction.dto.api.ApiForeignCurrencySearchDto;
import vn.compedia.website.auction.model.api.ApiForeignCurrency;
import vn.compedia.website.auction.repository.ApiForeignCurrencyRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public class ApiForeignCurrencyRepositoryImpl implements ApiForeignCurrencyRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<ApiForeignCurrency> search(ApiForeignCurrencySearchDto apiForeignCurrencySearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "fc.foreign_currency_id, "
                + "fc.name, "
                + "fc.create_date,"
                + "fc.create_by,"
                + "fc.update_date,"
                + "fc.update_by ");
        appendQueryFromAndWhereForSearch(sb, apiForeignCurrencySearchDto);
        sb.append(" GROUP BY fc.foreign_currency_id ");
        if (apiForeignCurrencySearchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (apiForeignCurrencySearchDto.getSortField().equals("name")) {
                sb.append(" fc.name collate utf8_vietnamese_ci ");
            } else if (apiForeignCurrencySearchDto.getSortField().equals("updateDate")) {
                sb.append(" fc.update_date ");
            } else if (apiForeignCurrencySearchDto.getSortField().equals("createDate")) {
                sb.append(" fc.update_by ");
            } else if (apiForeignCurrencySearchDto.getSortField().equals("updateBy")) {
                sb.append(" fc.update_date ");
            } else if (apiForeignCurrencySearchDto.getSortField().equals("creteBy")) {
                sb.append(" fc.creteBy ");
            }
            sb.append(apiForeignCurrencySearchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY fc.foreign_currency_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, apiForeignCurrencySearchDto);
        if (apiForeignCurrencySearchDto.getPageSize() > 0) {
            query.setFirstResult(apiForeignCurrencySearchDto.getPageIndex());
            query.setMaxResults(apiForeignCurrencySearchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<ApiForeignCurrency> ApiForeignCurrencyList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ApiForeignCurrency dto = new ApiForeignCurrency();
            dto.setForeignCurrencyId(ValueUtil.getLongByObject(obj[0]));
            dto.setName(ValueUtil.getStringByObject(obj[1]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[2]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[4]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[5]));

            ApiForeignCurrencyList.add(dto);
        }
        return ApiForeignCurrencyList;
    }

    @Override
    public Long countSearch (ApiForeignCurrencySearchDto apiForeignCurrencySearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(fc.foreign_currency_id) ");
        appendQueryFromAndWhereForSearch(sb, apiForeignCurrencySearchDto);
        Query query = createQueryObjForSearch(sb, apiForeignCurrencySearchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }
    private void appendQueryFromAndWhereForSearch(StringBuilder sb, ApiForeignCurrencySearchDto apiForeignCurrencySearchDto) {
        sb.append(" FROM api_foreign_currency fc ");
        sb.append(" WHERE 1 = 1 ");

        if (StringUtils.isNotBlank(apiForeignCurrencySearchDto.getKeyword())) {
            sb.append(" AND fc.name LIKE :keyword ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb,  ApiForeignCurrencySearchDto apiForeignCurrencySearchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(apiForeignCurrencySearchDto.getKeyword())) {
            query.setParameter("keyword", "%" + apiForeignCurrencySearchDto.getKeyword().trim() + "%");
        }

        return query;
    }

    @Transactional
    @Modifying
    @Override
    public void deleteAllRecords() {
        Query query = entityManager.createNativeQuery("DELETE FROM api_foreign_currency");
        query.executeUpdate();
    }
}
