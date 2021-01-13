package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.api.ApiEducation;
import vn.compedia.website.auction.model.api.ApiRegular;

public interface ApiEducationRepository extends CrudRepository<ApiEducation, Long>, ApiEducationRepositoryCustom {

}
