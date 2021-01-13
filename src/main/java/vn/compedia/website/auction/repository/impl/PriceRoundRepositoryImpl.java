package vn.compedia.website.auction.repository.impl;

import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.auction.PriceRoundDto;
import vn.compedia.website.auction.dto.auction.PriceRoundSearchDto;
import vn.compedia.website.auction.repository.PriceRoundRepositoryCustom;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PriceRoundRepositoryImpl implements PriceRoundRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<PriceRoundDto> searchForUser(PriceRoundSearchDto priceRoundSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT a.asset_id, "
                + "r.start_time, "
                + "r.time_per_round, "
                + "pr.number_of_round, "
                + "pr.highest_price, "
                + "pr.update_date ");
        appendQueryForUser(sb,priceRoundSearchDto);
        Query query = createQueryObjForUser(sb, priceRoundSearchDto);
        if (priceRoundSearchDto.getPageSize() > 0) {
            query.setFirstResult(priceRoundSearchDto.getPageIndex());
            query.setMaxResults(priceRoundSearchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<PriceRoundDto> priceRoundDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            PriceRoundDto dto = new PriceRoundDto();
            dto.setAssetId(ValueUtil.getLongByObject(obj[0]));
            dto.setStartTime(ValueUtil.getDateByObject(obj[1]));
            dto.setTimePerRound(ValueUtil.getIntegerByObject(obj[2]));
            dto.setNumberOfRound(ValueUtil.getIntegerByObject(obj[3]));
            dto.setHighestPrice(ValueUtil.getLongByObject(obj[4]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[5]));
            dto.setCurrentTime(DateUtil.plusMinute(dto.getStartTime(), dto.getNumberOfRound()*dto.getTimePerRound()));
            priceRoundDtoList.add(dto);
        }
        return priceRoundDtoList;
    }

    @Override
    public BigInteger countSearchForUser(PriceRoundSearchDto priceRoundSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(a.asset_id) ");
        appendQueryForUser(sb, priceRoundSearchDto);
        Query query = createQueryObjForUser(sb, priceRoundSearchDto);
        return (BigInteger) query.getSingleResult();
    }

    public void appendQueryForUser(StringBuilder sb, PriceRoundSearchDto searchDto){
        sb.append("FROM price_round pr "
                + "INNER JOIN asset a ON a.asset_id = pr.asset_id "
                + "INNER JOIN regulation r ON r.regulation_id = pr.regulation_id ");
        sb.append("WHERE 1 = 1 ");
        if (searchDto.getAssetId() != null) {
            sb.append("AND pr.asset_id = :assetId ");
        }
        if (searchDto.isStateEnd()) {
            sb.append("AND pr.create_date <> pr.update_date ");
        }
        sb.append("ORDER BY pr.price_round_id DESC ");
    }
    public Query createQueryObjForUser(StringBuilder sb, PriceRoundSearchDto searchDto){
        Query query = entityManager.createNativeQuery(sb.toString());
        if (searchDto.getAssetId() != null) {
            query.setParameter("assetId", searchDto.getAssetId());
        }
        return query;
    }
}
