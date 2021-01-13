package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.api.ApiRegulatoryAgencies;

public interface ApiRegulatoryAgenciesRepository extends CrudRepository<ApiRegulatoryAgencies, Long>, ApiRegulatoryAgenciesRepositoryCustom {

}
