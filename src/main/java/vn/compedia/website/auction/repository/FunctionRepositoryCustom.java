package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.base.FunctionDto;

import java.util.List;

public interface FunctionRepositoryCustom {
    List<FunctionDto> findFunctionsByRoleId(Integer roleId);
    List<Integer> findRoleIdsFromScopes(String... scope);
}
