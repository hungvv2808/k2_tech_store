package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.api.ApiNation;
import vn.compedia.website.auction.model.api.ApiRegular;

public interface ApiRegularRepository extends CrudRepository<ApiRegular, Long>, ApiRegularRepositoryCustom {

}
