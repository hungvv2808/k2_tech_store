package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.api.ApiCareerGroup;

import java.util.List;

public interface ApiCareerGroupService {
    int uploadApiCareerGroup(List<ApiCareerGroup> list);
}
