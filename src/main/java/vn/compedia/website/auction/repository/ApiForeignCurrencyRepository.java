package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.api.ApiForeignCurrency;

public interface ApiForeignCurrencyRepository extends CrudRepository<ApiForeignCurrency,Long>, ApiForeignCurrencyRepositoryCustom {
}
