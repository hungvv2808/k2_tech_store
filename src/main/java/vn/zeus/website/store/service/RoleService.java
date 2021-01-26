package vn.zeus.website.store.service;

import vn.zeus.website.store.dto.base.FunctionDto;
import vn.zeus.website.store.model.Role;

import java.util.List;

public interface RoleService {
    void save(Role roleId, List<FunctionDto> functionRoleList);
    void delete(Integer roleId);
}
