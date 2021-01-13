package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.system.DecisionNewsDto;
import vn.compedia.website.auction.dto.system.DecisionNewsSearchDto;

import java.util.List;

public interface DecisionNewsRepositoryCustom {
    List<DecisionNewsDto> search(DecisionNewsSearchDto decisionNewsSearchDto);
    Long countSearch(DecisionNewsSearchDto decisionNewsSearchDto);
    List<DecisionNewsDto> getDecisionNewsDto(DecisionNewsSearchDto searchDto);
    Long countSearchFE(DecisionNewsSearchDto decisionNewsSearchDto);
}
