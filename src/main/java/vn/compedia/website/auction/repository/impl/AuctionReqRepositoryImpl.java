package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.auction.AssetSearchDto;
import vn.compedia.website.auction.dto.request.AuctionReqDto;
import vn.compedia.website.auction.dto.request.AuctionReqSearchDto;
import vn.compedia.website.auction.repository.AuctionReqRepositoryCustom;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AuctionReqRepositoryImpl implements AuctionReqRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AuctionReqDto> search(AuctionReqSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM (SELECT "
                + " aur.create_date, "
                + " aur.update_date, "
                + " aur.create_by, "
                + " aur.update_by, "
                + " aur.auction_req_id, "
                + " aur.account_id, "
                + " aur.asset_name, "
                + " aur.asset_description, "
                + " aur.status, "
                + " ac.is_org, "
                + " ac.full_name, "
                + " ac.org_name, "
                + " ac.email, "
                + " ac.phone, "
                + "p.province_id, "
                + "p.name as 'province_name', "
                + "d.district_id, "
                + "d.name as 'district_name', "
                + "c.commune_id, "
                + "c.name as 'commune_name', "
                + "IF(ac.is_org = 1, ac.org_address, ac.address) as address");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY aur.auction_req_id) ar ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equalsIgnoreCase("assetName")) {
                sb.append(" ar.asset_name ");
            } else if (searchDto.getSortField().equalsIgnoreCase("fullNameNguoiDangKy")) {
                sb.append(" ar.full_name ");
            } else if (searchDto.getSortField().equalsIgnoreCase("email")) {
                sb.append(" ar.email ");
            } else if (searchDto.getSortField().equalsIgnoreCase("phone")) {
                sb.append(" ar.phone ");
            } else if (searchDto.getSortField().equalsIgnoreCase("address")) {
                sb.append(" ar.address ");
            } else if (searchDto.getSortField().equalsIgnoreCase("createDate")) {
                sb.append(" ar.createDate ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY ar.auction_req_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<AuctionReqDto> auctionReqDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            AuctionReqDto dto = new AuctionReqDto();
            dto.setCreateDate(ValueUtil.getDateByObject(obj[0]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[1]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[2]));
            dto.setUpdateBy(ValueUtil.getLongByObject(obj[3]));
            dto.setAuctionReqId(ValueUtil.getLongByObject(obj[4]));
            dto.setAccountId(ValueUtil.getLongByObject(obj[5]));
            dto.setAssetName(ValueUtil.getStringByObject(obj[6]));
            dto.setAssetDescription(ValueUtil.getStringByObject(obj[7]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[8]));
            dto.setOrg(ValueUtil.getBooleanByObject(obj[9]));

            if(!ValueUtil.getBooleanByObject(obj[9])){
                dto.setFullNameNguoiDangKy(ValueUtil.getStringByObject(obj[10]));
            } else {
                dto.setFullNameNguoiDangKy(ValueUtil.getStringByObject(obj[11]));
            }

            dto.setEmail(ValueUtil.getStringByObject(obj[12]));
            dto.setPhone(ValueUtil.getStringByObject(obj[13]));
            dto.setProviceId(ValueUtil.getLongByObject(obj[14]));
            dto.setNameProvice(ValueUtil.getStringByObject(obj[15]));
            dto.setDistrictId(ValueUtil.getLongByObject(obj[16]));
            dto.setNameDistrict(ValueUtil.getStringByObject(obj[17]));
            dto.setCommuneId(ValueUtil.getLongByObject(obj[18]));
            dto.setNameCommune(ValueUtil.getStringByObject(obj[19]));
            dto.setAddress(ValueUtil.getStringByObject(obj[20]));
            auctionReqDtoList.add(dto);
        }
        return auctionReqDtoList;
    }
    @Override
    public BigInteger countSearch(AuctionReqSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(aur.auction_req_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return (BigInteger) query.getSingleResult();
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, AuctionReqSearchDto searchDto) {
        sb.append(" FROM auction_req aur inner join account ac on aur.account_id = ac.account_id " +
                "left join province p on ac.province_id = p.province_id " +
                "left join district d on ac.district_id = d.district_id " +
                "left join commune c on ac.commune_id = c.commune_id " +
                "where 1 = 1 ");
        if (StringUtils.isNotBlank(searchDto.getFullNameNguoiDangKy())) {
            sb.append(" AND (ac.full_name LIKE :fullName OR ac.org_name LIKE :fullName) ");
        }
        if (StringUtils.isNotBlank(searchDto.getAssetName())) {
            sb.append(" and aur.asset_name like :assetName ");
        }
        if (StringUtils.isNotBlank(searchDto.getAddress())) {
            sb.append(" and (ac.address like :address ");
            sb.append(" or ac.org_address like :address) ");
        }
        if (StringUtils.isNotBlank(searchDto.getPhone())) {
            sb.append(" and ac.phone like :phone ");
        }
        if (searchDto.getProviceId() != null) {
            sb.append("and p.province_id = :proviceId ");
        }
        if (searchDto.getDistrictId() != null) {
            sb.append("and d.district_id = :districtId ");
        }
        if (searchDto.getCommuneId() != null) {
            sb.append("and c.commune_id = :communeId ");
        }
        if (searchDto.getStatus() != null) {
            sb.append(" and aur.status = :status");
        }
        if (searchDto.getDateFrom() != null) {
            sb.append(" and aur.create_date <= :dateFrom ");
        }
        if (searchDto.getDateTo() != null) {
            sb.append(" and aur.create_date >= :dateTo ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, AuctionReqSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(searchDto.getFullNameNguoiDangKy())) {
            query.setParameter("fullName", "%" + searchDto.getFullNameNguoiDangKy().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getAssetName())) {
            query.setParameter("assetName", "%" + searchDto.getAssetName().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getAddress())) {
            query.setParameter("address", "%" + searchDto.getAddress().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getPhone())) {
            query.setParameter("phone", "%" + searchDto.getPhone().trim() + "%");
        }
        if (searchDto.getProviceId() != null) {
            query.setParameter("proviceId", searchDto.getProviceId());
        }
        if (searchDto.getDistrictId() != null) {
            query.setParameter("districtId", searchDto.getDistrictId());
        }
        if (searchDto.getCommuneId() != null) {
            query.setParameter("communeId", searchDto.getCommuneId());
        }
        if (searchDto.getStatus() != null) {
            query.setParameter("status", searchDto.getStatus());
        }
        if (searchDto.getDateTo() != null) {
            query.setParameter("dateTo", DateUtil.formatDate(searchDto.getDateTo(), DateUtil.FROM_DATE_FORMAT));
        }
        if (searchDto.getDateFrom() != null) {
            query.setParameter("dateFrom", DateUtil.formatDate(searchDto.getDateFrom(), DateUtil.TO_DATE_FORMAT));
        }
        return query;
    }

    @Override
    public BigInteger getTotalAuctionReqContract(AssetSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(ar.auction_req_id) from auction_req ar where ar.status = :status ");
        if (searchDto.getFromDate() != null) {
            sb.append(" and ar.create_date > :fromDate ");
        }
        if (searchDto.getToDate() != null) {
            sb.append(" and ar.create_date < :toDate ");
        }
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("status", DbConstant.KY_HOP_DONG_ID);
        if (searchDto.getFromDate() != null) {
            query.setParameter("fromDate", searchDto.getFromDate(), TemporalType.DATE);
        }
        if (searchDto.getToDate() != null) {
            query.setParameter("toDate", searchDto.getToDate(), TemporalType.DATE);
        }
        return (BigInteger) query.getSingleResult();
    }
}

