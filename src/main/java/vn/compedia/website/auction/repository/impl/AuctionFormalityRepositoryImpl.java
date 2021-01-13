package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.manage.AuctionFormalitySearchDto;
import vn.compedia.website.auction.model.AuctionFormality;
import vn.compedia.website.auction.repository.AuctionFormalityRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AuctionFormalityRepositoryImpl implements AuctionFormalityRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AuctionFormality> search(AuctionFormalitySearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "n.auction_formality_id,"
                + " n.code, "
                + "n.name,"
                + " n.status, "
                + "n.create_date,"
                +"n.update_date ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY n.auction_formality_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("code")) {
                sb.append(" n.code ");
            } else if (searchDto.getSortField().equals("name")) {
                sb.append(" n.name collate utf8_vietnamese_ci ");
            }else if (searchDto.getSortField().equals("status")) {
                sb.append(" n.status ");
            } else if (searchDto.getSortField().equals("updateDate")) {
                sb.append(" n.update_date ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY n.auction_formality_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<AuctionFormality> auctionFormalityList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            AuctionFormality dto = new AuctionFormality();
            dto.setAuctionFormalityId(ValueUtil.getLongByObject(obj[0]));
            dto.setCode(ValueUtil.getStringByObject(obj[1]));
            dto.setName(ValueUtil.getStringByObject(obj[2]));
            dto.setStatus(ValueUtil.getBooleanByObject(obj[3]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[4]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[5]));

            auctionFormalityList.add(dto);
        }
        return auctionFormalityList;
    }


    @Override
    public Long countSearch(AuctionFormalitySearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(n.auction_formality_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());

    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, AuctionFormalitySearchDto searchDto) {
        sb.append(" FROM auction_formality n ");
        sb.append(" WHERE 1 = 1 ");

        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND (n.code LIKE :keyword OR n.name LIKE :keyword) ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, AuctionFormalitySearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword().trim() + "%");
        }

        return query;
    }
}
