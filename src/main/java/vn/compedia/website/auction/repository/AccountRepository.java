package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.compedia.website.auction.dto.user.AccountDto;
import vn.compedia.website.auction.model.Account;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Long>, AccountRepositoryCustom {

    Account findByIdCardNumber(String IdcardNumber);

    Account findByRelativeIdCardNumber(String relativeIdCardNumber);

    Account findByEmail(String email);

    Account findByEmailAndRoleIdNot(String email, Integer roleId);

    Account findByEmailAndRoleId(String email, Integer roleId);

    Account findAccountByUsernameAndRoleId(String username, Integer roleId);

    Account findAccountByUsernameAndRoleIdNot(String username, Integer roleId);

    Account findByPhone(String phone);

    Account findByOrgPhone(String orgPhone);

    Account findAccountByEmail(String email);

    Account findAccountByUsername(String username);

    List<Account> findByProvinceIdOfIssue(Long id);

    Account findAccountByAccountId(Long accountId);

    List<Account> findAccountByRoleId(Integer roleId);

    List<Account> findAccountByRoleIdInOrderByFullName(List<Integer> roleIds);

    Account findByAccountId(Long AccountId);

    List<Account> findAccountsByProvinceId(Long provinceId);

    List<Account> findAccountsByDistrictId(Long districtId);

    List<Account> findAccountsByCommuneId(Long communeId);

    @Query("select a from Account a where a.username like :username and a.roleId <> :roleId")
    Account getAccountByUsernameAndRoleId(@Param("username")String username,@Param("roleId")Integer roleId);

    @Query("select a.provinceId from Account a ")
    List<Long> findProvinceId();

    @Query("select a.provinceIdOfIssue from Account a ")
    List<Long> findProvinceIdOfIssue();

    @Query("select a.roleId from Account a")
    List<Long> findRoleId();

    @Query("select a.fullName from Account a where a.accountId =:auctioneerId")
    Account getFullName(@Param("auctioneerId")Long auctioneerId);


    @Query("select a from Account a where a.roleId <> :roleId ")
    List<Account> getAccountByAccountId(@Param("roleId") int roleId);

    @Query("select a from Account a where a.accountId = :accountId")
    Account getAccountByAccountId(@Param("accountId") Long id);

    @Query("select new vn.compedia.website.auction.dto.user.AccountDto(pr.name, dis.name, com.name) from Province pr, District dis, Commune com, Account ac " +
            "where ac.provinceId = pr.provinceId and ac.districtId = dis.districtId and ac.communeId = com.communeId and ac.accountId = :accountId")
    AccountDto getNameAddress(@Param("accountId") Long accountId);

    boolean existsAccountsByProvinceIdOrProvinceIdOfIssue(Long provinceId, Long provinceIdOfIssue);
}

