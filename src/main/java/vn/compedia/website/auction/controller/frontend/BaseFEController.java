package vn.compedia.website.auction.controller.frontend;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import vn.compedia.website.auction.controller.admin.CommonController;
import vn.compedia.website.auction.controller.admin.LanguageController;
import vn.compedia.website.auction.controller.frontend.common.FacesNoticeController;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.Asset;
import vn.compedia.website.auction.repository.AccountRepository;
import vn.compedia.website.auction.repository.AuctionRegisterRepository;
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
public abstract class BaseFEController implements Serializable {
    @Inject
    @Named
    LanguageController languageController;
    @Inject
    AuthorizationFEController authorizationFEController;
    @Inject
    CommonController commonController;
    @Inject
    private FacesNoticeController facesNoticeController;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private AccountRepository accountRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract String getMenuId();

    public void init() {
        if (CollectionUtils.isEmpty(authorizationFEController.getMyMenus())) {
            authorizationFEController.resetAll();
        }
        // Check block account
        if (authorizationFEController.hasLogged()) {
            Account account = accountRepository.getAccountByAccountId(authorizationFEController.getAccountDto().getAccountId());
            if ((account == null || Arrays.asList(DbConstant.ACCOUNT_DELETE_STATUS, DbConstant.ACCOUNT_LOCK_STATUS).contains(account.getAccountStatus()))) {
                authorizationFEController.resetAll();
                FacesUtil.redirect("/frontend/index.xhtml");
                return;
            }

            // load list asset joined
            loadAssetJoined();

            // check first time login
            if (!authorizationFEController.getAccountDto().isFirstTimeLogin() && authorizationFEController.getAccountDto().getAccountId() != null) {
                facesNoticeController.addErrorMessage("Vui lòng đổi mật khẩu trong lần đăng nhập đầu tiên !");
                facesNoticeController.openModal("changePassword");
            }

            // check time to change password
            if (authorizationFEController.getAccountDto().getTimeToChangePassword() != null) {
                if (DateUtil.plusDay(authorizationFEController.getAccountDto().getTimeToChangePassword(), DbConstant.ACCOUNT_TIME_TO_CHANGE_PASSWORD).getTime() <= (new Date()).getTime() && authorizationFEController.getAccountDto().getAccountId() != null) {
                    facesNoticeController.addErrorMessage("Mật khẩu của bạn đã được sử dụng trong " + DbConstant.ACCOUNT_TIME_TO_CHANGE_PASSWORD + " ngày vui lòng đổi mật khẩu để đảm bảo an toàn !");
                    facesNoticeController.openModal("changePassword");
                }
            }
        }

        if (!authorizationFEController.hasRole(getMenuId())) {
            FacesUtil.redirect("/frontend/index.xhtml");
            return;
        }

        // load list link
        commonController.loadLinkList();
    }

    public void loadAssetJoined() {
        List<Asset> assetList = auctionRegisterRepository.getAllAssetByAccountId(authorizationFEController.getAccountDto().getAccountId(),
                DbConstant.AUCTION_REGISTER_STATUS_ACCEPTED,
                Arrays.asList(DbConstant.ASSET_STATUS_WAITING, DbConstant.ASSET_STATUS_PLAYING));
        authorizationFEController.getAccountDto().setSessionAsset(new HashMap<>());
        for (Asset asset : assetList) {
            authorizationFEController.getAccountDto().getSessionAsset().put(asset.getAssetId(), asset);
        }
    }

    public FacesNoticeController facesNotice() {
        return facesNoticeController;
    }

    public void redirectToSuccessPage() {
        FacesUtil.redirect("/common/success.xhtml");
    }

    public void setErrorGlobal(String error) {
        FacesUtil.addErrorMessage(error);
    }

    public void setErrorForm(String error) {
        facesNoticeController.addErrorMessage(error);
    }

    public void closeModal(String modalId) {
        facesNoticeController.closeModal(modalId);
    }

    public void setSuccessGlobal(String error) {
        FacesUtil.addSuccessMessage(error);
    }

    public void setSuccessForm(String error) {
        facesNoticeController.addSuccessMessage(error);
    }

    public void resetGoogleRecaptchaForm() {
        facesNoticeController.resetGoogleRecaptchaRegister();
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

    public StreamedContent getFileDownload(String fileName) {
        StreamedContent streamedContent = FileUtil.getDownloadFileFromDatabase(fileName);
        if (streamedContent == null) {
            FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, "File không tồn tại");
        }
        return streamedContent;
    }

}


