package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.user.PropertySaleHistoryDto;
import vn.compedia.website.auction.dto.user.PropertySaleHistorySearchDto;

import java.math.BigInteger;
import java.util.List;

public interface PropertySaleHistoryRepositoryCustom {
    List<PropertySaleHistoryDto> search(PropertySaleHistorySearchDto searchDto);
    BigInteger countSearch(PropertySaleHistorySearchDto searchDto);
}
