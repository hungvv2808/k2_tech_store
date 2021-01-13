package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.compedia.website.auction.model.ContentDetail;

public interface ContentDetailRepository extends JpaRepository<ContentDetail, Long> {
}
