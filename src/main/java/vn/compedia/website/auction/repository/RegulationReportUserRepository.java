package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.compedia.website.auction.model.RegulationReportUser;

public interface RegulationReportUserRepository extends CrudRepository<RegulationReportUser, Long> {
    @Query("select ru from RegulationReportFile rf, RegulationReportUser ru " +
            "where rf.regulationReportFileId = ru.regulationReportFileId " +
            "and rf.regulationId = :regulationId and ru.assetId = :assetId and ru.createBy = :accountId")
    RegulationReportUser findRegulationReportUser(@Param("regulationId") Long regulationId, @Param("assetId") Long assetId, @Param("accountId") Long accountId);
}
