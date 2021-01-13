package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.api.ApiBusinessJob;

import java.util.List;

public interface ApiBusinessJobService {
    int uploadApiBusinessJob(List<ApiBusinessJob> list);
}
