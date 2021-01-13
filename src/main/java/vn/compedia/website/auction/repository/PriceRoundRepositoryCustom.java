package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.auction.PriceRoundDto;
import vn.compedia.website.auction.dto.auction.PriceRoundSearchDto;

import java.math.BigInteger;
import java.util.List;

public interface PriceRoundRepositoryCustom {
    List<PriceRoundDto> searchForUser(PriceRoundSearchDto priceRoundSearchDto);
    BigInteger countSearchForUser(PriceRoundSearchDto priceRoundSearchDto);

}
