package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.user.AccountDto;
import vn.compedia.website.auction.dto.user.AdministratorsCentralStaffSearchDto;

import java.math.BigInteger;
import java.util.List;

public interface AdministratorsCentralStaffRepositoryCustom {
    BigInteger checkExistEmail(String email);

    BigInteger checkExistPhone(String phone);

    List<AccountDto> search(AdministratorsCentralStaffSearchDto searchDto);

    BigInteger countSearch(AdministratorsCentralStaffSearchDto searchDto);

    boolean checkDeleteAuctionReq(Long accountId);
    boolean checkDeleteAutionRegister(Long accountId);
}
