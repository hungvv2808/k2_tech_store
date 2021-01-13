package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.DecisionNews;

import java.util.List;

public interface DecisionNewsRepository extends CrudRepository<DecisionNews, Long>, DecisionNewsRepositoryCustom {
    DecisionNews findDecisionNewsByDecisionNewsId(Long Id);
    List<DecisionNews> findTop10ByTypeAndStatusOrderByCreateDateDesc(boolean type, boolean status);
}
