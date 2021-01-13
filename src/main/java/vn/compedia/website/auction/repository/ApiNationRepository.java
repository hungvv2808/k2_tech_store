package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.api.ApiNation;

public interface ApiNationRepository extends CrudRepository<ApiNation, Long>, ApiNationRepositoryCustom {

}
