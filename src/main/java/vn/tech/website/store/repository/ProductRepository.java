package vn.tech.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.tech.website.store.model.Product;

public interface ProductRepository extends CrudRepository<Product, Long>,ProductRepositoryCustom {
}
