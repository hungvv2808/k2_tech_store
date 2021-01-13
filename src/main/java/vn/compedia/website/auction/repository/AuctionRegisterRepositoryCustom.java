package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.auction.AuctionRegisterDto;
import vn.compedia.website.auction.dto.auction.AuctionRegisterSearchDto;

import java.math.BigInteger;
import java.util.List;

public interface AuctionRegisterRepositoryCustom {
    List<AuctionRegisterDto> search(AuctionRegisterSearchDto searchDto);
    BigInteger countSearch(AuctionRegisterSearchDto searchDto);
    List<AuctionRegisterDto> searchForGuest(AuctionRegisterSearchDto searchDto);
    BigInteger countSearchForGuest(AuctionRegisterSearchDto searchDto);
}
