package vn.compedia.website.auction.repository.impl;

import org.jsoup.internal.StringUtil;
import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.auction.BidDto;
import vn.compedia.website.auction.dto.auction.BidSearchDto;
import vn.compedia.website.auction.dto.export.ExportExcelDto;
import vn.compedia.website.auction.model.Bid;
import vn.compedia.website.auction.repository.BidRepositoryCustom;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BidRepositoryImpl implements BidRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<BidDto> search(BidSearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "b.bid_id,"
                + "b.price_round_id,"
                + "b.auction_register_id,"
                + "b.money,"
                + "ac.is_org, "
                + "ac.org_name, "
                + "ac.full_name, "
                + "pr.highest_price, "
                + "pr.number_of_round, "
                + "ar.status_deposit, "
                + "ar.code, "
                + "b.time, "
                + "b.status_retract ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY b.bid_id ");
        sb.append(" ORDER BY b.bid_id DESC");
        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<BidDto> bidDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            BidDto dto = new BidDto();
            dto.setBidId(ValueUtil.getLongByObject(obj[0]));
            dto.setPriceRoundId(ValueUtil.getLongByObject(obj[1]));
            dto.setAuctionRegisterId(ValueUtil.getLongByObject(obj[2]));
            dto.setMoney(ValueUtil.getLongByObject(obj[3]));
            if (ValueUtil.getBooleanByObject(obj[4])) {
                dto.setFullName(ValueUtil.getStringByObject(obj[5]));
            } else {
                dto.setFullName(ValueUtil.getStringByObject(obj[6]));
            }
            dto.setHighestPrice(ValueUtil.getLongByObject(obj[7]));
            dto.setNumberOfRound(ValueUtil.getLongByObject(obj[8]));
            dto.setStatusDeposit(ValueUtil.getIntegerByObject(obj[9]));
            dto.setCode(ValueUtil.getStringByObject(obj[10]));
            dto.setTime(ValueUtil.getDateByObject(obj[11]));
            dto.setStatusRetract(ValueUtil.getBooleanByObject(obj[12]));
            bidDtoList.add(dto);
        }
        return bidDtoList;
    }

    @Override
    public Long countSearch(BidSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(T.bid_id) ");
        sb.append(" FROM (SELECT b.bid_id ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY b.bid_id) AS T ");
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    // > 0 da tra gia
    @Override
    public List<Bid> findAllBargain(BidSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT b.* ");
        appendQueryFromAndWhereForFindAllBargain(sb, searchDto);
        Query query = createQueryObjForFindAllBargain(sb, searchDto);
        return query.getResultList();
    }

    @Override
    public List<ExportExcelDto> getHistoryBargain(Long regulationId) {
        StringBuilder sb = new StringBuilder();
        sb.append("select r.regulation_id, " +
                "       r.code, " +
                "       r.auctioneer_id, " +
                "       r.auction_formality_id, " +
                "       r.auction_method_id, " +
                "       r.start_time, " +
                "       r.end_time, " +
                "       acc.full_name, " +
                "       a.asset_id, " +
                "       a.name, " +
                "       pr.price_round_id, " +
                "       pr.starting_price, " +
                "       pr.highest_price, " +
                "       b.bid_id, " +
                "       b.auction_register_id, " +
                "       ac.account_id, " +
                "       ac.full_name as 'full_name_register', " +
                "       b.create_date, " +
                "       b.money " +
                "from regulation r " +
                "         inner join account acc on acc.account_id = r.auctioneer_id " +
                "         inner join asset a on r.regulation_id = a.regulation_id " +
                "         inner join price_round pr on a.asset_id = pr.asset_id " +
                "         inner join bid b on pr.price_round_id = b.price_round_id " +
                "         inner join auction_register ar on b.auction_register_id = ar.auction_register_id " +
                "         inner join account ac on ar.account_id = ac.account_id " +
                "where r.regulation_id = :regulationId " +
                "order by b.create_date desc ");
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("regulationId", regulationId);
        List<ExportExcelDto> list = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ExportExcelDto dto = new ExportExcelDto();
            dto.setRegulationId(ValueUtil.getLongByObject(obj[0]));
            dto.setCode(ValueUtil.getStringByObject(obj[1]));
            dto.setAuctioneerId(ValueUtil.getLongByObject(obj[2]));
            dto.setAuctionFormalityId(ValueUtil.getLongByObject(obj[3]));
            dto.setAuctionMethodId(ValueUtil.getLongByObject(obj[4]));
            dto.setStartTime(ValueUtil.getDateByObject(obj[5]));
            dto.setRealEndTime(ValueUtil.getDateByObject(obj[6]));
            dto.setFullNameAuctioneer(ValueUtil.getStringByObject(obj[7]));
            dto.setAssetId(ValueUtil.getLongByObject(obj[8]));
            dto.setAssetName(ValueUtil.getStringByObject(obj[9]));
            dto.setPriceRoundId(ValueUtil.getLongByObject(obj[10]));
            dto.setStartingPrice(ValueUtil.getLongByObject(obj[11]));
            dto.setHighestPrice(ValueUtil.getLongByObject(obj[12]));
            dto.setBidId(ValueUtil.getLongByObject(obj[13]));
            dto.setAuctionRegisterId(ValueUtil.getLongByObject(obj[14]));
            dto.setRegisterAccountId(ValueUtil.getLongByObject(obj[15]));
            dto.setFullNameRegister(ValueUtil.getStringByObject(obj[16]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[17]));
            dto.setMoney(ValueUtil.getLongByObject(obj[18]));
            list.add(dto);
        }
        return list;
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, BidSearchDto searchDto) {
        sb.append(" FROM bid b, price_round pr, auction_register ar, account ac  ");
        sb.append(" WHERE b.price_round_id = pr.price_round_id " +
                "  AND b.auction_register_id = ar.auction_register_id " +
                "  AND ar.account_id = ac.account_id AND ar.asset_id = b.asset_id");
        if (searchDto.getAssetId() != null) {
            sb.append(" AND b.asset_id = :asset_id ");
        }
        if (!StringUtil.isBlank(searchDto.getKeyword())) {
            sb.append(" AND (ac.full_name LIKE :keyword OR ar.code LIKE :keyword OR ac.org_name LIKE :keyword) ");
        }
        if (searchDto.getMaxNumberOfRound() != null) {
            sb.append(" AND pr.number_of_round <= :maxNumberOfRound ");
        }
    }

    private void appendQueryFromAndWhereForFindAllBargain(StringBuilder sb, BidSearchDto searchDto) {
        sb.append("FROM bid b "
                + "INNER JOIN price_round pr ON pr.price_round_id = b.price_round_id ");
        sb.append("WHERE pr.asset_id = :asset_id ");
        if (searchDto.getAuctionRegisterId() != null) {
            sb.append("AND b.auction_register_id = :auction_register_id ");
        }
        sb.append("ORDER BY b.money DESC, b.bid_id ASC ");
    }

    private Query createQueryObjForSearch(StringBuilder sb, BidSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (searchDto.getAssetId() != null) {
            query.setParameter("asset_id", searchDto.getAssetId());
        }
        if (!StringUtil.isBlank(searchDto.getKeyword())) {
            query.setParameter("keyword","%"+ searchDto.getKeyword().trim() + "%");
        }
        if (searchDto.getMaxNumberOfRound() != null) {
            query.setParameter("maxNumberOfRound", searchDto.getMaxNumberOfRound());
        }
        return query;
    }

    private Query createQueryObjForFindAllBargain(StringBuilder sb, BidSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString(), Bid.class);
        if (searchDto.getAssetId() != null) {
            query.setParameter("asset_id", searchDto.getAssetId());
        }
        if (searchDto.getAuctionRegisterId() != null) {
            query.setParameter("auction_register_id", searchDto.getAuctionRegisterId());
        }
        return query;
    }

    @Override
    public List<BidDto> searchForHistoryFormalityDirectUp(BidSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" Select b.time, b.bid_id, b.price_round_id, b.auction_register_id, b.asset_id, b.money, ac.full_name, ar.code, pr.number_of_round ");
        createQueryForHistoryFormalityDirectUp(sb);
        List<BidDto> list = new ArrayList<>();
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("assetId", searchDto.getAssetId());

        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        //query.setParameter("statusRetract", DbConstant.BID_STATUS_RETRACT_FALSE);
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            BidDto dto = new BidDto();
            dto.setTime(ValueUtil.getDateByObject(obj[0]));
            dto.setBidId(ValueUtil.getLongByObject(obj[1]));
            dto.setPriceRoundId(ValueUtil.getLongByObject(obj[2]));
            dto.setAuctionRegisterId(ValueUtil.getLongByObject(obj[3]));
            dto.setAssetId(ValueUtil.getLongByObject(obj[4]));
            dto.setMoney(ValueUtil.getLongByObject(obj[5]));
            dto.setFullName(ValueUtil.getStringByObject(obj[6]));
            dto.setCode(ValueUtil.getStringByObject(obj[7]));
            dto.setNumberOfRound(ValueUtil.getLongByObject(obj[8]));
            list.add(dto);
        }
        return list;
    }

    @Override
    public Long countSearchForHistoryFormalityDirectUp(BidSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(b.bid_id) ");
        createQueryForHistoryFormalityDirectUp(sb);
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("assetId", searchDto.getAssetId());
//        query.setParameter("statusRetract", DbConstant.BID_STATUS_RETRACT_FALSE);
        return Long.valueOf(query.getSingleResult().toString());
    }

    public void createQueryForHistoryFormalityDirectUp(StringBuilder sb) {
        sb.append("from bid b inner join price_round pr on b.price_round_id = pr.price_round_id " +
                "inner join auction_register ar on b.auction_register_id = ar.auction_register_id " +
                "inner join asset ass on b.asset_id = ass.asset_id " +
                "inner join account ac on ar.account_id = ac.account_id " +
                "where b.asset_id = :assetId " +
                "ORDER BY b.bid_id DESC ");
    }
}
