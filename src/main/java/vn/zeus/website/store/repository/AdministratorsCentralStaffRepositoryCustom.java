package vn.zeus.website.store.repository;

import vn.zeus.website.store.dto.user.AccountDto;
import vn.zeus.website.store.dto.user.AdministratorsCentralStaffSearchDto;

import java.math.BigInteger;
import java.util.List;

public interface AdministratorsCentralStaffRepositoryCustom {
    BigInteger checkExistEmail(String email);

    BigInteger checkExistPhone(String phone);

    List<AccountDto> search(AdministratorsCentralStaffSearchDto searchDto);

    BigInteger countSearch(AdministratorsCentralStaffSearchDto searchDto);

    boolean checkDeleteStoreReq(Long accountId);
    boolean checkDeleteAutionRegister(Long accountId);
}
