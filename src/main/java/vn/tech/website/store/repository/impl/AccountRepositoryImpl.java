package vn.tech.website.store.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import vn.tech.website.store.dto.user.AccountDto;
import vn.tech.website.store.dto.user.AccountSearchDto;
import vn.tech.website.store.model.Account;
import vn.tech.website.store.repository.AccountRepositoryCustom;
import vn.tech.website.store.util.DbConstant;
import vn.tech.website.store.util.ValueUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class AccountRepositoryImpl implements AccountRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AccountDto> search(AccountSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT "
                + "acc.account_id, "
                + "acc.full_name, "
                + "acc.sex, "
                + "acc.date_of_birth, "
                + "acc.address, "
                + "acc.phone, "
                + "acc.email, "
                + "acc.password, "
                + "acc.salt, "
                + "acc.id_card_number, "
                + "acc.province_id_of_issue, "
                + "acc.date_of_issue, "
                + "acc.relative_name, "
                + "acc.relative_id_card_number, "
                + "acc.permanent_residence, "
                + "acc.org_name, "
                + "acc.business_license, "
                + "acc.org_address, "
                + "acc.position, "
                + "acc.org_phone, "
                + "acc.status, "
                + "acc.is_org,"
                + "acc.role_id,"
                + "acc.username,"
                + "p.province_id, "
                + "p.name as 'province_name', "
                + "d.district_id, "
                + "d.name as 'district_name', "
                + "c.commune_id, "
                + "c.name as 'commune_name', "
                + "acc.create_by,"
                + "acc.create_date, "
                + "acc.update_date,"
                + "acc.first_time_login,"
                + "acc.finish_info_status,"
                + "acc.avatar_path, "
                + "acc.time_to_change_password, "
                + "cmnd.image_card_id_front, "
                + "cmnd.image_card_id_back, "
                + "GROUP_CONCAT(DISTINCT cmnd.accuracy_company_file_path), "
                + "GROUP_CONCAT(DISTINCT cmnd.accuracy_company_file_name) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" GROUP BY acc.account_id ");
        if (searchDto.getSortField() != null) {
            sb.append(" ORDER BY ");
            if (searchDto.getSortField().equals("fullName")) {
                sb.append(" acc.full_name collate utf8_vietnamese_ci ");
            } else if (searchDto.getSortField().equals("accountStatus")) {
                sb.append(" acc.status ");
            } else if (searchDto.getSortField().equals("org")) {
                sb.append(" acc.is_org ");
            } else if (searchDto.getSortField().equals("username")) {
                sb.append(" acc.username ");
            } else if (searchDto.getSortField().equals("idCardNumber")) {
                sb.append(" acc.id_card_number ");
            } else if (searchDto.getSortField().equals("phone")) {
                sb.append(" acc.phone ");
            } else if (searchDto.getSortField().equals("email")) {
                sb.append(" acc.email ");
            } else if (searchDto.getSortField().equals("updateDate")) {
                sb.append(" acc.update_date ");
            } else if (searchDto.getSortField().equals("createDate")) {
                sb.append(" acc.create_date ");
            }
            sb.append(searchDto.getSortOrder());
        } else {
            sb.append(" ORDER BY acc.status ASC, acc.create_date DESC ");
        }

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
            dto.setAccountId(ValueUtil.getLongByObject(obj[0]));
            dto.setFullName(ValueUtil.getStringByObject(obj[1]));
            dto.setSex(ValueUtil.getIntegerByObject(obj[2]));
            dto.setDateOfBirth(ValueUtil.getDateByObject(obj[3]));
            dto.setAddress(ValueUtil.getStringByObject(obj[4]));
            dto.setPhone(ValueUtil.getStringByObject(obj[5]));
            dto.setEmail(ValueUtil.getStringByObject(obj[6]));
            dto.setPassword(ValueUtil.getStringByObject(obj[7]));
            dto.setSalt(ValueUtil.getStringByObject(obj[8]));
            dto.setIdCardNumber(ValueUtil.getStringByObject(obj[9]));
            dto.setProvinceIdOfIssue(ValueUtil.getLongByObject(obj[10]));
            dto.setDateOfIssue(ValueUtil.getDateByObject(obj[11]));
            dto.setRelativeName(ValueUtil.getStringByObject(obj[12]));
            dto.setRelativeIdCardNumber(ValueUtil.getStringByObject(obj[13]));
            dto.setPermanentResidence(ValueUtil.getStringByObject(obj[14]));
            dto.setOrgName(ValueUtil.getStringByObject(obj[15]));
            dto.setBusinessLicense(ValueUtil.getStringByObject(obj[16]));
            dto.setOrgAddress(ValueUtil.getStringByObject(obj[17]));
            dto.setPosition(ValueUtil.getStringByObject(obj[18]));
            dto.setOrgPhone(ValueUtil.getStringByObject(obj[19]));
            dto.setAccountStatus(ValueUtil.getIntegerByObject(obj[20]));
            dto.setOrg(ValueUtil.getBooleanByObject(obj[21]));
            dto.setRoleId(ValueUtil.getIntegerByObject(obj[22]));
            dto.setUsername(ValueUtil.getStringByObject(obj[23]));
            dto.setProvinceId(ValueUtil.getLongByObject(obj[24]));
            dto.setNameProvice(ValueUtil.getStringByObject(obj[25]));
            dto.setDistrictId(ValueUtil.getLongByObject(obj[26]));
            dto.setNameDistrict(ValueUtil.getStringByObject(obj[27]));
            dto.setCommuneId(ValueUtil.getLongByObject(obj[28]));
            dto.setNameCommune(ValueUtil.getStringByObject(obj[29]));
            dto.setCreateBy(ValueUtil.getLongByObject(obj[30]));
            dto.setCreateDate(ValueUtil.getDateByObject(obj[31]));
            dto.setUpdateDate(ValueUtil.getDateByObject(obj[32]));
            dto.setFirstTimeLogin(ValueUtil.getBooleanByObject(obj[33]));
            dto.setFinishInfoStatus(ValueUtil.getBooleanByObject(obj[34]));
            dto.setAvatarPath(ValueUtil.getStringByObject(obj[35]));
            dto.setTimeToChangePassword(ValueUtil.getDateByObject(obj[36]));
            if (!dto.isOrg()) {
                dto.setImageCardIdFrontPath(ValueUtil.getStringByObject(obj[37]));
                dto.setImageCardIdBackPath(ValueUtil.getStringByObject(obj[38]));
            } else {
                String accuracyCompanyFilePath = ValueUtil.getStringByObject(obj[39]);
                String accuracyCompanyFileName = ValueUtil.getStringByObject(obj[40]);
                if (accuracyCompanyFilePath != null) {
                    String[] filePathList = accuracyCompanyFilePath.split(",");
                    String[] fileNameList = accuracyCompanyFileName.split(",");
                    int i = 0;
                    dto.setAccuracyCompanyFile(new HashMap<>());
                    for (String filePath : filePathList) {
                        dto.getAccuracyCompanyFile().put(filePath, fileNameList[i++]);
                    }
                }
            }
            accountDtoList.add(dto);
        }
        return accountDtoList;
    }

    @Override
    public BigInteger countSearch(AccountSearchDto searchDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(1) from ( SELECT COUNT(acc.account_id) ");
        appendQueryFromAndWhereForSearch(sb, searchDto);
        sb.append(" group by acc.account_id) as countAccount ");
        Query query = createQueryObjForSearch(sb, searchDto);
        return (BigInteger) query.getSingleResult();
    }

    @Override
    public List<Account> findAccountByRoleIdAndAccountStatus(Integer roleId, Integer accountStatus) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ac.account_id, ac.full_name from account ac where ac.role_id =:roleId and ac.status =:status and ac.full_name <> '' and ac.is_org =:notOrg union all " +
                "select ac1.account_id, ac1.org_name from account ac1 where ac1.role_id =:roleId and ac1.status =:status and ac1.is_org =:isOrg and ac1.org_name <> '' ");

        Query query = entityManager.createNativeQuery(sb.toString());

        query.setParameter("roleId", DbConstant.ROLE_ID_USER);
        query.setParameter("status", DbConstant.ACCOUNT_ACTIVE_STATUS);
        query.setParameter("notOrg",DbConstant.notOrg);
        query.setParameter("isOrg",DbConstant.isOrg);
        List<Account> accountDtoList1 = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        for (Object[] obj : result) {
            Account ac = new Account();
            ac.setAccountId(ValueUtil.getLongByObject(obj[0]));
            ac.setFullName(ValueUtil.getStringByObject(obj[1]));
            accountDtoList1.add(ac);
        }
        return accountDtoList1;
    }

    private void appendQueryFromAndWhereForSearch(StringBuilder sb, AccountSearchDto searchDto) {
        sb.append("FROM account acc " +
                "LEFT JOIN accuracy cmnd on cmnd.account_id = acc.account_id " +
                "left join province p on acc.province_id = p.province_id " +
                "left join district d on acc.district_id = d.district_id " +
                "left join commune c on acc.commune_id = c.commune_id " +
                " WHERE 1 = 1 ");
        if (searchDto.getRoleId() != null) {
            sb.append("AND acc.role_id = :roleId ");
        } else {
            sb.append("AND acc.role_id = 1 ");
        }
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            sb.append(" AND ( acc.org_name LIKE :keyword or acc.full_name LIKE :keyword ) ");
        }
        if (StringUtils.isNotBlank(searchDto.getIdCardNumber())) {
            sb.append(" AND acc.id_card_number LIKE :idCardNumber ");
        }
        if (searchDto.getOrg() != null ) {
            sb.append(" AND acc.is_org = :isOrg ");
        }
        if (StringUtils.isNotBlank(searchDto.getPhone())) {
            sb.append(" AND (acc.phone LIKE :phone OR acc.org_phone LIKE :phone )");
        }
        if (StringUtils.isNotBlank(searchDto.getUsername())) {
            sb.append(" AND acc.username LIKE :username ");
        }
        if (searchDto.getAccountStatus() != null) {
            sb.append(" AND acc.status = :status ");
        }
    }

    private Query createQueryObjForSearch(StringBuilder sb, AccountSearchDto searchDto) {
        Query query = entityManager.createNativeQuery(sb.toString());
        if (searchDto.getRoleId() != null) {
            query.setParameter("roleId", searchDto.getRoleId());
        }
        if (StringUtils.isNotBlank(searchDto.getKeyword())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getIdCardNumber())) {
            query.setParameter("idCardNumber", "%" + searchDto.getIdCardNumber().trim() + "%");
        }
        if (searchDto.getOrg() != null) {
            query.setParameter("isOrg", searchDto.getOrg());
        }
        if (StringUtils.isNotBlank(searchDto.getPhone())) {
            query.setParameter("phone", "%" + searchDto.getPhone().trim() + "%");
        }
        if (StringUtils.isNotBlank(searchDto.getEmail())) {
            query.setParameter("email", "%" + searchDto.getEmail().trim() + "%");
        }
        if (searchDto.getAccountStatus() != null) {
            query.setParameter("status", searchDto.getAccountStatus());
        }
        if (StringUtils.isNotBlank(searchDto.getUsername())) {
            query.setParameter("username", "%" + searchDto.getUsername().trim() + "%");
        }
        return query;
    }

    @Override
    public BigInteger checkExistEmail(String email) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT count(acc.account_id) ");
        sb.append("FROM account acc ");
        sb.append("WHERE acc.email LIKE :email");
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("email", "%" + email + "%");

        return (BigInteger) query.getSingleResult();
    }

    @Override
    public BigInteger checkExistPhone(String phone) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT count(acc.account_id) ");
        sb.append("FROM account acc ");
        sb.append("WHERE acc.phone LIKE :phone ");
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("phone", "%" + phone + "%");
        return (BigInteger) query.getSingleResult();
    }

}
