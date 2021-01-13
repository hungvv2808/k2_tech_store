package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.District;

import javax.transaction.Transactional;
import java.util.List;

public interface DistrictRepository extends CrudRepository<District, Long>, DistrictRepositoryCustom {

    List <District> findByCode(String code);

    List<District> findByNameAndProvinceId(String code, Long pronvince);

    List<District> deleteDistrictsByProvinceId(Long provinceId);

    List<District> findDistrictsByProvinceId(Long provinceId);


    @Query(value = "SELECT district_id FROM district ORDER BY district_id DESC LIMIT 1", nativeQuery = true)
    Long getDistrictIdLast();
}
