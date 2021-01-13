package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.api.ApiCareerGroup;

public interface ApiCareerGroupRepository extends CrudRepository<ApiCareerGroup, Long>,ApiCareerGroupRepositoryCustom {
}
