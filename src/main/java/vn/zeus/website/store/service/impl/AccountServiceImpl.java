package vn.zeus.website.store.service.impl;

import org.springframework.stereotype.Service;
import vn.zeus.website.store.repository.AccountRepository;
import vn.zeus.website.store.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
}
