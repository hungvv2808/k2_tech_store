package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.user.AccountDto;
import vn.compedia.website.auction.dto.user.AccountSearchDto;
import vn.compedia.website.auction.model.Account;

import java.math.BigInteger;
import java.util.List;

public interface AccountRepositoryCustom {
    BigInteger checkExistEmail(String email);
    BigInteger checkExistPhone(String phone);
    List<AccountDto> search(AccountSearchDto searchDto);
    BigInteger countSearch(AccountSearchDto searchDto);
    List<Account> findAccountByRoleIdAndAccountStatus(Integer roleId, Integer accountStatus);
}
