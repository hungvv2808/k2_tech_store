package vn.tech.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.tech.website.store.model.Payments;

public interface PaymentsRepository extends CrudRepository<Payments,Long> {
}
