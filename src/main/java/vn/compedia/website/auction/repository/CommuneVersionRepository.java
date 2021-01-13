package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.CommuneVersion;

import javax.transaction.Transactional;
import java.util.List;

public interface CommuneVersionRepository extends CrudRepository<CommuneVersion, Long> {
    List<CommuneVersion> findAllByVersionId(Long id);
    @Transactional
    List<CommuneVersion> deleteAllByVersionId(Long id);
}
