package vn.compedia.website.auction.repository.impl;


import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.auction.AssetSearchDto;
import vn.compedia.website.auction.dto.report.ReportAssetDto;
import vn.compedia.website.auction.repository.ReportRepository;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReportRepositoryImpl implements ReportRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ReportAssetDto> search(AssetSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "a.asset_id,"
                + "a.regulation_id,"
                + "a.name as assetName, "
                + "a.description,"
                + "a.starting_price,"
                + "a.start_time,"
                + "r.number_of_rounds, "
                + "r.time_per_round,"
                + "a.price_step,"
                + "a.status,"
                + "a.serial_number,"
                + "amn.ending,"
                + "r.code, "
                + "a.create_date, "
                + "af.name as auctionFormalityName, "
                + "am.name as auctionMethodName, "
                + "ta.name as typeAssetName, "
                + "acc.full_name, "
                + "amn.money, "
                + "r.real_start_time, "
                + "af.auction_formality_id, "
                + "am.auction_method_id, "
                + "a.type_asset_id, "
                + "rf.file_path " );
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY a.asset_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("regulationCode")) {
                sb.append(" r.code ");
            } else if (searchDto.getSortField().equals("name")) {
                sb.append(" a.name ");
            } else if (searchDto.getSortField().equals("createDate")) {
                sb.append(" a.create_date ");
            } else if (searchDto.getSortField().equals("assetId")) {
                sb.append(" a.asset_id ");
            } else if (searchDto.getSortField().equals("auctionFormalityName")) {
                sb.append(" af.name ");
            } else if (searchDto.getSortField().equals("auctionMethodName")) {
                sb.append(" am.name ");
            } else if (searchDto.getSortField().equals("typeAssetName")) {
                sb.append(" ta.name ");
            } else if (searchDto.getSortField().equals("status")) {
                sb.append(" a.status ");
            } else if (searchDto.getSortField().equals("startingPrice")) {
                sb.append(" a.starting_price ");
            } else if (searchDto.getSortField().equals("auctioneerFullName")) {
                sb.append(" acc.full_name ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY a.asset_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<ReportAssetDto> reportAssetDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ReportAssetDto dto = new ReportAssetDto();
            dto.setAssetId(ValueUtil.getLongByObject(obj[0]));
            dto.setRegulationId(ValueUtil.getLongByObject(obj[1]));
            dto.setName(ValueUtil.getStringByObject(obj[2]));
            dto.setDescription(ValueUtil.getStringByObject(obj[3]));
            dto.setStartingPrice(ValueUtil.getLongByObject(obj[4]));
            dto.setStartTime(ValueUtil.getDateByObject(obj[5]));
            dto.setNumberOfRounds(ValueUtil.getIntegerByObject(obj[6]));
            dto.setTimePerRound(ValueUtil.getIntegerByObject(obj[7]));
            dto.setPriceStep(ValueUtil.getLongByObject(obj[8]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[9]));
            dto.setNumericalOrder(ValueUtil.getIntegerByObject(obj[10]));
            dto.setAssetManagementEnding(ValueUtil.getBooleanByObject(obj[11]));
            dto.setRegulationCode(ValueUtil.getStringByObject(obj[12]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[13]));
            dto.setAuctionFormalityName(ValueUtil.getStringByObject(obj[14]));
            dto.setAuctionMethodName(ValueUtil.getStringByObject(obj[15]));
            dto.setTypeAssetName(ValueUtil.getStringByObject(obj[16]));
            dto.setAuctioneerFullName(ValueUtil.getStringByObject(obj[17]));
            dto.setEndedPrice(ValueUtil.getLongByObject(obj[18]));
            dto.setRealStartTime(ValueUtil.getDateByObject(obj[19]));
            dto.setAuctionFormalityId(ValueUtil.getLongByObject(obj[20]));
            dto.setAuctionMethodId(ValueUtil.getLongByObject(obj[21]));
            dto.setTypeAssetId(ValueUtil.getLongByObject(obj[22]));
            dto.setRegulationFilePath(ValueUtil.getStringByObject(obj[23]));
            reportAssetDtoList.add(dto);
        }
        return reportAssetDtoList;
    }

    @Override
    public Long countSearch(AssetSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(T.asset_id) ");
        sb.append(" FROM (SELECT a.asset_id ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY a.asset_id) AS T ");
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, AssetSearchDto searchDto) {
        sb.append(" FROM asset a "
                + "INNER JOIN type_asset ta ON ta.type_asset_id = a.type_asset_id "
                + "INNER JOIN regulation r ON r.regulation_id = a.regulation_id "
                + "INNER JOIN regulation_file rf ON r.regulation_id = rf.regulation_id "
                + "INNER JOIN auction_formality af ON r.auction_formality_id = af.auction_formality_id "
                + "INNER JOIN auction_method am ON r.auction_method_id = am.auction_method_id "
                + "LEFT JOIN asset_management amn ON amn.asset_id = a.asset_id "
                + "LEFT JOIN asset_image ai ON a.asset_id = ai.asset_id "
                + "LEFT JOIN account acc ON acc.account_id = r.auctioneer_id ");
        sb.append(" WHERE 1 = 1 and rf.type = 1 ");
        if (searchDto.getFromDate() != null) {
            sb.append(" AND a.create_date >= :ngayTuNgay ");
        }
        if (searchDto.getToDate() != null) {
            sb.append(" AND a.create_date <= :ngayDenNgay ");
        }
        if (searchDto.getStartingPriceStart() != null) {
            sb.append(" AND a.starting_price >= :startingPriceStart ");
        }
        if (searchDto.getStartingPriceEnd() != null) {
            sb.append(" AND a.starting_price <= :startingPriceEnd ");
        }
        if (searchDto.getTypeAssetId() != null) {
            sb.append(" AND ta.type_asset_id = :typeAssetId ");
        }
        if (searchDto.getAuctioneerId() != null) {
            sb.append(" AND acc.account_id = :auctioneerId ");
        }
        if (searchDto.getAuctionFormalityId() != null) {
            sb.append(" AND af.auction_formality_id = :auctionFormalityId ");
        }
        if (searchDto.getAuctionMethodId() != null) {
            sb.append(" AND am.auction_method_id = :auctionMethodId ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, AssetSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (searchDto.getFromDate() != null) {
            query.setParameter("ngayTuNgay", DateUtil.formatFromDate(searchDto.getFromDate()));
        }
        if (searchDto.getToDate() != null) {
            query.setParameter("ngayDenNgay", DateUtil.formatToDate(searchDto.getToDate()));
        }
        if (searchDto.getStartingPriceStart() != null) {
            query.setParameter("startingPriceStart", searchDto.getStartingPriceStart());
        }
        if (searchDto.getStartingPriceEnd() != null) {
            query.setParameter("startingPriceEnd", searchDto.getStartingPriceEnd());
        }
        if (searchDto.getTypeAssetId() != null) {
            query.setParameter("typeAssetId", searchDto.getTypeAssetId());
        }
        if (searchDto.getAuctioneerId() != null) {
            query.setParameter("auctioneerId", searchDto.getAuctioneerId());
        }
        if (searchDto.getAuctionFormalityId() != null) {
            query.setParameter("auctionFormalityId", searchDto.getAuctionFormalityId());
            sb.append(" AND af.auction_formality_id = :auctionFormalityId ");
        }
        if (searchDto.getAuctionMethodId() != null) {
            query.setParameter("auctionMethodId", searchDto.getAuctionMethodId());
        }
        return query;
    }

    public List<ReportAssetDto> searchForExport(AssetSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "a.asset_id,"
                + "a.regulation_id,"
                + "a.name as assetName, "
                + "a.description,"
                + "a.starting_price,"
                + "a.start_time,"
                + "r.number_of_rounds, "
                + "r.time_per_round,"
                + "a.price_step,"
                + "a.status,"
                + "a.serial_number,"
                + "amn.ending,"
                + "r.code, "
                + "a.create_date, "
                + "af.name as auctionFormalityName, "
                + "am.name as auctionMethodName, "
                + "ta.name as typeAssetName, "
                + "acc.full_name, "
                + "amn.money, "
                + "r.real_start_time, "
                + "af.auction_formality_id, "
                + "am.auction_method_id, "
                + "a.type_asset_id, "
                + "rf.file_path " );
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY a.asset_id ");
        sb.append(" ORDER BY a.type_asset_id ASC ");
        Query query = entityManager.createNativeQuery(sb.toString());
        if (searchDto.getFromDate() != null) {
            query.setParameter("ngayTuNgay", DateUtil.formatFromDate(searchDto.getFromDate()));
        }
        if (searchDto.getToDate() != null) {
            query.setParameter("ngayDenNgay", DateUtil.formatToDate(searchDto.getToDate()));
        }
        if (searchDto.getStartingPriceStart() != null) {
            query.setParameter("startingPriceStart", searchDto.getStartingPriceStart());
        }
        if (searchDto.getStartingPriceEnd() != null) {
            query.setParameter("startingPriceEnd", searchDto.getStartingPriceEnd());
        }
        if (searchDto.getTypeAssetId() != null) {
            query.setParameter("typeAssetId", searchDto.getTypeAssetId());
        }
        if (searchDto.getAuctioneerId() != null) {
            query.setParameter("auctioneerId", searchDto.getAuctioneerId());
        }
        if (searchDto.getAuctionFormalityId() != null) {
            query.setParameter("auctionFormalityId", searchDto.getAuctionFormalityId());
            sb.append(" AND af.auction_formality_id = :auctionFormalityId ");
        }
        if (searchDto.getAuctionMethodId() != null) {
            query.setParameter("auctionMethodId", searchDto.getAuctionMethodId());
        }
        List<ReportAssetDto> reportAssetDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            ReportAssetDto dto = new ReportAssetDto();
            dto.setAssetId(ValueUtil.getLongByObject(obj[0]));
            dto.setRegulationId(ValueUtil.getLongByObject(obj[1]));
            dto.setName(ValueUtil.getStringByObject(obj[2]));
            dto.setDescription(ValueUtil.getStringByObject(obj[3]));
            dto.setStartingPrice(ValueUtil.getLongByObject(obj[4]));
            dto.setStartTime(ValueUtil.getDateByObject(obj[5]));
            dto.setNumberOfRounds(ValueUtil.getIntegerByObject(obj[6]));
            dto.setTimePerRound(ValueUtil.getIntegerByObject(obj[7]));
            dto.setPriceStep(ValueUtil.getLongByObject(obj[8]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[9]));
            dto.setNumericalOrder(ValueUtil.getIntegerByObject(obj[10]));
            dto.setAssetManagementEnding(ValueUtil.getBooleanByObject(obj[11]));
            dto.setRegulationCode(ValueUtil.getStringByObject(obj[12]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[13]));
            dto.setAuctionFormalityName(ValueUtil.getStringByObject(obj[14]));
            dto.setAuctionMethodName(ValueUtil.getStringByObject(obj[15]));
            dto.setTypeAssetName(ValueUtil.getStringByObject(obj[16]));
            dto.setAuctioneerFullName(ValueUtil.getStringByObject(obj[17]));
            dto.setEndedPrice(ValueUtil.getLongByObject(obj[18]));
            dto.setRealStartTime(ValueUtil.getDateByObject(obj[19]));
            dto.setAuctionFormalityId(ValueUtil.getLongByObject(obj[20]));
            dto.setAuctionMethodId(ValueUtil.getLongByObject(obj[21]));
            dto.setTypeAssetId(ValueUtil.getLongByObject(obj[22]));
            dto.setRegulationFilePath(ValueUtil.getStringByObject(obj[23]));
            reportAssetDtoList.add(dto);
        }
        return reportAssetDtoList;
    }
}
