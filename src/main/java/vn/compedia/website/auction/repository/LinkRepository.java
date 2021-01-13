package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.Link;

public interface LinkRepository extends CrudRepository<Link,Long> {
}
