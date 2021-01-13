package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.api.ApiCountry;

public interface ApiFamilyRelationshipRepository extends CrudRepository<ApiCountry, Long>, ApiFamilyRelationshipRepositoryCustom {

}
