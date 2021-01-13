package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.api.ApiCountry;

import java.util.List;

public interface ApiCountryService {
    int uploadCountry(List<ApiCountry> list);
}
