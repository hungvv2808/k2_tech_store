package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.DistrictVersion;
import vn.compedia.website.auction.model.ProvinceVersion;

import javax.transaction.Transactional;
import java.util.List;

public interface DistrictVersionRepository extends CrudRepository<DistrictVersion, Long> {
    List<DistrictVersion> findAllByVersionId(Long id);
    @Transactional
    List<DistrictVersion> deleteAllByVersionId(Long id);
}
