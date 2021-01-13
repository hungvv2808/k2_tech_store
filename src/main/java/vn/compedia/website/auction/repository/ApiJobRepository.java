package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.api.ApiJob;

public interface ApiJobRepository extends CrudRepository<ApiJob, Long>, ApiJobRepositoryCustom {
}
