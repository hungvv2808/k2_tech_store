package vn.zeus.website.store.repository;

import vn.zeus.website.store.dto.user.AccountDto;
import vn.zeus.website.store.dto.user.AccountSearchDto;
import vn.zeus.website.store.model.Account;

import java.math.BigInteger;
import java.util.List;

public interface AccountRepositoryCustom {
    BigInteger checkExistEmail(String email);
    BigInteger checkExistPhone(String phone);
    List<AccountDto> search(AccountSearchDto searchDto);
    BigInteger countSearch(AccountSearchDto searchDto);
    List<Account> findAccountByRoleIdAndAccountStatus(Integer roleId, Integer accountStatus);
}
