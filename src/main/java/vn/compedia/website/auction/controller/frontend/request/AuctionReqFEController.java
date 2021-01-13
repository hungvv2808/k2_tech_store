package vn.compedia.website.auction.controller.frontend.request;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.frontend.AuthorizationFEController;
import vn.compedia.website.auction.controller.frontend.BaseFEController;
import vn.compedia.website.auction.controller.frontend.common.FacesNoticeController;
import vn.compedia.website.auction.controller.frontend.regulation.RegulationIsInFinishController;
import vn.compedia.website.auction.dto.request.AuctionReqDto;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.AuctionReq;
import vn.compedia.website.auction.repository.AccountRepository;
import vn.compedia.website.auction.repository.AuctionReqRepository;
import vn.compedia.website.auction.util.*;
import vn.compedia.website.auction.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@Named
@Scope(value = "session")
@Getter
@Setter
public class AuctionReqFEController extends BaseFEController {
    @Inject
    private HttpServletRequest request;
    @Inject
    private FacesNoticeController facesNoticeController;
    @Inject
    private AuthorizationFEController authorizationFEController;

    @Autowired
    protected AuctionReqRepository auctionReqRepository;
    @Autowired
    protected AccountRepository accountRepository;

    private AuctionReq auctionReq;
    private AuctionReqDto auctionReqDto;
    private Account account;
    private String id;

    public void showInfo() {
        id = request.getParameter("id");
        if (!FacesContext.getCurrentInstance().isPostback()) {
            auctionReq = new AuctionReq();
        }
    }

    public void saveAuctionReq() {
        if (authorizationFEController.getAccountDto().getAccountId() == null) {
            setErrorForm( "Vui lòng đăng nhập hoặc đăng ký tài khoản để đăng ký bán đấu giá!");
            return;
        }
        if (StringUtils.isBlank(auctionReq.getAssetName().trim())) {
            setErrorForm( "Bạn vui lòng nhập tên tài sản");
            return;
        }
        if (StringUtils.isBlank(auctionReq.getAssetDescription().trim())) {
            setErrorForm( "Mô tả tài sản là trường bắt buộc");
            return;
        }
        auctionReq.setStatus(DbConstant.CHUA_XU_LY_ID);
        auctionReq.setAccountId(authorizationFEController.getAccountDto().getAccountId());
        auctionReq.setCreateBy(authorizationFEController.getAccountDto().getAccountId());
        auctionReqRepository.save(auctionReq);
        auctionReq = new AuctionReq();
        setSuccessForm("Đăng ký bán tài sản thành công, Vui lòng chờ duyệt!");
        facesNoticeController.closeModal("auctionPopup");
    }

    public void resetForm() {
        this.auctionReq = new AuctionReq();
    }

    @Override
    protected String getMenuId() {
        return Constant.ID_DON_YEU_CAU_DAU_GIA;
    }
}

