package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.base.HistorySystemDto;
import vn.compedia.website.auction.dto.base.HistorySystemSearchDto;

import java.math.BigInteger;
import java.util.List;

public interface HistorySystemRepositoryCustom {
    List<HistorySystemDto> search(HistorySystemSearchDto searchDto);
    BigInteger countSearch(HistorySystemSearchDto searchDto);
}
