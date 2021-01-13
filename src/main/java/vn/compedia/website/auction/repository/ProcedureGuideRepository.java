package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.compedia.website.auction.dto.system.DecisionNewsDto;
import vn.compedia.website.auction.dto.system.DecisionNewsSearchDto;
import vn.compedia.website.auction.dto.system.ProcedureGuideDto;
import vn.compedia.website.auction.dto.system.ProcedureGuideSearchDto;
import vn.compedia.website.auction.model.ProcedureGuide;

import java.util.List;

public interface ProcedureGuideRepository extends CrudRepository<ProcedureGuide, Long>, ProcedureGuideRepositoryCustom {
    List<ProcedureGuide> findAll();

    ProcedureGuide findProcedureGuideByProcedureGuideId(Long id);

    @Query("select pr from ProcedureGuide pr where pr.status = :status order by pr.procedureGuideId desc")
    List<ProcedureGuide> getProcedureGuideSortById(boolean status);

    @Query(value = "select * from procedure_guide pr where pr.status = :status order by pr.procedure_guide_id desc limit :limit ", nativeQuery = true)
    List<ProcedureGuide> getProcedureGuideSortByIdLimit(boolean status, int limit);

    @Query("select pr from ProcedureGuide pr where pr.title like :title and pr.status = :status order by pr.procedureGuideId desc")
    List<ProcedureGuide> getProcedureGuideByTitle(@Param("title") String title, boolean status);
}
