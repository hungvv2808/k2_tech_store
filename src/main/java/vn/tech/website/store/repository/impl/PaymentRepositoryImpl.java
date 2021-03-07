package vn.tech.website.store.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import vn.tech.website.store.dto.payment.PaymentDto;
import vn.tech.website.store.dto.payment.PaymentSearchDto;
import vn.tech.website.store.repository.PaymentRepositoryCustom;
import vn.tech.website.store.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PaymentDto> search(PaymentSearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "n.payment_id, "
                + "acc.is_org, "
                + "acc.full_name, "
                + "acc.org_name, "
                + "ass.name, "
                + "n.money, "
                + "n.payment_formality, "
                + "n.note, "
                + "n.status, "
                + "n.file_path, "
                + "n.create_date, "
                + "acc.address, "
                + "acc.org_address, "
                + "acc.email, "
                + "n.store_register_id, "
                + "n.send_bill_status, "
                + "n.code, "
                + "rl.code as regulationCode, "
                + "ass.asset_id, "
                + "rl.regulation_id, "
                + "n.file_name ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY n.payment_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("money")) {
                sb.append(" n.money ");
            } else if (searchDto.getSortField().equals("fullNameNguoiDangKy")) {
                sb.append(" acc.full_name ");
            }else if (searchDto.getSortField().equals("assetName")) {
                sb.append(" ass.name collate utf8mb4_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("paymentFormality")) {
                sb.append(" n.payment_formality ");
            } else if (searchDto.getSortField().equals("code")) {
                sb.append(" n.code ");
            } else if (searchDto.getSortField().equals("status")) {
                sb.append(" n.status ");
            } else if (searchDto.getSortField().equals("createDate")) {
                sb.append(" n.create_date ");
            } else if (searchDto.getSortField().equals("regulationCode")) {
                sb.append(" rl.code ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY n.payment_id DESC ");
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

            if (!ValueUtil.getBooleanByObject(obj[1])) {
                dto.setFullNameNguoiDangKy(ValueUtil.getStringByObject(obj[2]));
            } else {
                dto.setFullNameNguoiDangKy(ValueUtil.getStringByObject(obj[3]));
            }
            dto.setAssetName(ValueUtil.getStringByObject(obj[4]));
            dto.setMoney(ValueUtil.getLongByObject(obj[5]));
            dto.setPaymentFormality(ValueUtil.getIntegerByObject(obj[6]));
            dto.setNote(ValueUtil.getStringByObject(obj[7]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[8]));
            dto.setFilePath(ValueUtil.getStringByObject(obj[9]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[10]));
            dto.setAddress(ValueUtil.getStringByObject(obj[11]));
            if(StringUtils.isBlank(dto.getAddress())){
                dto.setAddress(ValueUtil.getStringByObject(obj[12]));
            }
            dto.setMail(ValueUtil.getStringByObject(obj[13]));
            dto.setStoreRegisterId(ValueUtil.getLongByObject(obj[14]));
            dto.setSendBillStatus(ValueUtil.getBooleanByObject(obj[15]));
            dto.setCode(ValueUtil.getStringByObject(obj[16]));
            dto.setRegulationCode(ValueUtil.getStringByObject(obj[17]));
            dto.setAssetId(ValueUtil.getLongByObject(obj[18]));
            dto.setRegulationId(ValueUtil.getLongByObject(obj[19]));
            dto.setFileName(ValueUtil.getStringByObject(obj[20]));
            paymentList.add(dto);
        }
        return paymentList;
    }

    @Override
    public Long countSearch(PaymentSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(n.payment_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, PaymentSearchDto searchDto) {
        sb.append(" FROM payment n, account acc, asset ass, store_register ar, regulation rl ");
        sb.append(" WHERE ar.asset_id = ass.asset_id AND n.store_register_id = ar.store_register_id AND ar.account_id = acc.account_id and ar.regulation_id = rl.regulation_id and 1 = 1 ");
        if (searchDto.getAccountId() != null) {
            sb.append(" AND acc.account_id = :accountId ");
        }
        if (searchDto.getAssetId() != null) {
            sb.append(" AND ass.asset_id = :assetId ");
        }
        if (searchDto.getPaymentFormality() != null) {
            sb.append(" AND n.payment_formality = :paymentFormality ");
        }
        if (searchDto.getFromDate() != null) {
            sb.append(" AND n.create_date >= :fromDate ");
        }
        if (searchDto.getToDate() != null) {
            sb.append(" AND n.create_date <= :toDate ");
        }
        if (searchDto.getStatus() != null) {
            sb.append(" AND n.status = :status ");
        } else if (searchDto.getStatusList() != null) {
            sb.append(" AND n.status IN (:statusList) ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, PaymentSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (searchDto.getAccountId() != null) {
            query.setParameter("accountId",  searchDto.getAccountId());
        }
        if (searchDto.getAssetId() != null) {
            query.setParameter("assetId",  searchDto.getAssetId());
        }
        if (searchDto.getPaymentFormality() != null) {
            query.setParameter("paymentFormality",  searchDto.getPaymentFormality());
        }
        if (searchDto.getFromDate() != null) {
            query.setParameter("fromDate",  searchDto.getFromDate());
        }
        if (searchDto.getToDate() != null) {
            query.setParameter("toDate",  searchDto.getToDate());
        }
        if (searchDto.getStatus() != null) {
            query.setParameter("status",  searchDto.getStatus());
        } else if (searchDto.getStatusList() != null) {
            query.setParameter("statusList",  searchDto.getStatusList());
        }
        return query;
    }

}

