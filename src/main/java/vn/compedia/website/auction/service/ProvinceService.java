package vn.compedia.website.auction.service;


import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.model.Province;
import vn.compedia.website.auction.model.ProvinceApi;

import java.util.List;

public interface ProvinceService {
        void deleteProvince(Long id);
        void save(Province province);
        void save(ProvinceApi provinceApi);
}
