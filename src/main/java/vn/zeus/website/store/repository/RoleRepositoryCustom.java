package vn.zeus.website.store.repository;

import vn.zeus.website.store.dto.base.RoleSearchDto;
import vn.zeus.website.store.model.Role;

import java.util.List;

public interface RoleRepositoryCustom {
    List<Role> search(RoleSearchDto roleSearchDto);
    Long countSearch(RoleSearchDto roleSearchDto);
}
