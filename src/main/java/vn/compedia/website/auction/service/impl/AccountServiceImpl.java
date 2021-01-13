package vn.compedia.website.auction.service.impl;

import org.springframework.stereotype.Service;
import vn.compedia.website.auction.repository.AccountRepository;
import vn.compedia.website.auction.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
}
