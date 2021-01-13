package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.api.ApiJob;

import java.util.List;

public interface ApiJobService {
    int uploadJob(List<ApiJob> list);
}
