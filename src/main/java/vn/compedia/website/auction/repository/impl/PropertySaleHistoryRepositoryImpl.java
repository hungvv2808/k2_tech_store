package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.dto.user.PropertySaleHistoryDto;
import vn.compedia.website.auction.dto.user.PropertySaleHistorySearchDto;
import vn.compedia.website.auction.repository.PropertySaleHistoryRepositoryCustom;
import vn.compedia.website.auction.repository.RegulationRepository;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;

public class PropertySaleHistoryRepositoryImpl implements PropertySaleHistoryRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RegulationRepository regulationRepository;

    @Override
    public List<PropertySaleHistoryDto> search(PropertySaleHistorySearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("select tmp.auction_id                as 'auction_id', " +
                "       tmp.auction_date              as 'auction_date', " +
                "       tmp.auction_account_id        as 'auction_account_id', " +
                "       tmp.type                      as 'type', " +
                "       tmp.asset_id                  as 'asset_id', " +
                "       tmp.asset_name                as 'asset_name', " +
                "       tmp.auction_status            as 'auction_status', " +
                "       tmp.asset_status              as 'asset_status', " +
                "       tmp.regulation_id             as 'regulation_id', " +
                "       tmp.regulation_name           as 'regulation_code', " +
                "       tmp.regulation_round          as 'regulation_number_of_rounds', " +
                "       tmp.regulation_start_time     as 'regulation_start_time', " +
                "       tmp.regulation_end_time       as 'regulation_end_time', " +
                "       tmp.time_per_round            as 'time_per_round', " +
                "       tmp.regulation_file_id        as 'regulation_file_id', " +
                "       tmp.file_path                 as 'file_path', " +
                "       tmp.auction_formality_id      as 'auction_formality_id', " +
                "       tmp.auction_formality_name    as 'auction_formality_name', " +
                "       tmp.auction_method_id         as 'auction_method_id', " +
                "       tmp.auction_method_name       as 'auction_method_name', " +
                "       tmp.status_ending             as 'asset_management_status_ending', " +
                "       tmp.status_payment            as 'payment_status', " +
                "       tmp.winner_id                 as 'winner_id', " +
                "       tmp.auction_status_deposit    as 'auction_status_deposit', " +
                "       tmp.auction_status_refund     as 'auction_status_refund', " +
                "       tmp.auction_status_refuse_win as 'auction_status_refuse_win', " +
                "       tmp.auction_status_joined     as 'auction_status_joined', " +
                "       tmp.signature                 as 'signature', " +
                "       tmp.status_retract_id         as 'status_retract_id', " +
                "       tmp.status_retract            as 'status_retract', " +
                "       tmp.regulation_auction_status as 'regulation_auction_status', " +
                "       tmp.file_path_deposit         as 'file_path_deposit', " +
                "       tmp.winner_nth                as 'winner_nth', " +
                "       tmp.status_ending_all         as 'asset_management_status_ending_all', " +
                "       tmp.is_cancel_playing         as 'is_cancel_playing' ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        if (searchDto.getSortField() != null) {
            sb.append(" order by ");
            if (searchDto.getSortField().equalsIgnoreCase("historyType")) {
                sb.append(" tmp.type ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" order by tmp.auction_date desc ");
        }
        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        Map<Long, PropertySaleHistoryDto> propertySaleHistoryDtoMap = new LinkedHashMap<>();

        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            PropertySaleHistoryDto dto = new PropertySaleHistoryDto();

            Long auctionId = ValueUtil.getLongByObject(obj[0]);
            Date auctionDate = ValueUtil.getDateByObject(obj[1]);
            Long auctionAccountId = ValueUtil.getLongByObject(obj[2]);
            boolean type = ValueUtil.getBooleanByObject(obj[3]);
            Long assetId = ValueUtil.getLongByObject(obj[4]);
            String assetName = ValueUtil.getStringByObject(obj[5]);
            Integer auctionStatus = ValueUtil.getIntegerByObject(obj[6]);
            Integer assetStatus = ValueUtil.getIntegerByObject(obj[7]);
            Long regulationId = ValueUtil.getLongByObject(obj[8]);
            String regulationCode = ValueUtil.getStringByObject(obj[9]);
            Integer regulationNumberOfRounds = ValueUtil.getIntegerByObject(obj[10]);
            Date regulationStartTime = ValueUtil.getDateByObject(obj[11]);
            Date regulationEndTime = ValueUtil.getDateByObject(obj[12]);
            Integer timePerRound = ValueUtil.getIntegerByObject(obj[13]);
            Long regulationFileId = ValueUtil.getLongByObject(obj[14]);
            String filePath = ValueUtil.getStringByObject(obj[15]);
            Long auctionFormalityId = ValueUtil.getLongByObject(obj[16]);
            String auctionFormalityName = ValueUtil.getStringByObject(obj[17]);
            Long auctionMethodId = ValueUtil.getLongByObject(obj[18]);
            String auctionMethodName = ValueUtil.getStringByObject(obj[19]);
            boolean assetManagementStatusEnding = ValueUtil.getBooleanByObject(obj[20]);
            Integer paymentStatus = ValueUtil.getIntegerByObject(obj[21]);
            Long winnerId = ValueUtil.getLongByObject(obj[22]);
            Integer statusDeposit = ValueUtil.getIntegerByObject(obj[23]);
            Integer statusRefund = ValueUtil.getIntegerByObject(obj[24]);
            boolean statusRefuseWin = ValueUtil.getBooleanByObject(obj[25]);
            boolean statusJoined = ValueUtil.getBooleanByObject(obj[26]);
            Integer signature = ValueUtil.getIntegerByObject(obj[27]);
            Long bidId = ValueUtil.getLongByObject(obj[28]);
            Boolean statusRetract;
            if (bidId == null) {
                statusRetract = false;
            } else if (bidId != null) {
                statusRetract = true;
            } else {
                statusRetract = null;
            }
            boolean statusRetractCopy = ValueUtil.getBooleanByObject(obj[29]);
            Integer regulationAuctionStatus = ValueUtil.getIntegerByObject(obj[30]);
            String filePathDeposit = ValueUtil.getStringByObject(obj[31]);
            Integer winnerNth = ValueUtil.getIntegerByObject(obj[32]);
            boolean assetManagementStatusEndingAll = ValueUtil.getBooleanByObject(obj[33]);
            boolean cancelAssetPlaying = ValueUtil.getBooleanByObject(obj[34]);

            dto.setAuctionId(auctionId);
            dto.setAuctionDate(auctionDate);
            dto.setAuctionAccountId(auctionAccountId);
            dto.setType(type);
            dto.setAssetId(assetId);
            dto.setAssetName(assetName);
            dto.setAuctionStatus(auctionStatus);
            dto.setAssetStatus(assetStatus);
            dto.setRegulationNumberOfRounds(regulationNumberOfRounds);
            dto.setRegulationEndTime(regulationEndTime);
            dto.setTimePerRound(timePerRound);
            dto.setRegulationFileId(regulationFileId);
            dto.setFilePath(filePath);
            dto.setAuctionFormalityId(auctionFormalityId);
            dto.setAuctionFormalityName(auctionFormalityName);
            dto.setAuctionMethodId(auctionMethodId);
            dto.setAuctionMethodName(auctionMethodName);
            dto.setAssetManagementStatusEnding(assetManagementStatusEnding);
            dto.setPaymentStatus(paymentStatus);
            dto.setWinnerId(winnerId);
            dto.setStatusDeposit(statusDeposit);
            dto.setStatusRefund(statusRefund);
            dto.setStatusRefuseWin(statusRefuseWin);
            dto.setStatusJoined(statusJoined);
            dto.setAssetManagementStatusEndingAll(assetManagementStatusEndingAll);

            List<RegulationDto> regulationDtoList = new ArrayList<>();
            if (regulationId != null) {
                dto.setRegulationId(regulationId);
                dto.setRegulationCode(regulationCode);
                if (!dto.isType()) {
                    regulationDtoList = regulationRepository.getRegulationDtoListByAuctionReqId(dto.getAuctionId());
                } else {
                    RegulationDto regulationDto = new RegulationDto(
                            regulationId,
                            regulationCode,
                            regulationStartTime,
                            type,
                            auctionFormalityId,
                            auctionFormalityName,
                            auctionMethodId,
                            auctionMethodName,
                            assetId,
                            statusRefund,
                            auctionStatus,
                            statusDeposit,
                            statusRefuseWin,
                            statusJoined,
                            assetManagementStatusEnding,
                            signature,
                            statusRetract,
                            regulationAuctionStatus,
                            assetStatus,
                            auctionId,
                            winnerId,
                            filePathDeposit,
                            winnerNth,
                            cancelAssetPlaying
                    );
                    regulationDtoList.add(regulationDto);
                }
            }

            if (!propertySaleHistoryDtoMap.containsKey(dto.getAuctionId())) {
                dto.setRegulationDtoList(new ArrayList<>());
                dto.setRegulationDtoList(regulationDtoList);
                propertySaleHistoryDtoMap.put(dto.getAuctionId(), dto);
            }
        }
        return new ArrayList<>(propertySaleHistoryDtoMap.values());
    }

    @Override
    public BigInteger countSearch(PropertySaleHistorySearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("select distinct count(tmp.auction_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return (BigInteger) query.getSingleResult();
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, PropertySaleHistorySearchDto searchDto) {
        StringBuilder queryAuctionReq = new StringBuilder();
        queryAuctionReq.append("select ar.auction_req_id         as 'auction_id', " +
                "             ar.create_date            as 'auction_date', " +
                "             ar.account_id             as 'auction_account_id', " +
                "             assr.asset_id             as 'asset_id', " +
                "             ar.asset_name             as 'asset_name', " +
                "             ar.status                 as 'auction_status', " +
                "             assr.status               as 'asset_status', " +
                "             0                         as 'type', " +
                "             rar.regulation_id         as 'regulation_id', " +
                "             rar.code                  as 'regulation_name', " +
                "             rar.number_of_rounds      as 'regulation_round', " +
                "             rar.start_time            as 'regulation_start_time', " +
                "             rar.end_time              as 'regulation_end_time', " +
                "             rar.time_per_round        as 'time_per_round', " +
                "             rfar.regulation_file_id   as 'regulation_file_id', " +
                "             rfar.file_path            as 'file_path', " +
                "             afar.auction_formality_id as 'auction_formality_id', " +
                "             afar.name                 as 'auction_formality_name', " +
                "             amar.auction_method_id    as 'auction_method_id', " +
                "             amar.name                 as 'auction_method_name', " +
                "             0                         as 'status_ending', " +
                "             0                         as 'status_ending_all', " +
                "             -1                        as 'status_payment', " +
                "             0                         as 'winner_id', " +
                "             -1                        as 'auction_status_deposit', " +
                "             -1                        as 'auction_status_refund', " +
                "             -1                        as 'auction_status_refuse_win', " +
                "             -1                        as 'auction_status_joined', " +
                "             -1                        as 'signature', " +
                "             -1                        as 'status_retract_id', " +
                "             -1                        as 'status_retract', " +
                "             rar.auction_status        as 'regulation_auction_status', " +
                "             null                      as 'file_path_deposit', " +
                "             null                      as 'winner_nth', " +
                "             0                         as 'is_cancel_playing' " +
                "      from auction_req ar " +
                "               left join regulation rar on ar.auction_req_id = rar.auction_req_id " +
                "               left join asset assr on rar.regulation_id = assr.regulation_id " +
                "               left join regulation_file rfar on rfar.regulation_id = rar.regulation_id and rfar.type = 1 " +
                "               left join auction_formality afar on rar.auction_formality_id = afar.auction_formality_id " +
                "               left join auction_method amar on rar.auction_method_id = amar.auction_method_id " +
                "      where ar.account_id = :accountId " +
                "      group by ar.auction_req_id ");

        StringBuilder queryAuctionRegister = new StringBuilder();
        queryAuctionRegister.append("select a.auction_register_id    as 'auction_id', " +
                "             a.create_date            as 'auction_date', " +
                "             a.account_id             as 'auction_account_id', " +
                "             assa.asset_id            as 'asset_id', " +
                "             assa.name                as 'asset_name', " +
                "             a.status                 as 'auction_status', " +
                "             assa.asset_status        as 'asset_status', " +
                "             1                        as 'type', " +
                "             ra.regulation_id         as 'regulation_id', " +
                "             ra.code                  as 'regulation_name', " +
                "             ra.number_of_rounds      as 'regulation_round', " +
                "             ra.start_time            as 'regulation_start_time', " +
                "             ra.end_time              as 'regulation_end_time', " +
                "             ra.time_per_round        as 'time_per_round', " +
                "             rfa.regulation_file_id   as 'regulation_file_id', " +
                "             rfa.file_path            as 'file_path', " +
                "             afa.auction_formality_id as 'auction_formality_id', " +
                "             afa.name                 as 'auction_formality_name', " +
                "             ama.auction_method_id    as 'auction_method_id', " +
                "             ama.name                 as 'auction_method_name', " +
                "             amta.ending              as 'status_ending', " +
                "             amall.ending             as 'status_ending_all', " +
                "             p.status                 as 'status_payment', " +
                "             amta.auction_register_id as 'winner_id', " +
                "             a.status_deposit         as 'auction_status_deposit', " +
                "             a.status_refund          as 'auction_status_refund', " +
                "             a.status_refuse_win      as 'auction_status_refuse_win', " +
                "             a.status_joined          as 'auction_status_joined', " +
                "             rru.status               as 'signature', " +
                "             b.bid_id                 as 'status_retract_id', " +
                "             b.status_retract         as 'status_retract', " +
                "             ra.auction_status        as 'regulation_auction_status', " +
                "             a.file_path              as 'file_path_deposit', " +
                "             b.winner_nth             as 'winner_nth', " +
                "             assa.is_cancel_playing   as 'is_cancel_playing' " +
                "      from auction_register a " +
                "               inner join regulation ra on a.regulation_id = ra.regulation_id " +
                "               inner join (select ass.asset_id, " +
                "                                  ass.name          as 'name', " +
                "                                  ass.regulation_id as 'regulation_id', " +
                "                                  ass.status        as 'asset_status', " +
                "                                  ass.is_cancel_playing as 'is_cancel_playing' " +
                "                           from asset ass, " +
                "                                regulation r " +
                "                           where ass.regulation_id = r.regulation_id) as assa on a.asset_id = assa.asset_id " +
                "               inner join regulation_file rfa on rfa.regulation_id = ra.regulation_id and rfa.type = 1 " +
                "               inner join auction_formality afa on ra.auction_formality_id = afa.auction_formality_id " +
                "               left join asset_management amta on a.auction_register_id = amta.auction_register_id and amta.ending = :goodEnding " +
                "               left join asset_management amall on a.asset_id = amall.asset_id " +
                "               left join auction_method ama on ra.auction_method_id = ama.auction_method_id " +
                "               left join payment p on a.auction_register_id = p.auction_register_id " +
                "               left join regulation_report_file rrf on ra.regulation_id = rrf.regulation_id " +
                "               left join regulation_report_user rru on rrf.regulation_report_file_id = rru.regulation_report_file_id " +
                "               left join (select * from bid b where b.status_retract = :statusRetract) b " +
                "                         on b.asset_id = a.asset_id and a.auction_register_id = b.auction_register_id " +
                "      where a.account_id = :accountId " +
                "        and (assa.asset_status = :statusEnded or assa.asset_status = :statusNotSuccess or assa.asset_status = :statusCanceled) " +
                "      group by assa.asset_id ");

        if (searchDto.getCheckStatusSearch() == 0) {
            sb.append("from (").append(queryAuctionReq).append(" union all ").append(queryAuctionRegister).append(") as tmp ");
        } else if (searchDto.getCheckStatusSearch() == 1) {
            sb.append("from (").append(queryAuctionReq).append(") as tmp ");
        } else {
            sb.append("from (").append(queryAuctionRegister).append(") as tmp ");
        }
        sb.append(" where 1 = 1 ");
        if (StringUtils.isNotBlank(searchDto.getAssetName())) {
            sb.append(" and tmp.asset_name like :assetName ");
        }
        if (StringUtils.isNotBlank(searchDto.getRegulationCode())) {
            sb.append(" and tmp.regulation_name like :regulationName ");
        }

        if (searchDto.getFromDate() != null) {
            sb.append(" and :fromDate <= tmp.regulation_start_time ");
        }
        if (searchDto.getToDate() != null) {
            sb.append(" and tmp.regulation_start_time <= :toDate ");
        }
        if (searchDto.getFromDate() != null && searchDto.getToDate() != null) {
            sb.append(" and :fromDate <= tmp.regulation_start_time and tmp.regulation_start_time <= :toDate ");
        }

        String conditionAppend = " and tmp.type = 1 ";
        if (searchDto.getActiveStatus() != null) {
            switch (searchDto.getActiveStatus()) {
                case -1:
                    sb.append(" ");
                    break;
                case Constant.HIS_REFUSE_TO_PARTICIPATE_IN_AUCTION:
                    sb.append(" and tmp.auction_status = :auctionRegisterStatusRejectedJoin ").append(conditionAppend);
                    break;
                case Constant.HIS_INVALID_REGISTRATION:
                    sb.append(" and tmp.auction_status = :auctionRegisterStatusRejected ").append(conditionAppend);
                    break;
                case Constant.HIS_JOIN_THE_AUCTION:
                    sb.append(" and tmp.auction_status_joined = :auctionRegisterStatusJoinedJoin ").append(conditionAppend);
                    break;
                case Constant.HIS_DO_NOT_PARTICIPATE_IN_THE_AUCTION:
                    sb.append(" and tmp.auction_status_joined = :auctionRegisterStatusJoinedNotJoin ").append(conditionAppend);
                    break;
                case Constant.HIS_DISQUALIFIED_BY_THE_PROFESSORS:
                    sb.append(" and tmp.auction_status_deposit = :auctionRegisterStatusDepositRefuse ").append(conditionAppend);
                    break;
                case Constant.HIS_REFUSING_TO_WIN_THE_AUCTION:
                    sb.append(" and tmp.auction_status_refuse_win = :auctionRegisterStatusRefuseWinRefused ").append(conditionAppend);
                    break;
                case Constant.HIS_WINNING_THE_AUCTION:
                    sb.append(" and tmp.status_ending = :assetManagementEndingGood and tmp.auction_id = tmp.winner_id and tmp.asset_status = :winnerAssetStatus ").append(conditionAppend);
                    break;
                case Constant.HIS_WITHDRAW_THE_PRICE_PAID:
                    sb.append(" and tmp.status_retract = :statusRetract ").append(conditionAppend);
                    break;
            }
        }

        if (searchDto.isStatusWin()) {
            sb.append(" and tmp.status_ending = :statusWin and tmp.auction_id = tmp.winner_id and tmp.asset_status = :winnerAssetStatus ").append(conditionAppend);
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, PropertySaleHistorySearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("accountId", searchDto.getAccountId());

        if (searchDto.getCheckStatusSearch() == 0 || searchDto.getCheckStatusSearch() == 2) {
            query.setParameter("goodEnding", DbConstant.ASSET_MANAGEMENT_ENDING_GOOD);
            query.setParameter("statusEnded", DbConstant.ASSET_STATUS_ENDED);
            query.setParameter("statusNotSuccess", DbConstant.ASSET_STATUS_NOT_SUCCESS);
            query.setParameter("statusCanceled", DbConstant.ASSET_STATUS_CANCELED);
            query.setParameter("statusRetract", DbConstant.BID_STATUS_RETRACT_TRUE);
        }

        if (StringUtils.isNotBlank(searchDto.getAssetName())) {
            query.setParameter("assetName", "%" + searchDto.getAssetName().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getRegulationCode())) {
            query.setParameter("regulationName", "%" + searchDto.getRegulationCode().trim() + "%");
        }
        if (searchDto.getFromDate() != null) {
            query.setParameter("fromDate", searchDto.getFromDate());
        }
        if (searchDto.getToDate() != null) {
            query.setParameter("toDate", searchDto.getToDate());
        }

        if (searchDto.getActiveStatus() != null) {
            switch (searchDto.getActiveStatus()) {
                case Constant.HIS_REFUSE_TO_PARTICIPATE_IN_AUCTION:
                    query.setParameter("auctionRegisterStatusRejectedJoin", DbConstant.AUCTION_REGISTER_STATUS_REJECTED_JOIN);
                    break;
                case Constant.HIS_INVALID_REGISTRATION:
                    query.setParameter("auctionRegisterStatusRejected", DbConstant.AUCTION_REGISTER_STATUS_REJECTED);
                    break;
                case Constant.HIS_JOIN_THE_AUCTION:
                    query.setParameter("auctionRegisterStatusJoinedJoin", DbConstant.AUCTION_REGISTER_STATUS_JOINED_JOIN);
                    break;
                case Constant.HIS_DO_NOT_PARTICIPATE_IN_THE_AUCTION:
                    query.setParameter("auctionRegisterStatusJoinedNotJoin", DbConstant.AUCTION_REGISTER_STATUS_JOINED_NOT_JOIN);
                    break;
                case Constant.HIS_DISQUALIFIED_BY_THE_PROFESSORS:
                    query.setParameter("auctionRegisterStatusDepositRefuse", DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_REFUSE);
                    break;
                case Constant.HIS_REFUSING_TO_WIN_THE_AUCTION:
                    query.setParameter("auctionRegisterStatusRefuseWinRefused", DbConstant.AUCTION_REGISTER_STATUS_REFUSE_WIN_REFUSED);
                    break;
                case Constant.HIS_WINNING_THE_AUCTION:
                    query.setParameter("assetManagementEndingGood", DbConstant.ASSET_MANAGEMENT_ENDING_GOOD);
                    query.setParameter("winnerAssetStatus", DbConstant.ASSET_STATUS_ENDED);
                    break;
                case Constant.HIS_WITHDRAW_THE_PRICE_PAID:
                    query.setParameter("statusRetract", DbConstant.BID_STATUS_RETRACT_TRUE);
                    break;
            }
        }

        if (searchDto.isStatusWin()) {
            query.setParameter("statusWin", DbConstant.ASSET_MANAGEMENT_ENDING_GOOD);
            query.setParameter("winnerAssetStatus", DbConstant.ASSET_STATUS_ENDED);
        }

        return query;
    }
}
