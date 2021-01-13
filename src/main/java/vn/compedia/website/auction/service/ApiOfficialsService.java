package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.api.ApiCategoryPosition;
import vn.compedia.website.auction.model.api.ApiOfficials;

import java.util.List;

public interface ApiOfficialsService {
    int uploadApiOfficials(List<ApiOfficials> list);
}
