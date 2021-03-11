package vn.tech.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.tech.website.store.model.OrdersDetail;

public interface OrderDetailRepository extends CrudRepository<OrdersDetail, Long> {
}
