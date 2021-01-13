package vn.compedia.website.auction.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.compedia.website.auction.dto.auction.AuctionRegisterSearchDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.repository.AuctionHistoryRepository;
import vn.compedia.website.auction.service.AuctionHistoryService;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuctionHistoryServiceImpl implements AuctionHistoryService {
    @Autowired
    private AuctionHistoryRepository auctionHistoryRepository;

    @Override
    public List<RegulationDto> search(AuctionRegisterSearchDto auctionRegisterSearchDto) {
        auctionRegisterSearchDto.setGroupByFields("r.regulation_id");
        List<RegulationDto> temp = auctionHistoryRepository.search(auctionRegisterSearchDto);
        List<RegulationDto> regulationDtoList = new ArrayList<>();
        for (RegulationDto regulationDto : temp) {
            auctionRegisterSearchDto.setPageSize(0);
            auctionRegisterSearchDto.setGroupByFields(null);
            auctionRegisterSearchDto.setRegulationId(regulationDto.getRegulationId());
            List<RegulationDto> tempAll = auctionHistoryRepository.search(auctionRegisterSearchDto);
            regulationDtoList.add(tempAll.get(0));
        }
        return regulationDtoList;
    }

    @Override
    public Long countSearch(AuctionRegisterSearchDto auctionRegisterSearchDto) {
        auctionRegisterSearchDto.setRegulationId(null);
        auctionRegisterSearchDto.setGroupByFields("r.regulation_id");
        return auctionHistoryRepository.countSearch(auctionRegisterSearchDto);
    }
}
