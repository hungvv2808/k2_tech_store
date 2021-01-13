package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import vn.compedia.website.auction.model.RegulationReportFile;

public interface RegulationReportFileRepository extends CrudRepository<RegulationReportFile, Long> {
    RegulationReportFile findByRegulationId(Long regulationId);

    @Transactional
    void deleteByRegulationId(Long regulationId);

}
