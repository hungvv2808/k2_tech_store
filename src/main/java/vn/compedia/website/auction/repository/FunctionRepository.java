package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.Function;

public interface FunctionRepository extends CrudRepository<Function, Long>, FunctionRepositoryCustom {

}
