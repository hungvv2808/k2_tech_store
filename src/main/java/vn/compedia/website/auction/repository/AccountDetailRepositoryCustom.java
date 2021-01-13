package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.user.AccountDto;
import vn.compedia.website.auction.dto.user.AccountSearchDto;

import java.math.BigInteger;
import java.util.List;

public interface AccountDetailRepositoryCustom {
    List<AccountDto> search(AccountSearchDto searchDto);
    BigInteger countSearch(AccountSearchDto searchDto);
}
