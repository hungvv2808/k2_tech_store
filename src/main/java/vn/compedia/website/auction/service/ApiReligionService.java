package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.api.ApiReligion;

import java.util.List;

public interface ApiReligionService {
    int uploadReligion(List<ApiReligion> list);
}
