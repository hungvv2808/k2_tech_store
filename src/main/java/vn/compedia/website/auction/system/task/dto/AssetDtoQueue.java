package vn.compedia.website.auction.system.task.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.model.AuctionRegister;
import vn.compedia.website.auction.model.PriceRound;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;

import javax.websocket.Session;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class AssetDtoQueue extends AssetDto implements Comparable<AssetDtoQueue> {
    // REDIS NODE ID
    private int nodeIdController;
    //
    private boolean firstAsset;
    private boolean inProcessing;               // dang dau gia?
    private boolean ended;                      // trang thai da ket thuc
    private boolean cancel;                     // huy bo tai san
    private boolean cancelRegulation;           // huy bo quy che
    private PriceRound priceRoundCurrent;       // priceRound vong hien tai
    // BARGAIN LIST / accountId
    private TreeMap<Long, Long> priceBargained = new TreeMap<>(); // accountId, money
    @Getter(onMethod_={@Synchronized}) @Setter(onMethod_={@Synchronized})
    private List<Bargain> bargainList = new LinkedList<>(); // accountId, money
    // ON-BARGAIN
    private Date startTimeRound;                // thoi gian bat dau vong hien tai
    private Date endTimeRound;                  // thoi gian ket thuc vong hien tai
    private Date timeAcceptPrice;               // thoi gian chap nhan gia
    private Integer roundTotalDown;             // tong vong giam gia
    private Long currentPrice;                  // gia hien tai
    // CONFIRM
    private boolean lockBargain;
    private boolean inTimeConfirm;              // dang trong thoi gian xac nhan?
    private boolean inTimeRandom;               // for wait random
    private Date endTimeRandom;                 // end time random
    private Integer randomCase;                     //
    private Integer countdownRandom;            // random times
    private Integer roundConfirm;               // vong hien tai xac nhan
    private Integer timeConfirmMinute;          // thời gian xac nhan
    private boolean deposit1th;
    private boolean refuseWinner1th;
    // WINNER
    private Bargain winner1th;                  // winner 1th
    private Bargain winner;                     // winner
    private boolean refuseWinner;               // tu choi thang cuoc
    private boolean acceptWinner;               // chap nhan thang cuoc
    // check deposit auto
    private boolean checkedDeposit;
    // SESSIONS CONNECTED
    private Map<Long, Date> accountJoined = new HashMap<>(); // accountID, date thời gian tham gia
    private transient Map<Long, Set<Session>> SESSIONS = new HashMap<>(); // accountID, session
    private transient Map<String, Session> guestSESSIONS = new HashMap<>(); // sessionId, session
    //
    private Set<Long> accountIdNotifyList = new HashSet<>(); // accountID for notify
    private Set<Long> accountIdDepositList = new HashSet<>(); // accountID truat quyen
    private Set<Long> accountIdDepositSystem = new HashSet<>(); // accountID truat quyen boi he thong
    private Set<Long> accountIdDepositShowed = new HashSet<>(); // accountID notified
    private Set<Long> accountIdBargained = new LinkedHashSet<>(); // accountID bargained
    // winners for display
    private Map<Long, Bargain> accountIdWinner1thList = new LinkedHashMap<>(); // accountID winner 1th
    private Map<Long, Bargain> accountIdWinner2thList = new LinkedHashMap<>(); // accountID winner 2th
    private Map<Long, Bargain> accountOpportunityList = new LinkedHashMap<>(); // accountID opportunity
    private Set<Long> accountIdRefuseWinnerList = new LinkedHashSet<>(); // accountID refuse winner

    public synchronized boolean hasDeposition(Long accountId) {
        return accountIdDepositList.contains(accountId)
                || getAuctionRegisterList().stream().anyMatch(e -> e.getAccountId().equals(accountId) && e.getStatusDeposit().equals(DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_REFUSE));
    }

    public synchronized boolean hasDepositionBySystem(Long accountId) {
        return accountIdDepositSystem.contains(accountId);
    }

    public boolean hasOpportunity(Long accountId) {
        return accountOpportunityList.containsKey(accountId);
    }

    public void deposition(Long accountId) {
        if (!hasDeposition(accountId)) {
            accountIdDepositList.add(accountId);
        }
    }

    public boolean checkDepositShow(Long accountId) {
        return accountIdDepositShowed.contains(accountId);
    }

    public void showedDeposit(Long accountId) {
        accountIdDepositShowed.add(accountId);
    }

    public List<Session> getSessionList() {
        List<Session> sessions = SESSIONS.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        sessions.addAll(guestSESSIONS.values());
        return sessions;
    }

    public List<Session> getSessionRegisterNotIn(List<Long> accountIdList) {
        List<Session> output = new ArrayList<>();
        accountIdNotifyList.forEach(e -> {
            if (!accountIdList.contains(e) && SESSIONS.get(e) != null) {
                output.addAll(SESSIONS.get(e));
            }
        });
        return output;
    }

    public List<Session> getSessionRegister() {
        List<Session> output = new ArrayList<>();
        accountIdNotifyList.forEach(e -> {
            if (SESSIONS.get(e) != null) {
                output.addAll(SESSIONS.get(e));
            }
        });
        return output;
    }

    public List<Session> getSessionsByAccountId(Long accountId) {
        if (SESSIONS.get(accountId) == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(SESSIONS.get(accountId));
    }

    public List<Session> getSessionsByAccountIdIn(List<Long> accountIdList) {
        List<Session> output = new ArrayList<>();
        for (Long accountId : accountIdList) {
            if (SESSIONS.get(accountId) != null) {
                output.addAll(SESSIONS.get(accountId));
            }
        }
        return output;
    }


    public Bargain getBargain(int serial) {
        if (bargainList.isEmpty() || bargainList.size() < serial || serial < 1) {
            return null;
        }

        return bargainList.get(bargainList.size() - serial);
    }

    // get all winner, from serial
    public List<Bargain> winner(int serial) {
        List<Bargain> bargainList = new LinkedList<>();
        Bargain bargain = getBargain(serial);
        if (bargain == null) {
            return bargainList;
        }

        bargainList.add(bargain);
        Long money = bargain.getMoney();
        do {
            bargain = getBargain(++serial);
            if (bargain == null) {
                return bargainList;
            }
            if (money.equals(bargain.getMoney())) {
                bargainList.add(bargain);
            }
        } while (bargain.getMoney().equals(money));

        return bargainList;
    }

    // get all winner 1th (same price)
    public List<Bargain> winner1th() {
        return winner(1);
    }

    // get all winner 2th (same price)
    public List<Bargain> winner2th() {
        int serial = 1;
        List<Bargain> bargainList = new LinkedList<>();
        List<Bargain> bargainListTemp = winner1th();
        if (bargainListTemp.isEmpty()) {
            return bargainList;
        }

        Long money = bargainListTemp.get(0).getMoney();
        bargainList = winner(bargainListTemp.size() + serial);

        if (DbConstant.AUCTION_FORMALITY_ID_POLL == getAuctionFormalityId() && bargainListTemp.size() == 1) {
            while (bargainList.size() == 1 && bargainList.get(0).getAccountId().equals(bargainListTemp.get(0).getAccountId())) {
                bargainList = winner(bargainListTemp.size() + ++serial);
            }
        }

        if (bargainList.isEmpty() || (bargainList.get(0).getMoney() + getDeposit() < money)) {
            return new LinkedList<>();
        }
        return bargainList;
    }

    public void addBargain(Long bidId, Long priceRoundId, Long accountId, Long auctionRegisterId, Long money) {
        Bargain bargain = new Bargain(priceRoundId, accountId, auctionRegisterId, money);
        bargain.setBidId(bidId);
        bargainList.add(bargain);
    }

    public void removeLastBargain() {
        Bargain bargain = bargainList.get(bargainList.size() - 1);
        priceBargained.remove(bargain.getAccountId());
        bargainList.remove(bargain);
    }

    public boolean hasBargained(Long accountId) {
        return priceBargained.get(accountId) != null;
    }

    public Long getBargained(Long accountId) {
        return priceBargained.get(accountId);
    }

    public Date endTimeAcceptPrice() {
        try {
            return DateUtil.plusMinute(timeAcceptPrice, timeConfirmMinute);
        } catch (Exception e) {
            return new Date();
        }
    }

    public AuctionRegister getInfoAuctionRegister(Long accountId) {
        try {
            return getAuctionRegisterList().stream().filter(e -> e.getAccountId().equals(accountId)).findFirst().get();
        } catch (Exception e) {
            return new AuctionRegister();
        }
    }

    public List<Long> getAuctionRegisterIdByAccountId(List<Long> accountIdList) {
        List<Long> output = new LinkedList<>();
        for (Long accountId : accountIdList) {
            output.add(getInfoAuctionRegister(accountId).getAuctionRegisterId());
        }
        return output;
    }

    public boolean hasAllDeposit() {
        return accountIdDepositList.size() == getAuctionRegisterList().size();
    }

    @Override
    public boolean equals(Object o) {
        AssetDtoQueue assetDtoQueue = (AssetDtoQueue) o;
        return this.getAssetId().equals(assetDtoQueue.getAssetId());
    }

    @Override
    public int compareTo(AssetDtoQueue assetDtoQueue) {
        return Integer.compare(this.getStartTime().compareTo(assetDtoQueue.getStartTime()), 0);
    }
}
