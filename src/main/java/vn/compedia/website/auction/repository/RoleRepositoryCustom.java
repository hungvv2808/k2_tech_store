package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.base.RoleSearchDto;
import vn.compedia.website.auction.model.Role;

import java.util.List;

public interface RoleRepositoryCustom {
    List<Role> search(RoleSearchDto roleSearchDto);
    Long countSearch(RoleSearchDto roleSearchDto);
}
