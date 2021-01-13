package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.DecisionNewsFile;

import javax.transaction.Transactional;

public interface DecisionNewsFileRepository extends CrudRepository<DecisionNewsFile, Long>, DecisionNewsFileRepositoryCustom {
    DecisionNewsFile findDecisionNewsFileByDecisionNewsId(Long decisionNewsId);
    @Transactional
    void deleteByDecisionNewsId(Long decisionNewsId);
}
