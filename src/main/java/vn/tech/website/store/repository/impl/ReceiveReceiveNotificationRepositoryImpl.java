package vn.tech.website.store.repository.impl;

import vn.tech.website.store.dto.NotificationSearchDto;
import vn.tech.website.store.dto.ReceiveNotificationDto;
import vn.tech.website.store.repository.ReceiveNotificationRepositoryCustom;
import vn.tech.website.store.util.DateUtil;
import vn.tech.website.store.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReceiveReceiveNotificationRepositoryImpl implements ReceiveNotificationRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

//    @Override
//    public List<ReceiveNotificationDto> search(NotificationSearchDto searchDto) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("select sn.send_notification_id    as 'sendNotificationId', " +
//                "       sn.account_id              as 'sendAccountId', " +
//                "       accs.full_name             as 'senderName', " +
//                "       sn.content                 as 'content', " +
//                "       rn.receive_notification_id as 'receiveNotificationId', " +
//                "       rn.account_id              as 'receiveAccountId', " +
//                "       accr.full_name             as 'receiverName', " +
//                "       rn.status                  as 'receiveStatus', " +
//                "       rn.create_date             as 'receiveCreateDate', " +
//                "       rn.create_by               as 'receiveCreateBy', " +
//                "       rn.status_bell             as 'receiveStatusBell' ");
//        appendQueryFromAndWhereForSearch(sb, searchDto);
//        sb.append(" order by rn.receive_notification_id desc ");
//
//        Query query = createQueryObjForSearch(sb, searchDto);
//        if (searchDto.getPageSize() > 0) {
//            query.setFirstResult(searchDto.getPageIndex());
//            query.setMaxResults(searchDto.getPageSize());
//        } else {
//            query.setFirstResult(0);
//            query.setMaxResults(Integer.MAX_VALUE);
//        }
//
//        List<ReceiveNotificationDto> notificationDtoList = new ArrayList<>();
//        List<Object[]> result = query.getResultList();
//        for (Object[] obj : result) {
//            ReceiveNotificationDto dto = new ReceiveNotificationDto();
//            dto.setSendNotificationId(ValueUtil.getLongByObject(obj[0]));
//            dto.setAccountId(ValueUtil.getLongByObject(obj[1]));
//            dto.setSenderName(ValueUtil.getStringByObject(obj[2]));
//            dto.setContent(ValueUtil.getStringByObject(obj[3]));
//            dto.setReceiveNotificationId(ValueUtil.getLongByObject(obj[4]));
//            dto.setAccountId(ValueUtil.getLongByObject(obj[5]));
//            dto.setReceiverName(ValueUtil.getStringByObject(obj[6]));
//            dto.setStatus(ValueUtil.getBooleanByObject(obj[7]));
//            Date dateBeforeNow = ValueUtil.getDateByObject(obj[8]);
//            dto.setCreateDate(dateBeforeNow);
//            dto.setDateBeforeNow(dateBeforeNow != null ? DateUtil.countTimeBefore(dateBeforeNow) + " " + DateUtil.typeTime + " trước" : "");
//            dto.setStatusBell(ValueUtil.getBooleanByObject(obj[9]));
//            notificationDtoList.add(dto);
//        }
//        return notificationDtoList;
//    }
//
//    @Override
//    public Long countRecordNotSeen(NotificationSearchDto searchDto) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("select count(rn.receive_notification_id) ");
//        appendQueryFromAndWhereForSearch(sb, searchDto);
//        Query query = createQueryObjForSearch(sb, searchDto);
//        return Long.valueOf(query.getSingleResult().toString());
//    }
//
//    private void appendQueryFromAndWhereForSearch(StringBuilder sb, NotificationSearchDto searchDto) {
//        sb.append(" from receive_notification rn " +
//                "         inner join send_notification sn on sn.send_notification_id = rn.send_notification_id " +
//                "         left join account accr on rn.account_id = accr.account_id " +
//                "         left join account accs on sn.account_id = accs.account_id " +
//                "where 1 = 1 ");
//
//        if (searchDto.getAccountId() != null) {
//            sb.append(" and rn.account_id = :accountId ");
//        }
//        if (searchDto.getReceiveNotificationLastId() != null) {
//            sb.append(" and rn.receive_notification_id < :receiveNotificationLastId ");
//        }
//    }
//
//    private Query createQueryObjForSearch(StringBuilder sb, NotificationSearchDto searchDto) {
//        Query query = entityManager.createNativeQuery(sb.toString());
//
//        if (searchDto.getAccountId() != null) {
//            query.setParameter("accountId", searchDto.getAccountId());
//        }
//
//        if (searchDto.getReceiveNotificationLastId() != null) {
//            query.setParameter("receiveNotificationLastId", searchDto.getReceiveNotificationLastId());
//        }
//
//        return query;
//    }
}
