package vn.compedia.website.auction.controller.frontend.user;


import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.frontend.BaseFEController;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.repository.AccountRepository;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.util.StringUtil;

import javax.inject.Inject;
import javax.inject.Named;

@Named
@Scope(value = "session")
@Getter
@Setter
public class UpdatePersonalInformationController extends BaseFEController {
    @Inject
    protected AuthorizationController authorizationController;
    @Autowired
    protected AccountRepository accountRepository;

    private Account account;
    private String rePassword;
    private Integer maritalStatus = 1;   /* 1 : độc thân , 2 : đã kết hôn*/

    public void resetAll() {
        account = new Account();
        account = accountRepository.findAccountByAccountId(authorizationController.getAccountDto().getAccountId());
        if(account.getRelativeName() != null || account.getRelativeIdCardNumber() != null) {
            maritalStatus = 2;
        }
    }

    public void onSaveData() {
        if (StringUtils.isBlank(account.getFullName().trim())) {
            FacesUtil.addErrorMessage("Họ và tên là trường bắt buộc");
            return;
        }
        if (StringUtils.isBlank(account.getIdCardNumber())) {
            FacesUtil.addErrorMessage("Số CMND/CCCD là trường bắt buộc");
            return;
        }
        if (account.getDateOfBirth() == null) {
            FacesUtil.addErrorMessage("Ngày tháng năm sinh là trường bắt buộc");
            return;
        }
        if (account.getProvinceIdOfIssue() == null) {
            FacesUtil.addErrorMessage("Nơi cấp CMND/CCCD là trường bắt buộc");
            return;
        }
        if (StringUtils.isBlank(account.getAddress())) {
            FacesUtil.addErrorMessage("Địa chỉ là trường bắt buộc");
            return;
        }
        if (account.getDateOfIssue() == null) {
            FacesUtil.addErrorMessage("Bạn vui lòng chọn ngày cấp CMND/CCCD");
            return;
        }
        if (StringUtils.isBlank(account.getPhone())) {
            FacesUtil.addErrorMessage("Số điện thoại là trường bắt buộc");
            return;
        }

        if(maritalStatus.equals(2)) {
            if(StringUtils.isBlank(account.getRelativeName())) {
                FacesUtil.addErrorMessage("Tên vợ/chồng là trường bắt buộc");
            }
            if(StringUtils.isBlank(account.getRelativeIdCardNumber())) {
                FacesUtil.addErrorMessage("Số CMND/CCCD vợ/chồng là trường bắt buộc");
            }
        }

        if (StringUtils.isBlank(account.getEmail())) {
            FacesUtil.addErrorMessage("Email là trường bắt buộc");
            return;
        }
        if (!StringUtil.isValidEmailAddress(account.getEmail())) {
            setErrorForm("Email không đúng định dạng");
            return;
        }

        account.setEmail(account.getEmail().trim());
        if (account.getAccountId() == null) {
            Account oldAccount = accountRepository.findByEmail(account.getEmail());
            if (oldAccount != null) {
                FacesUtil.addErrorMessage("Email đã tồn tại");
                return;
            }
        }

        if (StringUtils.isBlank(account.getPermanentResidence())) {
            FacesUtil.addErrorMessage("Hộ khẩu thường trú là trường bắt buộc");
            return;
        }
//        if (account.getSex() == null) {
//            FacesUtil.addErrorMessage("Giới tính là trường bắt buộc");
//            return;
//        }
        if (!StringUtils.equals(account.getPassword(), rePassword)) {
            FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, "Mật khẩu và xác nhận mật khẩu không khớp nhau");
            return;
        }
        account.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        //   account.setUpdateDate();
        accountRepository.save(account);
        FacesUtil.addSuccessMessage("Lưu thành công");
        FacesUtil.redirect("/frontend/index.xhtml");
    }

    @Override
    protected String getMenuId() {
        return Constant.ID_CAP_NHAT_THONG_TIN_CA_NHAN;
    }

}
