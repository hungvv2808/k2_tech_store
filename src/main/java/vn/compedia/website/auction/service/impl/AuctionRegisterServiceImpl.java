package vn.compedia.website.auction.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.compedia.website.auction.model.AuctionRegister;
import vn.compedia.website.auction.repository.AuctionRegisterRepository;
import vn.compedia.website.auction.service.AuctionRegisterService;
import vn.compedia.website.auction.util.DbConstant;

@Service
public class AuctionRegisterServiceImpl implements AuctionRegisterService {
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;

    @Override
    public void depositBySystem(Long auctionRegisterId, String reason) {
        // update
        AuctionRegister auctionRegister = auctionRegisterRepository.findById(auctionRegisterId).orElse(null);
        if (auctionRegister != null) {
            auctionRegister.setReasonDeposition("Hệ thống tự động truất quyền do " + reason);
            auctionRegister.setStatusDeposit(DbConstant.AUCTION_REGISTER_STATUS_DEPOSIT_REFUSE);
            auctionRegisterRepository.saveAndFlush(auctionRegister);
        }
    }
}
