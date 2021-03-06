package vn.tech.website.store.repository.impl;

import org.apache.commons.lang3.StringUtils;
import vn.tech.website.store.dto.CategorySearchDto;
import vn.tech.website.store.dto.ProductDto;
import vn.tech.website.store.dto.ProductSearchDto;
import vn.tech.website.store.repository.ProductRepositoryCustom;
import vn.tech.website.store.util.DbConstant;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ProductDto> search(ProductSearchDto searchDto) {
        return null;
    }

    @Override
    public Long countSearch(ProductSearchDto searchDto) {
        return null;
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, ProductSearchDto searchDto) {
        sb.append("FROM product p ");
        sb.append(" WHERE 1=1  ");

        if (StringUtils.isNotBlank(searchDto.getProductName())) {
            sb.append(" AND p.name LIKE :productName ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, ProductSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(searchDto.getProductName())) {
            query.setParameter("productName", "%" + searchDto.getProductName().trim() + "%");
        }

        return query;
    }
}
