package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.Accuracy;

import javax.transaction.Transactional;
import java.util.List;

public interface AccuracyRepository extends CrudRepository<Accuracy, Long>  {
    List<Accuracy> findByAccountIdAndType(Long accountId, boolean type);

    @Transactional
    @Modifying
    void deleteAllByAccountIdAndType(Long accountId, boolean type);
}
