package vn.zeus.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.zeus.website.store.model.Function;

public interface FunctionRepository extends CrudRepository<Function, Long>, FunctionRepositoryCustom {

}
