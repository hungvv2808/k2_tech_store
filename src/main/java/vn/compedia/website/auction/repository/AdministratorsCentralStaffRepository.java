package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.compedia.website.auction.model.Account;

public interface AdministratorsCentralStaffRepository extends CrudRepository<Account ,Long>,AdministratorsCentralStaffRepositoryCustom {
    @Query("select acc.fullName from Account acc where acc.accountId = :accountId")
    String getFullnameByAccountId(@Param("accountId") Long accountId);
}
