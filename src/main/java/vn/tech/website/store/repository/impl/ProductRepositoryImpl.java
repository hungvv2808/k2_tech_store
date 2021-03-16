package vn.tech.website.store.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import vn.tech.website.store.dto.CategoryDto;
import vn.tech.website.store.dto.CategorySearchDto;
import vn.tech.website.store.dto.ProductDto;
import vn.tech.website.store.dto.ProductSearchDto;
import vn.tech.website.store.entity.EntityMapper;
import vn.tech.website.store.repository.ProductImageRepository;
import vn.tech.website.store.repository.ProductRepositoryCustom;
import vn.tech.website.store.util.DbConstant;
import vn.tech.website.store.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List search(ProductSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "p.product_id AS productId, "
                + "p.name AS productName, "
                + "p.brand_id AS brandId, "
                + "b.name AS brandName, "
                + "p.category_id AS categoryId, "
                + "c.name AS categoryName, "
                + "p.type AS type, "
                + "p.code AS code, "
                + "p.count_code AS countCode, "
                + "p.description AS description, "
                + "p.quantity AS quantity, "
                + "p.price AS price, "
                + "p.discount AS discount, "
                + "p.status AS status, "
                + "p.create_date AS createDate, "
                + "p.create_by AS createBy, "
                + "p.update_date AS updateDate, "
                + "p.update_by AS updateBy, "
                + "p.price - p.price * p.discount / 100 AS priceAfterDiscount, "
                + "(SELECT pi.image_path FROM product_image pi WHERE pi.product_id = p.product_id LIMIT 1) AS imageToShow ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY p.product_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("productId")) {
                sb.append(" p.product_id ");
            } else if (searchDto.getSortField().equals("productName")) {
                sb.append(" p.name collate utf8mb4_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("status")) {
                sb.append(" p.status ");
            } else if (searchDto.getSortField().equals("type")) {
                sb.append(" p.type ");
            } else if (searchDto.getSortField().equals("code")) {
                sb.append(" p.code ");
            } else if (searchDto.getSortField().equals("updateDate")) {
                sb.append(" p.update_date ");
            } else if (searchDto.getSortField().equals("updateBy")) {
                sb.append(" p.updateBy ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY p.product_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        return EntityMapper.mapper(query, sb.toString(), ProductDto.class);
    }

    @Override
    public Long countSearch(ProductSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(p.product_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, ProductSearchDto searchDto) {
        sb.append("FROM product p " +
                " INNER JOIN category c on p.category_id = c. category_id " +
                " INNER JOIN brand b on p.brand_id = b.brand_id ");
        sb.append(" WHERE 1=1 ");

        if (StringUtils.isNotBlank(searchDto.getProductName())) {
            sb.append(" AND p.name LIKE :productName ");
        }
        if (searchDto.getCategoryId() != null) {
            sb.append(" AND p.category_id = :categoryId ");
        }
        if (searchDto.getBrandId() != null) {
            sb.append(" AND p.brand_id = :brandId ");
        }
        if (StringUtils.isNotBlank(searchDto.getCode())) {
            sb.append(" AND p.code LIKE :code ");
        }
        if (searchDto.getType() != null) {
            sb.append(" AND p.type = :type ");
        }
        if (searchDto.getExpertType() != null) {
            sb.append(" AND p.type <> :expertType ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, ProductSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(searchDto.getProductName())) {
            query.setParameter("productName", "%" + searchDto.getProductName().trim() + "%");
        }
        if (searchDto.getCategoryId() != null) {
            query.setParameter("categoryId", searchDto.getCategoryId());
        }
        if (searchDto.getBrandId() != null) {
            query.setParameter("brandId", searchDto.getBrandId());
        }
        if (StringUtils.isNotBlank(searchDto.getCode())) {
            query.setParameter("code", "%" + searchDto.getCode().trim() + "%");
        }
        if (searchDto.getType() != null) {
            query.setParameter("type", searchDto.getType());
        }
        if (searchDto.getExpertType() != null) {
            query.setParameter("expertType", searchDto.getExpertType());
        }

        return query;
    }
}
