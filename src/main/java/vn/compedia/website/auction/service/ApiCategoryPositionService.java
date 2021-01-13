package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.api.ApiCategoryPosition;

import java.util.List;

public interface ApiCategoryPositionService {
    int uploadApiCategoryPosition(List<ApiCategoryPosition> list);
}
