package vn.compedia.website.auction.controller.frontend.user.auth;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.frontend.common.FacesNoticeController;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.repository.AccountRepository;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@Named
@Scope(value = "session")
@Getter
@Setter
public class ActivationAccountController {
    @Inject
    private FacesNoticeController facesNoticeController;
    @Inject
    HttpServletRequest request;
    @Autowired
    private AccountRepository accountRepository;

    private String text;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            resetAll();
        }
    }

    public void resetAll() {
        text = null;
        String token = request.getParameter("token");
        if (StringUtils.isBlank(token)) {
            FacesUtil.redirect("/frontend/index.xhtml");
            return;
        }
        Date timeNow = new Date();
        Account account = accountRepository.findAccountByActiveToken(token);
        if (account != null) {
            if (account.getActiveToken().equals(token) && account.getAccountStatus().equals(DbConstant.ACCOUNT_INACTIVE_STATUS)) {
                account.setActiveToken(null);
                account.setTokenExpire(null);
                account.setAccountStatus(DbConstant.ACCOUNT_ACTIVE_STATUS);
                accountRepository.save(account);
                facesNoticeController.addSuccessMessage("Kích hoạt tài khoản thành công!");
                facesNoticeController.openModal("loginPopup");
            } else {
                facesNoticeController.addErrorMessage("Mã kích hoạt không đúng hoặc đã hết hạn vui lòng thử lại!");
            }
        }
        FacesUtil.redirect("/frontend/index.xhtml");
    }
}
