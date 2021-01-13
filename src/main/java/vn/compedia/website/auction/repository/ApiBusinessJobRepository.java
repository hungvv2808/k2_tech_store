package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.api.ApiBusinessJob;

public interface ApiBusinessJobRepository extends CrudRepository<ApiBusinessJob, Long>, ApiBusinessJobRepositoryCustom {

}
