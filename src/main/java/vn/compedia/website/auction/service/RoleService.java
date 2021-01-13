package vn.compedia.website.auction.service;

import vn.compedia.website.auction.dto.base.FunctionDto;
import vn.compedia.website.auction.model.Role;

import java.util.List;

public interface RoleService {
    void save(Role roleId, List<FunctionDto> functionRoleList);
    void delete(Integer roleId);
}
