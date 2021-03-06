package vn.tech.website.store.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.tech.website.store.model.Category;
import vn.tech.website.store.util.DbConstant;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category,Long>, CategoryRepositoryCustom {

    @Query("select c from Category c where c.status = "+ DbConstant.STATUS_CATEGORY_ACTIVE)
    List<Category> findAll();

    @Query("select c from Category c where c.categoryId <> :categoryId and c.status = "+ DbConstant.STATUS_CATEGORY_ACTIVE)
    List<Category> findAllExpertId(@Param("categoryId") Long categoryId);
}
