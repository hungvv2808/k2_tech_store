package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.api.ApiPaper;

public interface ApiPaperRepository extends CrudRepository<ApiPaper, Long>, ApiPaperRepositoryCustom {
}
