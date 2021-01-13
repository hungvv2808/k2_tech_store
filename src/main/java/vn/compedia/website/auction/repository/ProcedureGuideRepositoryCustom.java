package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.system.ProcedureGuideDto;
import vn.compedia.website.auction.dto.system.ProcedureGuideSearchDto;

import java.util.List;

public interface ProcedureGuideRepositoryCustom {
    List<ProcedureGuideDto> search(ProcedureGuideSearchDto procedureGuideSearchDto);
    Long countSearch(ProcedureGuideSearchDto procedureGuideSearchDto);
    List<ProcedureGuideDto> getProcedureGuideDto(ProcedureGuideSearchDto searchDto);
    Long countSearchFE(ProcedureGuideSearchDto searchDto);
}
