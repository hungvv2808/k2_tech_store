package vn.tech.website.store.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import vn.tech.website.store.dto.ChartModelDto;
import vn.tech.website.store.dto.payment.PaymentDto;
import vn.tech.website.store.dto.payment.PaymentSearchDto;
import vn.tech.website.store.entity.EntityMapper;
import vn.tech.website.store.repository.PaymentsRepositoryCustom;
import vn.tech.website.store.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentsRepositoryImpl implements PaymentsRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PaymentDto> search(PaymentSearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "p.payment_id, "
                + "p.orders_id, "
                + "p.code, "
                + "p.total_amount as 'totalAmount', "
                + "o.account_id, "
                + "acc.user_name, "
                + "o.customer_name, "
                + "o.address, "
                + "o.phone, "
                + "o.email, "
                + "o.note, "
                + "o.total_amount as 'amountProduct', "
                + "o.shipping_id, "
                + "o.shipping, "
                + "p.create_date,"
                + "s.name as shippingName ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY p.payment_id ");
        if (searchDto.getSortField() != null) {
            if (searchDto.getSortField().equals("customerName")) {
                sb.append(" ,o.customer_name ");
            }
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("customerName")) {
                sb.append(" o.customer_name ");
            } else if (searchDto.getSortField().equals("code")) {
                sb.append(" p.code ");
            } else if (searchDto.getSortField().equals("totalAmount")) {
                sb.append(" p.total_amount ");
            } else if (searchDto.getSortField().equals("createDate")) {
                sb.append(" p.create_date ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY p.payment_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }
        List<PaymentDto> paymentList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            PaymentDto dto = new PaymentDto();
            dto.setPaymentId(ValueUtil.getLongByObject(obj[0]));
            dto.setOrdersId(ValueUtil.getLongByObject(obj[1]));
            dto.getOrdersDto().setOrdersId(ValueUtil.getLongByObject(obj[1]));
            dto.setCode(ValueUtil.getStringByObject(obj[2]));
            dto.setTotalAmount(ValueUtil.getDoubleByObject(obj[3]));
            dto.getOrdersDto().setAccountId(ValueUtil.getLongByObject(obj[4]));
            dto.setUserName(ValueUtil.getStringByObject(obj[5]));
            dto.getOrdersDto().setCustomerName(ValueUtil.getStringByObject(obj[6]));
            dto.getOrdersDto().setAddress(ValueUtil.getStringByObject(obj[7]));
            dto.getOrdersDto().setPhone(ValueUtil.getStringByObject(obj[8]));
            dto.getOrdersDto().setEmail(ValueUtil.getStringByObject(obj[9]));
            dto.getOrdersDto().setNote(ValueUtil.getStringByObject(obj[10]));
            dto.setAmountProduct(ValueUtil.getDoubleByObject(obj[11]));
            dto.getOrdersDto().setShippingId(ValueUtil.getLongByObject(obj[12]));
            dto.getOrdersDto().setShipping(ValueUtil.getDoubleByObject(obj[13]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[14]));
            dto.setShippingName(ValueUtil.getStringByObject(obj[15]));
            paymentList.add(dto);
        }
        return paymentList;
    }

    @Override
    public Long countSearch(PaymentSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(p.payment_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, PaymentSearchDto searchDto) {
        sb.append(" FROM payments p " +
                "   INNER JOIN orders o ON p.orders_id = o.orders_id" +
                "   LEFT JOIN account acc ON o.account_id = acc.account_id" +
                "   INNER JOIN shipping s ON o.shipping_id = s.shipping_id ");
        sb.append(" WHERE 1 = 1 ");
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND (o.customer_name LIKE :keyword OR o.code LIKE :keyword) ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, PaymentSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword().trim() + "%");
        }
        return query;
    }

    @Override
    public List getDataModelChart(Integer year, boolean countDetail, String condition) {
        StringBuilder sb = new StringBuilder();
        sb.append("select distinct " + (!countDetail ? " p2_df.category_id as categoryId, " : "") +
                "    (select sum(p_1.total_amount) from payments p_1 where month(p_1.create_date) = 1 and year(p_1.create_date) = year(p_df.create_date)) as totalJanuary, " +
                "    (select sum(p_2.total_amount) from payments p_2 where month(p_2.create_date) = 2 and year(p_2.create_date) = year(p_df.create_date)) as totalFebruary, " +
                "    (select sum(p_3.total_amount) from payments p_3 where month(p_3.create_date) = 3 and year(p_3.create_date) = year(p_df.create_date)) as totalMarch, " +
                "    (select sum(p_4.total_amount) from payments p_4 where month(p_4.create_date) = 4 and year(p_4.create_date) = year(p_df.create_date)) as totalApril, " +
                "    (select sum(p_5.total_amount) from payments p_5 where month(p_5.create_date) = 5 and year(p_5.create_date) = year(p_df.create_date)) as totalMay, " +
                "    (select sum(p_6.total_amount) from payments p_6 where month(p_6.create_date) = 6 and year(p_6.create_date) = year(p_df.create_date)) as totalJune, " +
                "    (select sum(p_7.total_amount) from payments p_7 where month(p_7.create_date) = 7 and year(p_7.create_date) = year(p_df.create_date)) as totalJuly, " +
                "    (select sum(p_8.total_amount) from payments p_8 where month(p_8.create_date) = 8 and year(p_8.create_date) = year(p_df.create_date)) as totalAugust, " +
                "    (select sum(p_9.total_amount) from payments p_9 where month(p_9.create_date) = 9 and year(p_9.create_date) = year(p_df.create_date)) as totalSeptember, " +
                "    (select sum(p_10.total_amount) from payments p_10 where month(p_10.create_date) = 10 and year(p_10.create_date) = year(p_df.create_date)) as totalOctober, " +
                "    (select sum(p_11.total_amount) from payments p_11 where month(p_11.create_date) = 11 and year(p_11.create_date) = year(p_df.create_date)) as totalNovember, " +
                "    (select sum(p_12.total_amount) from payments p_12 where month(p_12.create_date) = 12 and year(p_12.create_date) = year(p_df.create_date)) as totalDecember " +
                "from payments p_df " +
                (!countDetail ? " inner join order_detail od_df on p_df.orders_id = od_df.orders_id inner join product p2_df on od_df.product_id = p2_df.product_id " : "") +
                "where year(p_df.create_date) = :year " + (!countDetail ? " and p2_df.category_id in " + condition : ""));
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("year", year);
        return EntityMapper.mapper(query, sb.toString(), ChartModelDto.class);
    }

}

