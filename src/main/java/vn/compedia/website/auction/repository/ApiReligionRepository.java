package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.api.ApiReligion;

public interface ApiReligionRepository extends CrudRepository<ApiReligion, Long>, ApiReligionRepositoryCustom {
}
