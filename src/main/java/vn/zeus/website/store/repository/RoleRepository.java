package vn.zeus.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.zeus.website.store.model.Role;

import java.util.List;

public interface RoleRepository extends CrudRepository<Role, Integer>, RoleRepositoryCustom {
    Role findByCode(String code);
    Role findRoleByRoleId(Integer roleId);
    List<Role> findRolesByType(int type);
    List<Role> findRolesByTypeAndStatus(int type, int status);
}
