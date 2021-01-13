package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.Role;

import java.util.List;

public interface RoleRepository extends CrudRepository<Role, Integer>, RoleRepositoryCustom {
    Role findByCode(String code);
    Role findRoleByRoleId(Integer roleId);
    List<Role> findRolesByType(int type);
    List<Role> findRolesByTypeAndStatus(int type, int status);
}
