package vn.compedia.website.auction.service;


import vn.compedia.website.auction.model.api.ApiNation;
import vn.compedia.website.auction.model.api.ApiRegular;

import java.util.List;

public interface ApiRegularService {
    int uploadRigular(List<ApiRegular> list);
}
