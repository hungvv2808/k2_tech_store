package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.auction.TypeAssetSearchDto;
import vn.compedia.website.auction.model.TypeAsset;
import vn.compedia.website.auction.repository.TypeAssetRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TypeAssetRepositoryImpl implements TypeAssetRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<TypeAsset> search(TypeAssetSearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "n.type_asset_id,"
                + " n.code, "
                + " n.name,"
                + " n.status,"
                + "n.create_date,"
                + " n.update_date ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY n.type_asset_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("code")) {
                sb.append(" n.code collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("name")) {
                sb.append(" n.name collate utf8_vietnamese_ci ");
            }else if (searchDto.getSortField().equals("status")) {
                sb.append(" n.status ");
            } else if (searchDto.getSortField().equals("updateDate")) {
                sb.append(" n.update_date ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY n.type_asset_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<TypeAsset> typeAssetList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            TypeAsset dto = new TypeAsset();
            dto.setTypeAssetId(ValueUtil.getLongByObject(obj[0]));
            dto.setCode(ValueUtil.getStringByObject(obj[1]));
            dto.setName(ValueUtil.getStringByObject(obj[2]));
            dto.setStatus(ValueUtil.getBooleanByObject(obj[3]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[4]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[5]));

            typeAssetList.add(dto);
        }
        return typeAssetList;
    }

    @Override
    public Long countSearch(TypeAssetSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(n.type_asset_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());

    }
    private void appendQueryFromAndWhereForSearch(StringBuilder sb, TypeAssetSearchDto searchDto) {
        sb.append(" FROM type_asset n ");
        sb.append(" WHERE 1 = 1 ");

        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND (n.code LIKE :keyword ");
            sb.append(" OR n.name LIKE :keyword) ");
        }
    }
    private Query createQueryObjForSearch(StringBuilder sb,TypeAssetSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword().trim() + "%");
        }
        return query;
    }
}
