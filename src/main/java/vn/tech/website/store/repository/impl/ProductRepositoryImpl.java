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
    public List<ProductDto> search(ProductSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "p.product_id as productId, "
                + "p.name as productName, "
                + "p.brand_id as brandId, "
                + "b.name as brandName, "
                + "p.category_id as categoryId, "
                + "c.name as categoryName, "
                + "p.type as type, "
                + "p.code as code, "
                + "p.count_code as countCode, "
                + "p.description as description, "
                + "p.quantity as quantity, "
                + "p.price as price, "
                + "p.discount as discount, "
                + "p.status as status, "
                + "p.create_date as createDate, "
                + "p.create_by as createBy, "
                + "p.update_date as updateDate, "
                + "p.update_by as updateBy ");
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

        List<ProductDto> dtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ProductDto dto = new ProductDto();
            dto.setProductId(ValueUtil.getLongByObject(obj[0]));
            dto.setProductName(ValueUtil.getStringByObject(obj[1]));
            dto.setBrandId(ValueUtil.getLongByObject(obj[2]));
            dto.setBrandName(ValueUtil.getStringByObject(obj[3]));
            dto.setCategoryId(ValueUtil.getLongByObject(obj[4]));
            dto.setCategoryName(ValueUtil.getStringByObject(obj[5]));
            dto.setType(ValueUtil.getIntegerByObject(obj[6]));
            dto.setCode(ValueUtil.getStringByObject(obj[7]));
            dto.setCountCode(ValueUtil.getLongByObject(obj[8]));
            dto.setDescription(ValueUtil.getStringByObject(obj[9]));
            dto.setQuantity(ValueUtil.getLongByObject(obj[10]));
            dto.setPrice(ValueUtil.getDoubleByObject(obj[11]));
            dto.setDiscount(ValueUtil.getFloatByObject(obj[12]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[13]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[14]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[15]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[16]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[17]));
            if (dto.getDiscount() == null) {
                dto.setPriceAfterDiscount(dto.getPrice());
            } else {
                dto.setPriceAfterDiscount(dto.getPrice() - dto.getPrice() * dto.getDiscount() / 100);
            }
            dtoList.add(dto);
        }

        return dtoList;
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
        sb.append(" WHERE 1=1  ");

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
