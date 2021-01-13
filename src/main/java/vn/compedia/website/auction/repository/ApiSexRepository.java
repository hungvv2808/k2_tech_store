package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.api.ApiSex;

public interface ApiSexRepository extends CrudRepository<ApiSex, Long>, ApiSexRepositoryCustom {

}
