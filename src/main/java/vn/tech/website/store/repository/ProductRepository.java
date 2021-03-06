package vn.tech.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.tech.website.store.model.Product;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long>, ProductRepositoryCustom {

    List<Product> getAllByType(Integer type);
}
