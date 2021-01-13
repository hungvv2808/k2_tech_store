
package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.api.ApiCategoryPosition;
import vn.compedia.website.auction.model.api.ApiOfficials;

public interface ApiOfficialsRepository extends CrudRepository<ApiOfficials, Long>, ApiOfficialsRepositoryCustom {

}