package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.ContactInfo;

import java.util.List;

public interface ContactInfoRepository extends CrudRepository<ContactInfo, Long> {
    List<ContactInfo> getAllByType(boolean type);
}
