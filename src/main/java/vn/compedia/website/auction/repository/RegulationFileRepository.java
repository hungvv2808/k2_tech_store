package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.compedia.website.auction.model.RegulationFile;

import java.util.List;

public interface RegulationFileRepository extends CrudRepository<RegulationFile, Long>, RegulationFileRepositoryCustom {

    @Query("select r from RegulationFile r, Regulation re where r.regulationId = re.regulationId and r.type = 0 and r.regulationId = :regulationId")
    RegulationFile findLydoRegulationFileByRegulationId(@Param("regulationId") Long regulationId);

    RegulationFile findRegulationFileByRegulationIdAndType(Long RegulationId, Integer type);

    List<RegulationFile> findAllByRegulationId(Long regulationId);
    List<RegulationFile> deleteRegulationFileByRegulationId(Long regulationId);
    @Query("select r from RegulationFile r, Regulation re where r.regulationId = re.regulationId and r.type = :type and r.regulationId = :regulationId")
    RegulationFile getByRegulationId(@Param("type") int type, @Param("regulationId") Long regulationId);

    @Transactional
    void deleteByRegulationIdAndType(Long regulationId, Integer type);
}
