
package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.api.ApiCategoryPosition;

public interface ApiCategoryPositionRepository extends CrudRepository<ApiCategoryPosition, Long>, ApiCategoryPositionRepositoryCustom {

}