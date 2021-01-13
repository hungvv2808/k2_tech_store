package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.SystemConfig;

public interface SystemConfigRepository extends CrudRepository<SystemConfig, Long> {
    SystemConfig findSystemConfigBySystemConfigId(Long id);
}
