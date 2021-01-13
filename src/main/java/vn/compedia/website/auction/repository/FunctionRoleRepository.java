package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.FunctionRole;

import javax.transaction.Transactional;

public interface FunctionRoleRepository extends CrudRepository<FunctionRole, Long>, FunctionRoleRepositoryCustom {
    @Transactional
    void deleteByRoleId(Integer roleId);
}
