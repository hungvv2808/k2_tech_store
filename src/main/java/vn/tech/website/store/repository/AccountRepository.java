package vn.tech.website.store.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.tech.website.store.dto.user.AccountDto;
import vn.tech.website.store.model.Account;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Long>, AccountRepositoryCustom {
//
//    Account findByIdCardNumber(String IdcardNumber);
//
//    Account findByRelativeIdCardNumber(String relativeIdCardNumber);

    Account findByEmail(String email);

//    Account findByEmailAndRoleIdNot(String email, Integer roleId);
//
//    Account findByEmailAndRoleId(String email, Integer roleId);

    Account findAccountByUserNameAndRoleId(String username, Integer roleId);

//    Account findAccountByUsernameAndRoleIdNot(String username, Integer roleId);

    Account findByPhone(String phone);

//    Account findByOrgPhone(String orgPhone);

    Account findAccountByEmail(String email);

    Account findAccountByUserName(String username);

//    List<Account> findByProvinceIdOfIssue(Long id);

    Account findAccountByAccountId(Long accountId);

    List<Account> findAccountByRoleId(Integer roleId);


    Account findByAccountId(Long AccountId);

    List<Account> findAccountsByProvinceId(Long provinceId);

    List<Account> findAccountsByDistrictId(Long districtId);

    List<Account> findAccountsByCommuneId(Long communeId);

    @Query("select a from Account a where a.userName like :username and a.roleId <> :roleId")
    Account getAccountByUserNameAndRoleId(@Param("username")String username,@Param("roleId")Integer roleId);

    @Query("select a.provinceId from Account a ")
    List<Long> findProvinceId();

    //@Query("select a.provinceIdOfIssue from Account a ")
    //List<Long> findProvinceIdOfIssue();

    @Query("select a.roleId from Account a")
    List<Long> findRoleId();

//    @Query("select a.fullName from Account a where a.accountId =:storeeerId")
//    Account getFullName(@Param("storeeerId")Long storeeerId);


    @Query("select a from Account a where a.roleId <> :roleId ")
    List<Account> getAccountByAccountId(@Param("roleId") int roleId);

    @Query("select a from Account a where a.accountId = :accountId")
    Account getAccountByAccountId(@Param("accountId") Long id);

//    boolean existsAccountsByProvinceIdOrProvinceIdOfIssue(Long provinceId, Long provinceIdOfIssue);
}

