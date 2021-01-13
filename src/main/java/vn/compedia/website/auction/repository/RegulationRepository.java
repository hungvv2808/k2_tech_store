package vn.compedia.website.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.model.Regulation;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface RegulationRepository extends JpaRepository<Regulation, Long>, RegulationRepositoryCustom {

    Regulation findRegulationByRegulationId(Long id);

    List<Regulation> findRegulationsByStatus(int status);

    @Query("select r.reasonCancel from Regulation r where r.regulationId = :regulationId")
    String findReasonCancelByRegulationId(@Param("regulationId") Long regulationId);

    @Query("select r.auctionReqId from Regulation r")
    List<Long> findAuctionReqId();

    @Query("select r.auctionFormalityId from Regulation r ")
    List<Long> findAuctionFormalityId();

    @Query("select r.auctionMethodId from Regulation r ")
    List<Long> findAuctionMethodId();

    Regulation findRegulationByRegulationIdAndAuctionStatus(Long id, Integer status);

    List<Regulation> findAllByStatusAndAuctionStatusAndStartTimeBetween(int status, int auctionStatus, Date fromDate, Date toDate);

    @Query("select new vn.compedia.website.auction.dto.regulation.RegulationDto(r.createDate, r.updateDate, r.createBy, r.updateBy, " +
            "r.regulationId, r.auctionReqId, r.auctionFormalityId, r.code, r.startTime, r.numberOfRounds, r.timePerRound, r.startRegistrationDate, " +
            "r.endRegistrationDate, r.auctionMethodId, r.historyStatus, r.status, r.reasonCancel, r.auctioneerId, r.realStartTime, r.realEndTime, " +
            "r.auctionStatus, r.endTime, r.paymentStartTime, r.paymentEndTime, r.retractPrice) from Regulation r where r.regulationId = :regulationId")
    RegulationDto getByRegulationId(@Param("regulationId") Long regulationId);

    @Query("select new vn.compedia.website.auction.dto.regulation.RegulationDto(r.regulationId, r.code, r.startTime, false , f.auctionFormalityId," +
            "f.name, m.auctionMethodId, m.name) " +
            "from Regulation r, AuctionFormality f, AuctionMethod m " +
            "where r.auctionFormalityId = f.auctionFormalityId " +
            "and r.auctionMethodId = m.auctionMethodId " +
            "and r.auctionReqId = :auctionReqId")
    List<RegulationDto> getRegulationDtoListByAuctionReqId(@Param("auctionReqId") Long auctionReqId);

    @Transactional
    @Modifying
    @Query("UPDATE Regulation r SET r.auctionStatus = :auctionStatus, r.realEndTime = :realEndTime WHERE r.regulationId = :regulationId")
    void changeAuctionStatus(@Param("regulationId") Long regulationId, @Param("auctionStatus") Integer auctionStatus, @Param("realEndTime") Date realEndTime);

    List<Regulation> findRegulationsByAuctioneerId(Long id);

    List<Regulation> findRegulationsByCreateBy(Long id);

    Integer countByStatus(int status);

    Integer countByAuctioneerIdAndStatusAndAuctionStatus(Long auctioneerId, int status, int auctionStatus);

    @Query("SELECT r.code from Regulation r where r.regulationId = :regulationId")
    String getCodeByRegulationId(@Param("regulationId") Long id);

    @Query("SELECT r from Regulation r where r.status <> :status order by r.regulationId desc, r.auctionStatus ASC ")
    List<Regulation> getRegulationOrderByAuctionStatus(@Param("status") int status);

    @Query("SELECT r from Regulation r where (r.auctionStatus = :ended or r.auctionStatus = :canceled) order by r.regulationId desc, r.auctionStatus ASC ")
    List<Regulation> findAllByAuctionStatusOrderByRegulationId(@Param("ended") int ended, @Param("canceled") int canceled);
}
