package vn.compedia.website.auction.repository.impl;

import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.auction.AuctionRegisterSearchDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.repository.AuctionHistoryRepositoryCustom;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Repository
public class AuctionHistoryRepositoryImpl implements AuctionHistoryRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RegulationDto> search(AuctionRegisterSearchDto auctionRegisterSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "r.regulation_id, "
                + "r.code as regulationCode, "
                + "a.asset_id, "
                + "a.name as assetName, "
                + "r.start_time, "
                + "r.end_time, "
                + "pr.number_of_round, "
                + "a.starting_price as startingPrice1, "
                + "a.price_step, "
                + "a.status as 'asset_status', "
                + "r.number_of_rounds, "
                + "af.name as afName, "
                + "am.name as amName, "
                + "ar.status as auctionRegisterStatus, "
                + "pr.starting_price as startingPrice2, "
                + "pr.price_round_id,"
                + "ar.auction_register_id, "
                + "r.auction_formality_id, "
                + "r.auction_method_id, "
                + "aMng.ending, "
                + "aMng.money, "
                + "r.retract_price, "
                + "r.payment_start_time, "
                + "r.payment_end_time, "
                + "ar.status_refund, "
                + "aMng1.ending as endingFinal ");
        appendQueryFromAndWhereForSearch(sb, auctionRegisterSearchDto);

        if (auctionRegisterSearchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (auctionRegisterSearchDto.getSortField().equals("regulationCode")) {
                sb.append(" r.code ");
            }
            sb.append(auctionRegisterSearchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY ar.auction_register_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, auctionRegisterSearchDto);
        if (auctionRegisterSearchDto.getPageSize() > 0) {
            query.setFirstResult(auctionRegisterSearchDto.getPageIndex());
            query.setMaxResults(auctionRegisterSearchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<RegulationDto> regulationDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        // list temp
        if (result != null && !result.isEmpty()) {
            List<String> temp = new ArrayList<>();
            RegulationDto regulationDto = new RegulationDto();
            List<AssetDto> assetDtoList = new ArrayList<>();
            for (Object[] obj : result) {
                Long regulationId = ValueUtil.getLongByObject(obj[0]);
                String regulationCode = ValueUtil.getStringByObject(obj[1]);
                Long assetId = ValueUtil.getLongByObject(obj[2]);
                String assetName = ValueUtil.getStringByObject(obj[3]);
                Date startTime = ValueUtil.getDateByObject(obj[4]);
                Date endTime = ValueUtil.getDateByObject(obj[5]);
                Integer currentRound = ValueUtil.getIntegerByObject(obj[6]);
                Long startingPrice = ValueUtil.getLongByObject(obj[7]);
                Long priceStep = ValueUtil.getLongByObject(obj[8]);
                Integer status = ValueUtil.getIntegerByObject(obj[9]);
                Integer numberOfRounds = ValueUtil.getIntegerByObject(obj[10]);
                String afName = ValueUtil.getStringByObject(obj[11]);
                String amName = ValueUtil.getStringByObject(obj[12]);
                Integer auctionRegisterStatus = ValueUtil.getIntegerByObject(obj[13]);
                if (ValueUtil.getLongByObject(obj[14]) != null) {
                    startingPrice = ValueUtil.getLongByObject(obj[14]);
                }
                Long priceRoundId = ValueUtil.getLongByObject(obj[15]);
                Long auctionRegisterId = ValueUtil.getLongByObject(obj[16]);
                Long auctionFormalityId = ValueUtil.getLongByObject(obj[17]);
                Long auctionMethodId = ValueUtil.getLongByObject(obj[18]);
                boolean assetManagementEnding = ValueUtil.getBooleanByObject(obj[19]);
                Long highestPrice = ValueUtil.getLongByObject(obj[20]);
                boolean retractPrice = ValueUtil.getBooleanByObject(obj[21]);
                Date paymentStartTime = ValueUtil.getDateByObject(obj[22]);
                Date paymentEndTime = ValueUtil.getDateByObject(obj[23]);
                Integer statusRefund = ValueUtil.getIntegerByObject(obj[24]);
                boolean endingFinal = ValueUtil.getBooleanByObject(obj[25]);

                //init regulationDto
                if (!temp.contains(regulationId + regulationCode)) {
                    //add regulationDto to list
                    if (!temp.isEmpty()) {
                        assetDtoList.sort(Comparator.comparing(AssetDto::getAssetId));
                        regulationDto.setAssetDtoList(assetDtoList);
                        regulationDtoList.add(regulationDto);
                    }
                    regulationDto = new RegulationDto();
                    regulationDto.setRegulationId(regulationId);
                    regulationDto.setCode(regulationCode);
                    regulationDto.setStartTime(startTime);
                    regulationDto.setEndTime(endTime);
                    regulationDto.setNumberOfRounds(numberOfRounds);
                    regulationDto.setAuctionFormalityId(auctionFormalityId);
                    regulationDto.setAuctionFormalityName(afName);
                    regulationDto.setAuctionMethodId(auctionMethodId);
                    regulationDto.setAuctionMethodName(amName);
                    regulationDto.setAssetDtoList(new ArrayList<>());
                    temp.add(regulationId + regulationCode);
                    assetDtoList = new ArrayList<>();
                }
                //init assetDto
                assetDtoList.add(new AssetDto(assetId, assetName, startingPrice, priceStep, status, priceRoundId, currentRound, auctionRegisterStatus,
                        auctionRegisterId, auctionFormalityId, auctionMethodId, assetManagementEnding, endingFinal, highestPrice, retractPrice, paymentStartTime, paymentEndTime, statusRefund));
            }
            assetDtoList.sort(Comparator.comparing(AssetDto::getAssetId));
            regulationDto.setAssetDtoList(assetDtoList);
            regulationDtoList.add(regulationDto);
        }
        return regulationDtoList;
    }

    @Override
    public Long countSearch(AuctionRegisterSearchDto auctionRegisterSearchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(1) FROM ");
        sb.append("(SELECT COUNT(ar.auction_register_id) ");
        appendQueryFromAndWhereForSearch(sb, auctionRegisterSearchDto);
        sb.append(") as T ");
        Query query = createQueryObjForSearch(sb, auctionRegisterSearchDto);
        return Long.parseLong(query.getSingleResult().toString());
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, AuctionRegisterSearchDto auctionRegisterSearchDto) {
        sb.append("FROM auction_register ar "
                + "INNER JOIN regulation r ON r.regulation_id = ar.regulation_id "
                + "INNER JOIN auction_formality af ON r.auction_formality_id = af.auction_formality_id "
                + "INNER JOIN auction_method am ON r.auction_method_id = am.auction_method_id "
                + "INNER JOIN asset a ON a.asset_id = ar.asset_id "
                + "LEFT JOIN asset_management aMng ON ar.auction_register_id = aMng.auction_register_id "
                + "LEFT JOIN asset_management aMng1 ON ar.asset_id = aMng1.asset_id "
                + "LEFT JOIN price_round pr ON pr.price_round_id = " +
                            "(SELECT pr.price_round_id " +
                            "FROM price_round pr " +
                            "WHERE pr.asset_id = a.asset_id " +
                            "ORDER BY pr.number_of_round DESC " +
                            "LIMIT 1) "
                + "WHERE 1 = 1 ");
        sb.append("AND r.auction_status IN (:auction_status) ");
        if (auctionRegisterSearchDto.getAccountId() != null) {
            sb.append("AND ar.account_id = :account_id ");
        }
        if (auctionRegisterSearchDto.getRegulationId() != null) {
            sb.append("AND r.regulation_id = :regulation_id ");
        }

        if (auctionRegisterSearchDto.getGroupByFields() != null) {
            sb.append(" GROUP BY ").append(auctionRegisterSearchDto.getGroupByFields()).append(" ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, AuctionRegisterSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (searchDto.getRegulationStatus() != null) {
            query.setParameter("auction_status", Collections.singletonList(searchDto.getRegulationStatus()));
        } else {
            query.setParameter("auction_status", Arrays.asList(
                    DbConstant.REGULATION_AUCTION_STATUS_WAITING,
                    DbConstant.REGULATION_AUCTION_STATUS_PLAYING
            ));
        }
        if (searchDto.getAccountId() != null) {
            query.setParameter("account_id", searchDto.getAccountId());
        }
        if (searchDto.getRegulationId() != null) {
            query.setParameter("regulation_id", searchDto.getRegulationId());
        }
        return query;
    }
}
