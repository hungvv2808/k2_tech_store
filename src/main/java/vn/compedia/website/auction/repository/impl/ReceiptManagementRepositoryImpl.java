package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.payment.PaymentDto;
import vn.compedia.website.auction.dto.payment.ReceiptManagementDto;
import vn.compedia.website.auction.dto.payment.ReceiptManagementSearchDto;
import vn.compedia.website.auction.repository.ReceiptManagementRepositoryCustom;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReceiptManagementRepositoryImpl implements ReceiptManagementRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ReceiptManagementDto> search(ReceiptManagementSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("select rm.receipt_management_id, " +
                "       rm.payment_id, " +
                "       rm.code, " +
                "       rm.payer_fullname, " +
                "       rm.payer_address, " +
                "       rm.amount, " +
                "       rm.content_payment, " +
                "       rm.status, " +
                "       rm.create_date, " +
                "       p.code as paymentCode, " +
                "       pr.name as provinceName, " +
                "       d.name as districtName, " +
                "       c.name as communeName, " +
                "       p.status as paymentStatus, " +
                "       ass.name as assetName, " +
                "       r.code as regulationCode, " +
                "       ac.username," +
                "       rm.create_by ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" group by rm.receipt_management_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" order by ");
            if (searchDto.getSortField().equals("code")) {
                sb.append(" rm.code ");
            } else if (searchDto.getSortField().equals("payerFullname")) {
                sb.append(" rm.payer_fullname collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("payerAddress")) {
                sb.append(" pr.province_id ");
            } else if (searchDto.getSortField().equals("amount")) {
                sb.append(" rm.amount ");
            } else if (searchDto.getSortField().equals("contentPayment")) {
                sb.append(" rm.content_payment collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("createDate")) {
                sb.append(" rm.create_date ");
            } if (searchDto.getSortField().equals("status")) {
                sb.append(" rm.status ");
            } if (searchDto.getSortField().equals("paymentCode")) {
                sb.append(" p.code ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" order by rm.receipt_management_id desc ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<ReceiptManagementDto> receiptManagementDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] objects : result) {
            ReceiptManagementDto receiptManagementDto = new ReceiptManagementDto();
            receiptManagementDto.setReceiptManagementId(ValueUtil.getLongByObject(objects[0]));
            receiptManagementDto.setPaymentId(ValueUtil.getLongByObject(objects[1]));
            receiptManagementDto.setCode(ValueUtil.getStringByObject(objects[2]));
            receiptManagementDto.setPayerFullname(ValueUtil.getStringByObject(objects[3]));
            receiptManagementDto.setPayerAddress(ValueUtil.getStringByObject(objects[4]));
            receiptManagementDto.setAmount(ValueUtil.getLongByObject(objects[5]));
            receiptManagementDto.setContentPayment(ValueUtil.getStringByObject(objects[6]));
            receiptManagementDto.setStatus(ValueUtil.getBooleanByObject(objects[7]));
            receiptManagementDto.setCreateDate(ValueUtil.getDateByObject(objects[8]));
            receiptManagementDto.setPaymentCode(ValueUtil.getStringByObject(objects[9]));
            receiptManagementDto.setProvinceName(ValueUtil.getStringByObject(objects[10]));
            receiptManagementDto.setDistrictName(ValueUtil.getStringByObject(objects[11]));
            receiptManagementDto.setCommuneName(ValueUtil.getStringByObject(objects[12]));
            receiptManagementDto.setPaymentStatus(ValueUtil.getIntegerByObject(objects[13]));
            receiptManagementDto.setTitleBill(ValueUtil.getIntegerByObject(objects[13]) == DbConstant.PAYMENT_STATUS_PAID ? "thu tiền" : "trả tiền đặt trước");
            receiptManagementDto.setAssetName(ValueUtil.getStringByObject(objects[14]));
            receiptManagementDto.setRegulationCode(ValueUtil.getStringByObject(objects[15]));
            receiptManagementDto.setUsername(ValueUtil.getStringByObject(objects[16]));
            receiptManagementDto.setCreateBy(ValueUtil.getLongByObject(objects[17]));
            receiptManagementDtoList.add(receiptManagementDto);
        }
        return receiptManagementDtoList;
    }

    @Override
    public Long countSearch(ReceiptManagementSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(rm.receipt_management_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, ReceiptManagementSearchDto searchDto) {
        sb.append(" from receipt_management rm " +
                " inner join payment p on rm.payment_id = p.payment_id " +
                " inner join auction_register ar on p.auction_register_id = ar.auction_register_id " +
                " inner join asset ass on ar.asset_id = ass.asset_id " +
                " inner join regulation r on ar.regulation_id = r.regulation_id " +
                " inner join account ac on ar.account_id = ac.account_id " +
                " left join province pr on ac.province_id = pr.province_id " +
                " left join district d on ac.district_id = d.district_id " +
                " left join commune c on ac.commune_id = c.commune_id" +
                " where 1 = 1 " );
        if (searchDto.getPaymentId() != null){
            sb.append(" and rm.payment_id = :paymentId ");
        }
        if (searchDto.getStartDate() != null){
            sb.append(" and rm.create_date >= :startDate ");
        }
        if (searchDto.getEndDate() != null){
            sb.append(" and rm.create_date <= :endDate ");
        }
        if (searchDto.getStatus() != null) {
            sb.append(" and rm.status = :status ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, ReceiptManagementSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (searchDto.getPaymentId() != null) {
            query.setParameter("paymentId",  searchDto.getPaymentId());
        }
        if (searchDto.getStartDate() != null) {
            query.setParameter("startDate",  searchDto.getStartDate());
        }
        if (searchDto.getEndDate() != null) {
            query.setParameter("endDate",  searchDto.getEndDate());
        }
        if (searchDto.getStatus() != null) {
            query.setParameter("status",  searchDto.getStatus());
        }
        return query;
    }

    @Override
    public List<PaymentDto> searchPaymentReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("select p.payment_id             as 'payment_id', " +
                "       p.code                   as 'payment_code', " +
                "       p.auction_register_id    as 'payment_auction_register_id', " +
                "       p.money                  as 'payment_money', " +
                "       p.status                 as 'payment_status', " +
                "       p.payment_formality      as 'payment_formality', " +
                "       p.note                   as 'payment_note', " +
                "       p.file_path              as 'payment_file_path', " +
                "       p.file_name              as 'payment_file_name', " +
                "       p.send_bill_status       as 'payment_send_bill_status', " +
                "       p.receipt_file_path      as 'payment_receipt_file_path', " +
                "       rm.receipt_management_id as 'receipt_management_id', " +
                "       rm.code                  as 'receipt_code', " +
                "       rm.payer_fullname        as 'receipt_payer_fullname', " +
                "       rm.payer_address         as 'receipt_payer_address', " +
                "       rm.amount                as 'receipt_amount', " +
                "       rm.content_payment       as 'receipt_content_payment', " +
                "       rm.status                as 'receipt_status' " +
                "from payment p " +
                "         left join receipt_management rm on p.payment_id = rm.payment_id " +
                "where (p.status = " + DbConstant.PAYMENT_STATUS_PAID + " or p.status = " + DbConstant.PAYMENT_STATUS_REFUND + ") " +
                "order by p.payment_id desc");

        Query query = entityManager.createNativeQuery(sb.toString());

        List<PaymentDto> paymentList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            PaymentDto dto = new PaymentDto();
            dto.setPaymentId(ValueUtil.getLongByObject(obj[0]));
            dto.setCode(ValueUtil.getStringByObject(obj[1]));
            dto.setAuctionRegisterId(ValueUtil.getLongByObject(obj[2]));
            dto.setMoney(ValueUtil.getLongByObject(obj[3]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[4]));
            dto.setPaymentFormality(ValueUtil.getIntegerByObject(obj[5]));
            dto.setNote(ValueUtil.getStringByObject(obj[6]));
            dto.setFilePath(ValueUtil.getStringByObject(obj[7]));
            dto.setFileName(ValueUtil.getStringByObject(obj[8]));
            dto.setSendBillStatus(ValueUtil.getBooleanByObject(obj[9]));
            dto.setReceiptFilePath(ValueUtil.getStringByObject(obj[10]));
            dto.setReceiptManagementId(ValueUtil.getLongByObject(obj[11]));
            dto.setReceiptCode(ValueUtil.getStringByObject(obj[12]));
            dto.setReceiptPayerFullname(ValueUtil.getStringByObject(obj[13]));
            dto.setReceiptPayerAddress(ValueUtil.getStringByObject(obj[14]));
            dto.setReceiptAmount(ValueUtil.getLongByObject(obj[15]));
            dto.setReceiptContentPayment(ValueUtil.getStringByObject(obj[16]));
            dto.setReceiptStatus(ValueUtil.getBooleanByObject(obj[17]));
            paymentList.add(dto);
        }
        return paymentList;
    }
}
