package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import vn.compedia.website.auction.dto.api.HistorySyncSearchDto;
import vn.compedia.website.auction.model.HistorySync;
import vn.compedia.website.auction.repository.HistorySyncRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Service
public class HistorySyncRepositoryImpl implements HistorySyncRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<HistorySync> search(HistorySyncSearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "n.history_sync_id, "
                + "n.create_date,"
                + "n.status_apply,"
                + "n.version_id ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY n.history_sync_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("createDate")) {
                sb.append(" n.create_date  ");
            } else  if (searchDto.getSortField().equals("versionId")) {
                sb.append(" n.version_id  ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY n.history_sync_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<HistorySync>historySyncList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            HistorySync dto = new HistorySync();
            dto.setHistorySyncId(ValueUtil.getLongByObject(obj[0]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[1]));
            dto.setStatusApply(ValueUtil.getLongByObject(obj[2]));
            dto.setVersionId(ValueUtil.getLongByObject(obj[3]));
            historySyncList.add(dto);
        }
        return historySyncList;
    }

    @Override
    public  Long countSearch(HistorySyncSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(n.history_sync_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, HistorySyncSearchDto searchDto) {
        sb.append(" FROM history_sync n ");
        sb.append(" WHERE 1 = 1 ");
    }

    private Query createQueryObjForSearch(StringBuilder sb,HistorySyncSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        return query;
    }
}
