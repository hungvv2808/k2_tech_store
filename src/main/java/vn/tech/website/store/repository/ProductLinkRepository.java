package vn.tech.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.tech.website.store.model.ProductLink;

public interface ProductLinkRepository extends CrudRepository<ProductLink, Long> {
}
