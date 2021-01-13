package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.compedia.website.auction.model.HistorySystem;

public interface HistorySystemRepository extends JpaRepository<HistorySystem, Long>, HistorySystemRepositoryCustom {
}
