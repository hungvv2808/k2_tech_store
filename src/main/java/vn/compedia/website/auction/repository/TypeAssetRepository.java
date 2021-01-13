package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.TypeAsset;

import java.util.List;

public interface TypeAssetRepository extends CrudRepository<TypeAsset, Long>, TypeAssetRepositoryCustom {
    TypeAsset findByCode(String code);
    @Query("select r from TypeAsset r where r.status=true ")
    List<TypeAsset> findTypeAssetByStatus();

}
