package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.api.ApiRegulatoryAgencies;

import java.util.List;

public interface ApiRegulatoryAgenciesService {
    int uploadRegulatoryAgencies(List<ApiRegulatoryAgencies> list);
}
