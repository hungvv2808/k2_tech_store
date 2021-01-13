package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.auction.TypeAssetSearchDto;
import vn.compedia.website.auction.model.TypeAsset;

import java.util.List;

public interface TypeAssetRepositoryCustom {
    List<TypeAsset> search(TypeAssetSearchDto typeAssetSearchDto);

    Long countSearch(TypeAssetSearchDto typeAssetSearchDto);
}
