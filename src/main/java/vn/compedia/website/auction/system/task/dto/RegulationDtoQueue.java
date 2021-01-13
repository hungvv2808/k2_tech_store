package vn.compedia.website.auction.system.task.dto;

import lombok.Getter;
import lombok.Setter;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.model.AuctionRegister;

import java.util.*;

@Getter
@Setter
public class RegulationDtoQueue extends RegulationDto implements Comparable<RegulationDtoQueue> {
    private final Map<Long, Date> accountJoined = new HashMap<>(); // accountID, date thời gian tham gia
    private List<AuctionRegister> auctionRegisterList;
    private List<Long> accountIdDeposited = new ArrayList<>();
    private Date timeOverJoined;
    private boolean checkedDeposit;
    private Queue<AssetDtoQueue> assetDtoQueueList;

    public boolean isOvertimeJoined() {
        return new Date().compareTo(timeOverJoined) >= 0;
    }

    public void addAccountJoined(Long accountId) {
        accountJoined.computeIfAbsent(accountId, k -> new Date());
    }

    public void depositAllNotJoined() {
        for (AuctionRegister auctionRegister : auctionRegisterList) {
            if (!accountJoined.containsKey(auctionRegister.getAccountId())) {
                accountIdDeposited.add(auctionRegister.getAccountId());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        RegulationDtoQueue regulationDtoQueue = (RegulationDtoQueue) o;
        return this.getRegulationId().equals(regulationDtoQueue.getRegulationId());
    }

    @Override
    public int compareTo(RegulationDtoQueue regulationDtoQueue) {
        if (this.getStartTime().compareTo(regulationDtoQueue.getStartTime()) > 0) {
            return 1;
        } else if (this.getStartTime().compareTo(regulationDtoQueue.getStartTime()) < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
