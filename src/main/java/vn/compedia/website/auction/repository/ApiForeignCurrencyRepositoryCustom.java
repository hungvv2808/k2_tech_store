package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiForeignCurrencySearchDto;
import vn.compedia.website.auction.model.api.ApiForeignCurrency;

import java.util.List;

public interface ApiForeignCurrencyRepositoryCustom {
    List<ApiForeignCurrency> search(ApiForeignCurrencySearchDto apiForeignCurrencySearchDto);
    Long countSearch(ApiForeignCurrencySearchDto apiForeignCurrencySearchDto);
    void deleteAllRecords();
}
