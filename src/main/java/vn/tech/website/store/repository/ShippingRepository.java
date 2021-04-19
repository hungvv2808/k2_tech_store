package vn.tech.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.tech.website.store.model.Shipping;

public interface ShippingRepository extends CrudRepository<Shipping, Long>, ShippingRepositoryCustom {
}
