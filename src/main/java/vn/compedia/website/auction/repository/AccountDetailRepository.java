package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.Account;

public interface AccountDetailRepository extends CrudRepository<Account,Long> , AccountDetailRepositoryCustom {
}
