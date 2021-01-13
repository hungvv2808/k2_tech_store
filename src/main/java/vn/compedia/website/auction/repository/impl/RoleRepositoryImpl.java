package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import vn.compedia.website.auction.dto.base.RoleSearchDto;
import vn.compedia.website.auction.model.Role;
import vn.compedia.website.auction.repository.RoleRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RoleRepositoryImpl implements RoleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Role> search(RoleSearchDto searchDto) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "n.role_id,"
                + "n.code,"
                + "n.name,"
                + "n.status,"
                + "n.create_date,"
                + "n.update_date,"
                + "n.type,"
                + "n.can_modify, "
                + "n.description ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY n.role_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("code")) {
                sb.append(" n.code ");
            }
            if (searchDto.getSortField().equals("name")) {
                sb.append(" n.name collate utf8_vietnamese_ci ");
            }
            if (searchDto.getSortField().equals("status")) {
                sb.append(" n.status ");
            }
            if (searchDto.getSortField().equals("updateDate")) {
                sb.append(" n.update_date ");
            }
            if (searchDto.getSortField().equals("description")) {
                sb.append(" n.description collate utf8mb4_vietnamese_ci ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY n.role_id DESC ");
        }

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<Role> roleList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            Role dto = new Role();
            dto.setRoleId(ValueUtil.getIntegerByObject(obj[0]));
            dto.setCode(ValueUtil.getStringByObject(obj[1]));
            dto.setName(ValueUtil.getStringByObject(obj[2]));
            dto.setStatus(ValueUtil.getIntegerByObject(obj[3]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[4]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[5]));
            dto.setType(ValueUtil.getIntegerByObject(obj[6]));
            dto.setCanModify(ValueUtil.getBooleanByObject(obj[7]));
            dto.setDescription(ValueUtil.getStringByObject(obj[8]));

            roleList.add(dto);
        }
        return roleList;

    }

    @Override
    public Long countSearch(RoleSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(n.role_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return Long.valueOf(query.getSingleResult().toString());

    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, RoleSearchDto searchDto) {
        sb.append(" FROM role n ");
        sb.append(" WHERE 1=1 ");

        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND (n.code LIKE :keyword ");
            sb.append(" OR n.name LIKE :keyword) ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, RoleSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword().trim() + "%");
        }

        return query;
    }
}
