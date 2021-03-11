package vn.tech.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.tech.website.store.model.Orders;

public interface OrderRepository extends CrudRepository<Orders, Long>, OrderRepositoryCustom {
}
