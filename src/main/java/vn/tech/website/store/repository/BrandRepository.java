package vn.tech.website.store.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.tech.website.store.model.Brand;

import java.util.List;

public interface BrandRepository extends CrudRepository<Brand, Long>,BrandRepositoryCustom {

    @Query("select b from Brand b")
    List<Brand> findAll();

    @Query("select b from Brand b where b.brandId <> :brandId")
    List<Brand> findAllGoodsExpertId(@Param("brandId") Long brandId);
}
