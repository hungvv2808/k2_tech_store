package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.api.ApiRegulatoryAgency;

public interface ApiRegulatoryAgencyRepository extends CrudRepository<ApiRegulatoryAgency, Long>, ApiRegulatoryAgencyRepositoryCustom {

}
