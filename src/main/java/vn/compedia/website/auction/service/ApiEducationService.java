package vn.compedia.website.auction.service;


import vn.compedia.website.auction.model.api.ApiEducation;
import vn.compedia.website.auction.model.api.ApiNation;

import java.util.List;

public interface ApiEducationService {
    int uploadEducation(List<ApiEducation> list);
}
