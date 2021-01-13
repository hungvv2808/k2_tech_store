package vn.compedia.website.auction.service;


import vn.compedia.website.auction.model.api.ApiNation;

import java.util.List;

public interface ApiNationService {
    int uploadNation(List<ApiNation> list);
}
