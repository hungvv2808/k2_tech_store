package vn.zeus.website.store.repository;

import vn.zeus.website.store.dto.base.FunctionDto;

import java.util.List;

public interface FunctionRepositoryCustom {
    List<FunctionDto> findFunctionsByRoleId(Integer roleId);
    List<Integer> findRoleIdsFromScopes(String... scope);
}
