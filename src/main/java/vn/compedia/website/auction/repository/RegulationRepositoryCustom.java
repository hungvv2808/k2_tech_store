package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.dto.regulation.RegulationSearchDto;

import java.math.BigInteger;
import java.util.List;

public interface RegulationRepositoryCustom {
    List<RegulationDto> search(RegulationSearchDto searchDto);
    BigInteger countSearch(RegulationSearchDto searchDto);
    List<RegulationDto> searchForGuest(RegulationSearchDto regulationSearchDto);
    BigInteger countSearchForGuest(RegulationSearchDto regulationSearchDto);
    List<RegulationDto> searchForAuctionReqId(RegulationSearchDto searchDto, Long auctionReqId);
    BigInteger countSearchForAuctionReqId(RegulationSearchDto searchDto, Long auctionReqId);
    List<RegulationDto> searchForReport(RegulationSearchDto searchDto, Integer typeFile);
    BigInteger countSearchForReport(RegulationSearchDto searchDto, Integer typeFile);
    RegulationDto getRegulationDto(Long regulationId, int auctionStatus);
    RegulationDto getRegulationDtoByRegulationId(Long regulationId);
}
