package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.api.ApiRegulatoryAgency;

import java.util.List;

public interface ApiRegulatoryAgencyService {
    int uploadRegulatoryAgency(List<ApiRegulatoryAgency> list);
}
