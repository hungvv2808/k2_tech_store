package vn.tech.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.tech.website.store.model.ProductDetail;

public interface ProductDetailRepository extends CrudRepository<ProductDetail, Long> {
}
