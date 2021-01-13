package vn.compedia.website.auction.controller.frontend;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.frontend.common.FacesNoticeController;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.AuctionReq;
import vn.compedia.website.auction.repository.AccountRepository;
import vn.compedia.website.auction.repository.AuctionRegisterRepository;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.FacesUtil;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;

@Log4j2
@Named
@Getter
@Setter
@Scope(value = "session")
public class CommonFEController implements Serializable {
    @Inject
    private AuthorizationFEController authorizationFEController;
    @Inject
    private FacesNoticeController facesNoticeController;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;

    public boolean updateInfoBeforeJoin(AssetDto dto) {
        authorizationFEController.setCheck(true);
        if (!authorizationFEController.hasLogged()) {
            facesNoticeController.openModal("loginPopup");
            return false;
        }
        Account ac = accountRepository.findAccountByAccountId(authorizationFEController.getAccountDto().getAccountId());
        if (!ac.isFinishInfoStatus()) {
            String path = request.getHttpServletMapping().getMatchValue() + ".xhtml";
            if (path.equals("frontend/asset/asset_detail.xhtml")) {
                if (!ac.isOrg()) {
                    FacesUtil.redirect("/frontend/account/personal_account.xhtml?assetId=" + dto.getAssetId());
                    return false;
                }
                FacesUtil.redirect("/frontend/account/company_account.xhtml?assetId=" + dto.getAssetId());
                return false;
            }
            if (path.equals("frontend/regulation/regulation_detail.xhtml")) {
                if (!ac.isOrg()) {
                    FacesUtil.redirect("/frontend/account/personal_account.xhtml?regulationId=" + dto.getRegulationId() + "&assId=" + dto.getAssetId());
                    return false;
                }
                FacesUtil.redirect("/frontend/account/company_account.xhtml?regulationId=" + dto.getRegulationId() + "&assId=" + dto.getAssetId());
                return false;
            }
        }
        return true;
    }

    public boolean showButtonRegister(AssetDto dto, AuctionReq auctionReq) {
        try {
            return dto != null && DbConstant.ASSET_STATUS_WAITING == dto.getStatus()
                    && DbConstant.REGULATION_STATUS_CANCELED != dto.getRegulationStatus()
                    && dto.getAuctionRegisterId() == null
                    && new Date().compareTo(DateUtil.formatDate(dto.getStartRegistrationDate(), DateUtil.DATE_FORMAT_MINUTE)) >= 0
                    && dto.getEndRegistrationDate() != null
                    && new Date().compareTo(DateUtil.formatDate(dto.getEndRegistrationDate(), DateUtil.DATE_FORMAT_SECOND_END)) <= 0
                    && !auctionReq.getAccountId().equals(authorizationFEController.getAccountDto().getAccountId());
        } catch (Exception e) {
            log.error(e);
            return false;
        }
    }

    public boolean renderButtonPayment(AssetDto assetDto) {
        Date timeNow = new Date();
        return assetDto != null && assetDto.getAuctionRegisterId() != null && assetDto.getPaymentStartTime() != null && assetDto.getPaymentEndTime() != null
                && assetDto.getStatus() == DbConstant.ASSET_STATUS_WAITING && assetDto.getAuctionRegisterStatus() == DbConstant.AUCTION_REGISTER_STATUS_WAITING
                && DateUtil.formatDate(assetDto.getPaymentStartTime(), DateUtil.DATE_FORMAT_MINUTE).compareTo(timeNow) <= 0
                && timeNow.compareTo(DateUtil.formatDate(assetDto.getPaymentEndTime(), DateUtil.DATE_FORMAT_SECOND_END)) <= 0;
    }

    public String statusDeposit(boolean byAuctioneer, boolean retract) {
        if (retract) {
            return " do rút lại giá đã trả";
        }
        if (!byAuctioneer) {
            return " do không trả giá vòng trước";
        } else {
            return " bởi đấu giá viên";
        }
    }

}
