package vn.tech.website.store.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.tech.website.store.model.Product;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long>, ProductRepositoryCustom {

    List<Product> getAllByType(Integer type);

    @Query("select max(p.countCode) from Product p")
    Long findMaxCountCode();

    List<Product> findByCode(String code);

    Product getByProductId(Long productId);
}
