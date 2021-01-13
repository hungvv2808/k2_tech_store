package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.ProvinceVersion;

import javax.transaction.Transactional;
import java.util.List;

public interface ProvinceVersionRepository extends CrudRepository<ProvinceVersion, Long> {
    List<ProvinceVersion> findAllByVersionId(Long id);

    @Transactional
    List<ProvinceVersion> deleteAllByVersionId(Long id);
}
