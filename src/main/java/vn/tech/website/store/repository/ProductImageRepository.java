package vn.tech.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.tech.website.store.model.ProductImage;

public interface ProductImageRepository extends CrudRepository<ProductImage, Long> {
}
