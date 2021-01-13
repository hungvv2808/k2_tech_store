package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.api.ApiForeignCurrency;

import java.util.List;

public interface ApiForeignCurrencyService {
    int uploadApiForeignCurrency(List<ApiForeignCurrency> list);
}
