package vn.compedia.website.auction.repository.impl;

import org.apache.commons.lang3.StringUtils;
import vn.compedia.website.auction.dto.user.AccountDto;
import vn.compedia.website.auction.dto.user.AccountSearchDto;
import vn.compedia.website.auction.repository.AccountDetailRepositoryCustom;
import vn.compedia.website.auction.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class AccountDetailRepositoryImpl implements AccountDetailRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AccountDto> search(AccountSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "tmp.*, "
                + "r.code ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" ORDER BY tmp.create_date DESC ");

        Query query = createQueryObjForSearch(sb, searchDto);
        if (searchDto.getPageSize() > 0) {
            query.setFirstResult(searchDto.getPageIndex());
            query.setMaxResults(searchDto.getPageSize());
        } else {
            query.setFirstResult(0);
            query.setMaxResults(Integer.MAX_VALUE);
        }

        List<AccountDto> accountDtoList = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            AccountDto dto = new AccountDto();
            dto.setDate(ValueUtil.getDateByObject(obj[0]));
            dto.setAccountId(ValueUtil.getLongByObject(obj[1]));
            dto.setRegulationId(ValueUtil.getLongByObject(obj[2]));
            dto.setAction(ValueUtil.getLongByObject(obj[3]));
            dto.setAssetName(ValueUtil.getStringByObject(obj[4]));
            dto.setAuctionMethodName(ValueUtil.getStringByObject(obj[5]));
            dto.setAuctionFormalityName(ValueUtil.getStringByObject(obj[6]));
            dto.setFilePath(ValueUtil.getStringByObject(obj[7]));
            dto.setName(ValueUtil.getStringByObject(obj[8]));
            accountDtoList.add(dto);
        }
        return accountDtoList;
    }
    @Override

    public BigInteger countSearch(AccountSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(tmp.account_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        Query query = createQueryObjForSearch(sb, searchDto);
        return (BigInteger) query.getSingleResult();
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, AccountSearchDto searchDto) {
        sb.append("FROM  (" +
                "SELECT reg.create_date, reg.account_id, reg.regulation_id as regulation_id, '1' AS action, a.name AS asset_name, af.name AS name_auction_formality, am.name AS name_auction_method, rf.file_path AS file " +
                "FROM auction_register reg, asset a,regulation r, auction_formality af , auction_method am, regulation_file rf WHERE reg.regulation_id = r.regulation_id " +
                "AND a.asset_id = reg.asset_id AND r.auction_formality_id = af.auction_formality_id AND r.auction_method_id = am.auction_method_id AND r.regulation_id = rf.regulation_id AND rf.type = 1 " +
                " UNION ALL " +
                "SELECT req.create_date, req.account_id, r.regulation_id as regulation_id, '2' AS action, req.asset_name AS asset_name, af.name AS name_auction_formality, am.name AS name_auction_method, rf.file_path AS file " +
                "FROM auction_req req, regulation r, auction_formality af , auction_method am, regulation_file rf WHERE r.auction_req_id = req.auction_req_id " +
                "AND r.auction_formality_id = af.auction_formality_id AND r.auction_method_id = am.auction_method_id AND r.regulation_id = rf.regulation_id AND rf.type = 1 " +
                ") tmp " +
                "LEFT JOIN regulation r on tmp.regulation_id = r.regulation_id " +
                "WHERE tmp.account_id = :accountId");

        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND (r.code LIKE :keyword ");
            sb.append(" OR tmp.asset_name LIKE :keyword) ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, AccountSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("accountId", searchDto.getAccountId());
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword().trim() + "%");
        }
        return query;
    }

}
