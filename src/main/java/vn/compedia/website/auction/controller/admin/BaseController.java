package vn.compedia.website.auction.controller.admin;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import vn.compedia.website.auction.controller.admin.auth.AuthFunctionController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.entity.EFunction;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.Asset;
import vn.compedia.website.auction.repository.AccountRepository;
import vn.compedia.website.auction.repository.AssetRepository;
import vn.compedia.website.auction.util.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public abstract class BaseController implements Serializable {
    @Inject
    @Named
    LanguageController languageController;
    @Inject
    AuthorizationController authorizationController;
    @Inject
    ActionSystemController actionSystemController;
    @Inject
    CommonController commonController;

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    AccountRepository accountRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected abstract EScope getMenuId();


    public void init() {
        // Check login
        if (!authorizationController.hasLogged()) {
            FacesUtil.redirect("/admin/login.xhtml");
            return;
        }
        // Check block account
        Account account = accountRepository.getAccountByAccountId(authorizationController.getAccountDto().getAccountId());
        if (account == null || Arrays.asList(DbConstant.ACCOUNT_DELETE_STATUS, DbConstant.ACCOUNT_LOCK_STATUS).contains(account.getAccountStatus())) {
            authorizationController.resetAll();
            FacesUtil.redirect("/admin/login.xhtml");
            return;
        }
        // Check first time login
        if (!authorizationController.getAccountDto().isLoginFromSso()
                && !authorizationController.getAccountDto().isFirstTimeLogin()) {
            FacesUtil.redirect("/admin/doi-mat-khau.xhtml");
            return;
        }
        if (!authorizationController.getAccountDto().isLoginFromSso()
                && authorizationController.getAccountDto().getTimeToChangePassword() != null) {
            if (DateUtil.plusDay(authorizationController.getAccountDto().getTimeToChangePassword(), DbConstant.ACCOUNT_TIME_TO_CHANGE_PASSWORD).getTime() <= (new Date()).getTime() && authorizationController.getAccountDto().getAccountId() != null) {
                FacesUtil.addErrorMessage("Mật khẩu của bạn đã được sử dụng trong " + DbConstant.ACCOUNT_TIME_TO_CHANGE_PASSWORD + " ngày vui lòng đổi mật khẩu để đảm bảo an toàn !");
                FacesUtil.redirect("/admin/doi-mat-khau.xhtml");
                return;
            }
        }
        // Check permission
        if (!authorizationController.hasRole(getMenuId())) {
            // redirect to access denied page
            FacesUtil.redirect("/admin/errors/access.xhtml");
        }

        // load asset assign
        List<Asset> assetList = assetRepository.getAssetByAuctioneerIdAndAssetStatus(
                authorizationController.getAccountDto().getAccountId(),
                Arrays.asList(DbConstant.ASSET_STATUS_WAITING, DbConstant.ASSET_STATUS_PLAYING)
        );
        authorizationController.getAccountDto().setSessionAsset(new HashMap<>());
        for (Asset asset : assetList) {
            authorizationController.getAccountDto().getSessionAsset().put(asset.getAssetId(), asset);
        }
    }
    public ActionSystemController actionSystemController(){ return  actionSystemController;}

    public EFunction getActions() {
        return authorizationController.getActions(getMenuId());
    }

    public AuthFunctionController getAuthFunction() {
        return authorizationController.getAuthFunction();
    };

    public void redirectToSuccessPage() {
        FacesUtil.redirect("/common/success.xhtml");
    }

    public void setErrorGlobal(String error) {
        FacesUtil.addErrorMessage(error);
    }

    public void setErrorForm(String error) {
        FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, error);
    }

    public void setSuccessGlobal(String msg) {
        FacesUtil.addSuccessMessage(msg);
    }

    public void setSuccessForm(String msg) {
        FacesUtil.addSuccessMessage(Constant.ERROR_MESSAGE_ID, msg);
    }

    public void setSuccessForm(String title, String msg) {
        FacesUtil.addSuccessMessage(Constant.ERROR_MESSAGE_ID, title, msg);
        FacesUtil.updateView(Constant.ERROR_GROWL_DETAIL_ID);
    }

    public boolean isPdfFile(String fileName) {
        return FileUtil.isPdfFileExt(fileName);
    }

    public String getTextMessage(String key) {
        return languageController.getTextMap().get(key);
    }

    public String getErrorMessage(String key) {
        return languageController.getErrorMap().get(key);
    }

    public StreamedContent getFileDownload(String filePath) {
        StreamedContent streamedContent = FileUtil.getDownloadFileFromDatabase(filePath);
        if (streamedContent == null) {
            FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, "File không tồn tại");
        }
        return streamedContent;
    }

    public StreamedContent getFileDownload(String filePath, String fileName) {
        StreamedContent streamedContent = FileUtil.getDownloadFileFromDatabase(filePath, fileName);
        if (streamedContent == null) {
            FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, "File không tồn tại");
        }
        return streamedContent;
    }

    public String convertDatetimeToHour(Date date) {
        return DateUtil.formatToPattern(date, "HH");
    }

    public String convertDatetimeToMinute(Date date) {
        return DateUtil.formatToPattern(date, "mm");
    }

    public void redirectToError() {
        FacesUtil.redirect("/admin/errors/error.xhtml");
    }

}
