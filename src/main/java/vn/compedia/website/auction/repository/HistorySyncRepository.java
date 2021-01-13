package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.dto.api.HistorySyncSearchDto;
import vn.compedia.website.auction.model.HistorySync;

import javax.transaction.Transactional;
import java.util.List;

public interface HistorySyncRepository extends CrudRepository<HistorySync, Long> {
    List<HistorySync> search(HistorySyncSearchDto searchDto);
    Long countSearch(HistorySyncSearchDto searchDto);
    HistorySync findAllByVersionId(Long id);
    @Transactional
    void deleteAllByVersionId(Long id);
}


