package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.compedia.website.auction.dto.auction.PriceRoundDto;
import vn.compedia.website.auction.model.PriceRound;

import java.util.Date;
import java.util.List;

public interface PriceRoundRepository extends JpaRepository<PriceRound, Long>, PriceRoundRepositoryCustom {
    @Query("select new vn.compedia.website.auction.dto.auction.PriceRoundDto(pr.priceRoundId, pr.regulationId, pr.assetId, pr.numberOfRound, " +
            "pr.startingPrice, pr.highestPrice, pr.auctionRegisterId) from PriceRound pr where pr.assetId = :assetId order by pr.priceRoundId asc")
    List<PriceRoundDto> getAllPriceRoundDtoByAssetId(@Param("assetId") Long assetId);

    @Query("select new vn.compedia.website.auction.model.PriceRound(pr.priceRoundId, pr.regulationId, pr.assetId,pr.numberOfRound ," +
            "pr.startingPrice, pr.highestPrice,pr.auctionRegisterId ) from PriceRound pr where pr.assetId=:assetId order by pr.numberOfRound desc ")
    PriceRound findPriceRoundByAssetId(Long assetId);

    PriceRound findFirstByAssetIdOrderByNumberOfRoundDesc(Long assetId);

    @Transactional
    void deleteAllByRegulationId(Long regulationId);

    @Transactional
    @Modifying
    @Query("UPDATE PriceRound SET auctionRegisterId = :auctionRegisterId, highestPrice = :highestPrice, updateDate = :updateDate WHERE priceRoundId = :priceRoundId")
    void updateHighestPrice(@Param("priceRoundId") Long priceRoundId, @Param("auctionRegisterId") Long auctionRegisterId, @Param("highestPrice") Long highestPrice, @Param("updateDate") Date updateDate);
}
