package vn.tech.website.store.repository.impl;

import org.apache.commons.lang3.StringUtils;
import vn.tech.website.store.dto.BrandDto;
import vn.tech.website.store.dto.BrandSearchDto;
import vn.tech.website.store.dto.OrdersDto;
import vn.tech.website.store.dto.OrdersSearchDto;
import vn.tech.website.store.repository.OrderRepositoryCustom;
import vn.tech.website.store.util.DbConstant;
import vn.tech.website.store.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<OrdersDto> search(OrdersSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "o.orders_id, "
                + "o.account_id, "
                + "o.customer_name, "
                + "o.code, "
                + "o.count_code, "
                + "o.address, "
                + "o.phone, "
                + "o.note, "
                + "o.total_amount, "
                + "o.shipping_id, "
                + "o.shipping, "
                + "o.status, "
                + "o.create_date, "
                + "o.create_by, "
                + "o.update_date, "
                + "o.update_by ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY o.orders_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("brandId")) {
                sb.append(" o.orders_id ");
            } else if (searchDto.getSortField().equals("brandName")) {
                sb.append(" o.customer_name collate utf8mb4_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("status")) {
                sb.append(" o.status ");
            } else if (searchDto.getSortField().equals("code")) {
                sb.append(" o.code ");
            } else if (searchDto.getSortField().equals("phone")) {
                sb.append(" o.phone ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY o.orders_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        query.setParameter("statusInit", DbConstant.ORDER_STATUS_PAID);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<OrdersDto> dtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            OrdersDto dto = new OrdersDto();
            dto.setOrdersId(ValueUtil.getLongByObject(obj[0]));
            dto.setAccountId(ValueUtil.getLongByObject(obj[1]));
            dto.setCustomerName(ValueUtil.getStringByObject(obj[2]));
            dto.setCode(ValueUtil.getStringByObject(obj[3]));
            dto.setCountCode(ValueUtil.getLongByObject(obj[4]));
            dto.setAddress(ValueUtil.getStringByObject(obj[5]));
            dto.setPhone(ValueUtil.getStringByObject(obj[6]));
            dto.setNote(ValueUtil.getStringByObject(obj[7]));
            dto.setTotalAmount(ValueUtil.getDoubleByObject(obj[8]));
            dto.setShippingId(ValueUtil.getLongByObject(obj[9]));
            dto.setShipping(ValueUtil.getDoubleByObject(obj[10]));
            dto.setAllTotalAmount(dto.getTotalAmount() + dto.getShipping());
            dto.setStatus(ValueUtil.getIntegerByObject(obj[11]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[12]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[13]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[14]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[15]));
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public Long countSearch(OrdersSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(o.orders_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        query.setParameter("statusInit", DbConstant.ORDER_STATUS_PAID);
        return Long.valueOf(query.getSingleResult().toString());
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, OrdersSearchDto searchDto) {
        sb.append(" FROM orders o " +
                " LEFT JOIN account acc ON o.account_id = acc.account_id ");
        sb.append(" WHERE 1=1 AND o.status <> " + DbConstant.ORDER_STATUS_CANCEL);
        if (searchDto.getStatusInit() == DbConstant.ORDER_TYPE_ORDER) {
            sb.append(" AND o.status <> :statusInit ");
        }
        if (searchDto.getStatusInit() == DbConstant.ORDER_TYPE_BILL) {
            sb.append(" AND o.status = :statusInit ");
        }
        if (StringUtils.isNotBlank(searchDto.getCustomerName())) {
            sb.append(" AND o.customer_name LIKE :customerName ");
        }
        if (StringUtils.isNotBlank(searchDto.getCode())) {
            sb.append(" AND o.code LIKE :code ");
        }
        if (StringUtils.isNotBlank(searchDto.getPhone())) {
            sb.append(" AND o.phone LIKE :phone ");
        }
        if (searchDto.getStatus() != null) {
            sb.append(" AND o.status = :status ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, OrdersSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(searchDto.getCustomerName())) {
            query.setParameter("customerName", "%" + searchDto.getCustomerName().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getCode())) {
            query.setParameter("code", "%" + searchDto.getCode().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getPhone())) {
            query.setParameter("customerName", "%" + searchDto.getPhone().trim() + "%");
        }
        if (searchDto.getStatus() != null) {
            query.setParameter("status", searchDto.getStatus());
        }

        return query;
    }
}
