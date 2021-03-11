package vn.tech.website.store.controller.frontend;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.controller.frontend.common.AddressFEController;
import vn.tech.website.store.controller.frontend.common.FacesNoticeController;
import vn.tech.website.store.dto.common.CityDistrictDto;
import vn.tech.website.store.dto.user.AccountDto;
import vn.tech.website.store.jsf.CookieHelper;
import vn.tech.website.store.jsf.GoogleRecaptcha;
import vn.tech.website.store.model.Account;
import vn.tech.website.store.model.Role;
import vn.tech.website.store.repository.AccountRepository;
import vn.tech.website.store.repository.ProvinceRepository;
import vn.tech.website.store.repository.RoleRepository;
import vn.tech.website.store.util.*;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.Serializable;
import java.util.*;

@Log4j2
@Named
@Scope(value = "session")
@Getter
@Setter
public class AuthorizationFEController implements Serializable {
    @Inject
    HttpServletRequest request;
    @Inject
    private FacesNoticeController facesNoticeController;
    @Inject
    private AddressFEController addressFEController;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ProvinceRepository provinceRepository;

    private List<String> myMenus;
    private int role = DbConstant.ROLE_ID_NOT_LOGIN;
    private AccountDto accountDto;
    private Account account;

    // upload image
    private Map<String, String> accuracyCompanyFilePathMap;
    private boolean onChangeAccountType;

    private boolean married;
    private List<SelectItem> listProvinceAccount;
    private Date now;

    private String oldPassword;
    private String newPassword;
    private String newRepassword;
    private String inputCode;
    private String code;
    private boolean resend;
    private boolean rememberLogin;
    private boolean checkActiveStatus;
    private String encodeEmail;
    private String link;

    private boolean showCaptcha;
    private Integer numberWrongShowCaptcha;
    private boolean requiredCaptcha;
    private int numberWrongPassword = 1;
    private Long regulationId;
    private Long assetId;
    private Long assId;
    private boolean check;
    private FilterConfig config = null;
    private Part avatarImagePart;
    private List<SelectItem> apiSexList;

    @Value("${LOGIN_SECRET_KEY}")
    private String cryptoSecretKey;

    @PostConstruct
    public void init() {
        // TODO: We have to check postback at herea
        updateMenuByRole();
        resetAll();
        // For testing
    }

    public void initLoginModal() {
        resetAll();
    }

    public void initCompanyAccount(){
        if (!hasLogged()) {
            FacesUtil.redirect("/frontend/index.xhtml");
            return;
        }
//        if (accountDto.isOrg()){
//            if (!FacesContext.getCurrentInstance().isPostback()) {
//                resetMyAccount();
//            }
//        } else {
            FacesUtil.redirect("/frontend/account/personal_account.xhtml");
//        }
    }

    public void initPersonalAccount() {
        if (!hasLogged()) {
            FacesUtil.redirect("/frontend/index.xhtml");
            return;
        }
//        if (!accountDto.isOrg()) {
//            if (!FacesContext.getCurrentInstance().isPostback()) {
//                resetMyAccount();
//            }
//        } else {
            FacesUtil.redirect("/frontend/account/company_account.xhtml");
//        }
    }

    public void resetMyAccount() {
        account = accountRepository.findByAccountId(accountDto.getAccountId());
        BeanUtils.copyProperties(account, accountDto);
        married = false;
//        if (StringUtils.isNotBlank(accountDto.getRelativeIdCardNumber()) && StringUtils.isNotBlank(accountDto.getRelativeName())) {
//            married = true;
//        }
        addressFEController.resetAll();
        CityDistrictDto cityDistrictDto = new CityDistrictDto(accountDto.getProvinceId(), accountDto.getDistrictId(), accountDto.getCommuneId());
        addressFEController.setCityDistrictDto(cityDistrictDto);
        addressFEController.loadData();
        listProvinceAccount = new ArrayList<>();
    }

//    private boolean validateField(AccountDto accountDto, Map<String, String> accuracyCompanyFilePathMap) {
//        // org
//        if (accountDto.isOrg()) {
//            if (StringUtils.isBlank(accountDto.getOrgName())) {
//                facesNoticeController.addErrorMessage("Tên tổ chức là trường bắt buộc");
//                return false;
//            }
//            if (StringUtils.isBlank(accountDto.getBusinessLicense())) {
//                facesNoticeController.addErrorMessage("Số đăng ký giấy phép kinh doanh là trường bắt buộc");
//                return false;
//            }
//            if (StringUtils.isBlank(accountDto.getPosition())) {
//                facesNoticeController.addErrorMessage("Chức vụ là trường bắt buộc");
//                return false;
//            }
//            if (StringUtils.isBlank(accountDto.getOrgPhone())) {
//                facesNoticeController.addErrorMessage("Số điện thoại cơ quan là trường bắt buộc");
//                return false;
//            }
//            if (accountDto.getPhone().trim().equals(accountDto.getOrgPhone().trim())) {
//                facesNoticeController.addErrorMessage("Số điện thoại của người đại diện không được trùng số điện thoại tổ chức");
//                FacesUtil.updateView(Constant.ERROR_FE_GROWL_ID);
//                return false;
//            }
//            Account checkAccountOrg = accountRepository.findByOrgPhone(accountDto.getPhone());
//            if (checkAccountOrg != null && !checkAccountOrg.getPhone().equals(accountDto.getPhone())) {
//                facesNoticeController.addErrorMessage("Số điện thoại của người đại diện đã tồn tại");
//                FacesUtil.updateView(Constant.ERROR_FE_GROWL_ID);
//                return false;
//            }
//            Account checkOldAccountOrg = accountRepository.findByOrgPhone(accountDto.getOrgPhone());
//            if (checkOldAccountOrg != null && !checkOldAccountOrg.getOrgPhone().equals(accountDto.getOrgPhone())) {
//                facesNoticeController.addErrorMessage("Số điện thoại tổ chức đã tồn tại");
//                FacesUtil.updateView(Constant.ERROR_FE_GROWL_ID);
//                return false;
//            }
//            if (accuracyCompanyFilePathMap.isEmpty()) {
//                facesNoticeController.addErrorMessage("Ảnh giấy phép ĐKKD là trường bắt buộc");
//                FacesUtil.updateView(Constant.ERROR_FE_GROWL_ID);
//                return false;
//            }
//        }
//
//        // person
//        if (!accountDto.isOrg()) {
//            if (accountDto.getDateOfBirth() == null) {
//                facesNoticeController.addErrorMessage("Ngày tháng năm sinh là trường bắt buộc");
//                return false;
//            }
//            if(accountDto.getDateOfBirth() != null){
//                Date timeDate = DateUtil.formatFromDate(accountDto.getDateOfBirth());
//                Date timeNows = DateUtil.formatFromDate(now);
//                if (timeDate.compareTo(timeNows) >= 0) {
//                    facesNoticeController.addErrorMessage("Ngày sinh phải nhỏ hơn ngày hiện tại");
//                    return false;
//                }
//            }
//
//            if (married) {
//                if (StringUtils.isBlank(accountDto.getRelativeName())) {
//                    facesNoticeController.addErrorMessage("Họ và tên vợ/chồng là trường bắt buộc");
//                    return false;
//                }
//                if (StringUtils.isBlank(accountDto.getRelativeIdCardNumber())) {
//                    facesNoticeController.addErrorMessage("Số CMND/CCCD của vợ/chồng là trường bắt buộc");
//                    return false;
//                }
//                if (!accountDto.getRelativeIdCardNumber().matches("^[0-9]{9}(?:[0-9]{3})?$|")) {
//                    facesNoticeController.addErrorMessage("Số CMND/CCCD của vợ/chồng không đúng định dạng");
//                    return false;
//                }
//                if (accountDto.getIdCardNumber().compareToIgnoreCase(accountDto.getRelativeIdCardNumber()) == 0) {
//                    facesNoticeController.addErrorMessage("Số CMND/CCCD của vợ/chồng không được trùng nhau");
//                    return false;
//                }
//                accountDto.setRelativeIdCardNumber(accountDto.getRelativeIdCardNumber().trim());
//                Account oldAccount1 = accountRepository.findByRelativeIdCardNumber(accountDto.getRelativeIdCardNumber());
//                if (oldAccount1 != null && !accountDto.getRelativeIdCardNumber().equalsIgnoreCase(oldAccount1.getRelativeIdCardNumber().trim())) {
//                    facesNoticeController.addErrorMessage("Số CMND/CCCD vợ/chồng đã tồn tại");
//                    FacesUtil.updateView(Constant.ERROR_FE_GROWL_ID);
//                    return false;
//                }
//            }
//
//            if (accountDto.getDateOfBirth().getYear() <= -1899) {
//                facesNoticeController.addErrorMessage("Ngày sinh sai định dạng");
//                return false;
//            }
//
//            if(accountDto.getDateOfIssue() != null && accountDto.getDateOfBirth() != null) {
//                Date timeDate = DateUtil.formatFromDate(accountDto.getDateOfIssue());
//                Date timeBirth = DateUtil.formatFromDate(accountDto.getDateOfBirth());
//                if (timeDate.compareTo(timeBirth) < 0) {
//                    facesNoticeController.addErrorMessage("Ngày cấp phải lớn hơn ngày sinh");
//                    return false;
//                }
//            }
//        }
//
//        if (StringUtils.isBlank(accountDto.getUsername().trim())) {
//            facesNoticeController.addErrorMessage("Tên đăng nhập là trường bắt buộc");
//            return false;
//        }
//        if (accountDto.getUsername().length() < DbConstant.ACCOUNT_MINLENGTH_USERNAME) {
//            facesNoticeController.addErrorMessage("Tên đăng nhập phải có tối thiểu 6 ký tự");
//            return false;
//        }
//        accountDto.setUsername(accountDto.getUsername().trim());
//        Account oldUserName = accountRepository.findAccountByUsernameAndRoleId(accountDto.getUsername(),DbConstant.ROLE_ID_USER);
//        if (oldUserName != null && !accountDto.getAccountId().equals(oldUserName.getAccountId())) {
//            facesNoticeController.addErrorMessage("Tên đăng nhập đã tồn tại");
//            return false;
//        }
//
//        if (StringUtils.isBlank(accountDto.getFullName().trim())) {
//            facesNoticeController.addErrorMessage("Họ và tên là trường bắt buộc");
//            return false;
//        }
//
//        accountDto.setPhone(accountDto.getPhone().trim());
//        Account oldAccountt = accountRepository.findByPhone(accountDto.getPhone());
//        if (oldAccountt != null && !accountDto.getPhone().equals(oldAccountt.getPhone())) {
//            facesNoticeController.addErrorMessage("Số điện thoại đã tồn tại");
//            FacesUtil.updateView(Constant.ERROR_FE_GROWL_ID);
//            return false;
//        }
//        Account checkAccountOrgg = accountRepository.findByOrgPhone(accountDto.getPhone());
//        if (checkAccountOrgg != null && !accountDto.getPhone().equals(checkAccountOrgg.getPhone())) {
//            facesNoticeController.addErrorMessage("Số điện thoại đã tồn tại");
//            FacesUtil.updateView(Constant.ERROR_FE_GROWL_ID);
//            return false;
//        }
//
//        if (accountDto.getProvinceIdOfIssue() == null) {
//            facesNoticeController.addErrorMessage("Nơi cấp CMND/CCCD là trường bắt buộc");
//            return false;
//        }
//
//        if (accountDto.getDateOfIssue() == null) {
//            facesNoticeController.addErrorMessage("Bạn vui lòng chọn ngày cấp CMND/CCCD");
//            return false;
//        }
//
//        if (StringUtils.isBlank(accountDto.getPhone())) {
//            facesNoticeController.addErrorMessage("Số điện thoại là trường bắt buộc");
//            return false;
//        }
//
//        if (!accountDto.getPhone().matches("[0]{1}[1-9]{1}[0-9]{8,9}")) {
//            facesNoticeController.addErrorMessage("Số điện thoại không đúng định dạng");
//            return false;
//        }
//
//        if (StringUtils.isBlank(accountDto.getIdCardNumber().trim())) {
//            facesNoticeController.addErrorMessage("Số CMND/CCCD là trường bắt buộc");
//            return false;
//        }
//        if (!accountDto.getIdCardNumber().matches("^[0-9]{9}(?:[0-9]{3})?$")) {
//            facesNoticeController.addErrorMessage("Số CMND/CCCD không đúng định dạng");
//            return false;
//        }
//        accountDto.setIdCardNumber(accountDto.getIdCardNumber().trim());
//        Account oldAccount2 = accountRepository.findByIdCardNumber(accountDto.getIdCardNumber());
//        if (oldAccount2 != null && !accountDto.getAccountId().equals(oldAccount2.getAccountId())) {
//            facesNoticeController.addErrorMessage("Số CMND/CCCD đã tồn tại");
//            FacesUtil.updateView(Constant.ERROR_FE_GROWL_ID);
//            return false;
//        }
//
//        if (accountDto.getDateOfIssue().getYear() <= -1899) {
//            facesNoticeController.addErrorMessage("Ngày cấp sai định dạng");
//            return false;
//        }
//
//        if (StringUtils.isBlank(accountDto.getEmail())) {
//            facesNoticeController.addErrorMessage("Địa chỉ email là trường bắt buộc");
//            return false;
//        }
//        if (!accountDto.getEmail().matches("^\\s*[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})\\s*$")) {
//            facesNoticeController.addErrorMessage("Email không đúng định dạng");
//            return false;
//        }
//        accountDto.setEmail(accountDto.getEmail().trim());
//        Account oldEmail = accountRepository.findByEmail(accountDto.getEmail());
//        if (oldEmail != null && !accountDto.getAccountId().equals(oldEmail.getAccountId())) {
//            facesNoticeController.addErrorMessage("Email đã tồn tại");
//            return false;
//        }
//
//        if (StringUtils.isBlank(accountDto.getPermanentResidence())) {
//            facesNoticeController.addErrorMessage("Hộ khẩu thường trú là trường bắt buộc");
//            return false;
//        }
//
//        if (addressFEController.getCityDistrictDto().getProvinceId() == null) {
//            facesNoticeController.addErrorMessage("Tỉnh/thành phố là trường bắt buộc");
//            return false;
//        }
//        if (addressFEController.getCityDistrictDto().getDistrictId() == null) {
//            facesNoticeController.addErrorMessage("Quận/huyện là trường bắt buộc");
//            return false;
//        }
//        if (addressFEController.getCityDistrictDto().getCommuneId() == null) {
//            facesNoticeController.addErrorMessage("Phường/xã là trường bắt buộc");
//            return false;
//        }
//
//        return true;
//    }
//
//    private boolean validateUploadFile(FileUploadEvent e) {
//        if (!FileUtil.isAcceptFileType(e.getFile())) {
//            facesNoticeController.addErrorMessage("Loại file không được phép. Những file được phép: " + FileUtil.getAcceptFileImageString().replaceAll(",", ", ").toUpperCase());
//            FacesUtil.updateView(Constant.ERROR_FE_GROWL_ID);
//            return false;
//        }
//        if (e.getFile().getSize() > Constant.MAX_FILE_SIZE) {
//            facesNoticeController.addErrorMessage("Dung lượng file quá lớn. Dung lượng tối đa " + Constant.MAX_FILE_SIZE / 1000000 + "Mb");
//            FacesUtil.updateView(Constant.ERROR_FE_GROWL_ID);
//            return false;
//        }
//        return true;
//    }

    public void onUploadAvatar(FileUploadEvent e) {
        try {
//            if (!validateUploadFile(e)) {
//                return;
//            }
            accountDto.setImagePath(null);
            //accountDto.setAvatarPath(FileUtil.saveImageFile(e.getFile()));
        } catch (Exception ex) {
            log.error("Error", ex);
        }
    }

    public void onUploadCardIdFront(FileUploadEvent e) {
        try {
//            if (!validateUploadFile(e)) {
//                return;
//            }
        } catch (Exception ex) {
            log.error("Error", ex);
        }
    }

    public void onUploadCardIdBack(FileUploadEvent e) {
        try {
//            if (!validateUploadFile(e)) {
//                return;
//            }
        } catch (Exception ex) {
            log.error("Error", ex);
        }
    }

    public void onUploadMultipleFile(FileUploadEvent e) {
        try {
//            if (!validateUploadFile(e)) {
//                return;
//            }
            if (accuracyCompanyFilePathMap.containsKey(e.getFile().getFileName())) {
                facesNoticeController.addErrorMessage("Ảnh đã tồn tại, vui lòng chọn ảnh khác");
                FacesUtil.updateView(Constant.ERROR_FE_GROWL_ID);
                return;
            }
            if (accuracyCompanyFilePathMap.size() >= 4) {
                facesNoticeController.addErrorMessage("Bạn chỉ được tải lên tối đa 4 ảnh giấy phép ĐKKD");
                FacesUtil.updateView(Constant.ERROR_FE_GROWL_ID);
                return;
            }
            String fileName = e.getFile().getFileName();
            String filePath = FileUtil.saveFile(e.getFile());
            accuracyCompanyFilePathMap.put(fileName, filePath);
        } catch (Exception ex) {
            log.error("Error", ex);
        }
    }

    public void onRemoveFile(String key) {
        accuracyCompanyFilePathMap.remove(key);
    }

    public void onSaveData() {
//        if (!validateField(accountDto, accuracyCompanyFilePathMap)) {
//            resetMyAccount();
//            return;
//        }
        accountDto.setProvinceId(addressFEController.getCityDistrictDto().getProvinceId());
        accountDto.setDistrictId(addressFEController.getCityDistrictDto().getDistrictId());
        accountDto.setCommuneId(addressFEController.getCityDistrictDto().getCommuneId());
        Account account = new Account();
        BeanUtils.copyProperties(accountDto, account);
        //account.setFinishInfoStatus(true);
        account.setUpdateBy(accountDto.getAccountId());
//        if (!married) {
//            account.setRelativeName(null);
//            account.setRelativeIdCardNumber(null);
//        }
        accountRepository.save(account);

        facesNoticeController.addSuccessMessage("Cập nhật thành công");

        if (assetId != null) {
            FacesUtil.redirect("/frontend/asset/asset_detail.xhtml?id=" + assetId);
            facesNoticeController.openModal("storeRegisterPopup");
            assetId = null;
        }
        if (regulationId != null) {
            FacesUtil.redirect("/frontend/regulation/regulation_detail.xhtml?id=" + regulationId + "&assId=" + assId);
            check = false;
            regulationId = null;
            assId = null;
        }

        resetMyAccount();
    }

    public void initChangePassword() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.oldPassword = "";
            this.newRepassword = "";
            this.newRepassword = "";
        }
    }

    public boolean hasRole(String menuId) {
        return myMenus.indexOf(menuId) > -1;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
        updateMenuByRole();
    }

    public void login() {
        String username = accountDto.getUserName();
        String password = accountDto.getPassword();

        facesNoticeController.resetGoogleRecaptcha("login-form");

        checkActiveStatus = false;
        if (StringUtils.isBlank(username.trim())) {
            facesNoticeController.addErrorMessage("Bạn vui lòng nhập tên đăng nhập");
            return;
        }
        if ("".equals(password)) {
            facesNoticeController.addErrorMessage("Bạn vui lòng nhập mật khẩu");
            return;
        }

        if (showCaptcha && requiredCaptcha && !GoogleRecaptcha.verify()) {
            facesNoticeController.addErrorMessage("Bạn vui lòng nhập mã xác nhận");
            return;
        }

        Account account = accountRepository.findAccountByUserNameAndRoleId(username.trim(),DbConstant.ROLE_ID_USER);
        if (account == null) {
            facesNoticeController.addErrorMessage("Tên đăng nhập hoặc mật khẩu không chính xác");
            return;
        }
        if (account.getRoleId() != DbConstant.ROLE_ID_USER) {
            facesNoticeController.addErrorMessage("Tên đăng nhập hoặc mật khẩu không chính xác");
            return;
        }

        if (!StringUtil.encryptPassword(password, account.getSalt()).equals(account.getPassword())) {
            // Check to show the captcha
            if (showCaptcha) {
                int numToShowCaptcha = numberWrongShowCaptcha == null ? Constant.NUMBER_WRONG_PASSWORD_TO_SHOW_CAPTCHA : numberWrongShowCaptcha;
                if (numberWrongPassword++ >= numToShowCaptcha && !requiredCaptcha) {
                    requiredCaptcha = true;
                }
            }
//            if (account.getLoginFailed() < 5) {
//                account.setLoginFailed(account.getLoginFailed() + 1);
//                facesNoticeController.addErrorMessage("Tên đăng nhập hoặc mật khẩu không chính xác, bạn còn " + 5 + " lần thử vui lòng thử lại ");
//                accountRepository.save(account);
//            } else {
//                facesNoticeController.addErrorMessage(PropertiesUtil.getProperty("notification.locked.account"));
//                account.setAccountStatus(DbConstant.ACCOUNT_LOCK_STATUS);
//                accountRepository.save(account);
//            }
            return;
        }

        if (account.getStatus().equals(DbConstant.ACCOUNT_INACTIVE_STATUS)) {
            String[] codeEmail = account.getEmail().split("@");
            StringBuilder inEmail = new StringBuilder();
            for (int i = 2; i < codeEmail[0].length() - 1; i++) {
                inEmail.append("*");
            }
            encodeEmail = account.getEmail().substring(0, 2) + inEmail.toString() + codeEmail[0].charAt(codeEmail[0].length() - 1) + codeEmail[1];
            facesNoticeController.addErrorMessage("Tài khoản chưa được kích hoạt");
            checkActiveStatus = true;
            return;
        }

        if (account.getStatus().equals(DbConstant.ACCOUNT_LOCK_STATUS)) {
            facesNoticeController.addErrorMessage("Tài khoản đang bị khóa");
            return;
        }
        BeanUtils.copyProperties(account, accountDto);
//        if (!account.isFirstTimeLogin()) {
//            facesNoticeController.addErrorMessage("Vui lòng đổi mật khẩu trong lần đăng nhập đầu tiên!");
//            facesNoticeController.openModal("changePassword");
//            return;
//        }

        // reset
        requiredCaptcha = false;
        numberWrongPassword = 1;

        //PROCESS LOGIN
        processLogin(account);

        if (rememberLogin) {
            //save to cookie
            CookieHelper.setCookie(Constant.COOKIE_ACCOUNT, account.getUserName());
            CookieHelper.setCookie(Constant.COOKIE_PASSWORD, StringUtil.encryptPassword(account.getPassword(), account.getSalt() + account.getCreateDate()));
        }

        facesNoticeController.closeModal("loginPopup");
        facesNoticeController.addSuccessMessage("Đăng nhập thành công!");

        // Redirect to home page
        facesNoticeController.reload();
    }

    public void processLogin(Account account) {

        AccountDto accountDtoAddress = addressFEController.getAddressForAccount(account.getAccountId());

        if (accountDtoAddress != null) {
            accountDto.setProvinceName(accountDtoAddress.getProvinceName());
            accountDto.setDistrictName(accountDtoAddress.getDistrictName());
            accountDto.setCommuneName(accountDtoAddress.getCommuneName());
        }

        BeanUtils.copyProperties(account, accountDto);
//        Role userRole = roleRepository.findRoleByRoleId(accountDto.getRoleId());
//        accountDto.setRoleName(userRole.getName());

        this.role = account.getRoleId();
        updateMenuByRole();

        //account.setLoginFailed(0);
        accountRepository.save(account);
    }

    public void logout() {
        resetAll();

        // destroy current session
        request.getSession().invalidate();

        //remove cookie
        CookieHelper.removeCookie(Constant.COOKIE_ACCOUNT);
        CookieHelper.removeCookie(Constant.COOKIE_PASSWORD);

        // Redirect to home page
        FacesUtil.redirect("/frontend/index.xhtml");
    }

    public List<String> getMyMenus() {
        return myMenus;
    }

    public void setMyMenus(List<String> myMenus) {
        this.myMenus = myMenus;
    }

    private void updateMenuByRole() {
        myMenus = new ArrayList<String>();
        myMenus.add(Constant.ID_CAU_HINH_TIN_TUC);
        // Menu trang chu
        switch (role) {
            case DbConstant.ROLE_ID_NOT_LOGIN:
                myMenus.add(Constant.ID_INDEX_FONTEND);
                myMenus.add(Constant.ID_ASSET_FRONTEND);
                myMenus.add(Constant.ID_QUAN_LY_TAI_KHOAN_NGUOI_DUNG);
                myMenus.add(Constant.ID_REGULATION_FRONTEND);
                myMenus.add(Constant.ID_REGULATION_DETAIL_FRONTEND);
                myMenus.add(Constant.ID_ASSET_DETAIL_FRONTEND);
                myMenus.add(Constant.ID_PAYMENT_WALLET_RETURN_URL);
                break;
            case DbConstant.ROLE_ID_USER:
                myMenus.add(Constant.ID_INDEX_FONTEND);
                myMenus.add(Constant.ID_PROPERTY_SALE_HISTORY);
                myMenus.add(Constant.ID_ASSET_FRONTEND);
                myMenus.add(Constant.ID_REGULATION_FRONTEND);
                myMenus.add(Constant.ID_QUAN_LY_TAI_KHOAN_NGUOI_DUNG);
                myMenus.add(Constant.ID_REGULATION_DETAIL_FRONTEND);
                myMenus.add(Constant.ID_MY_AUCTION);
                myMenus.add(Constant.ID_MY_PAYMENT);
                myMenus.add(Constant.ID_ASSET_DETAIL_FRONTEND);
                myMenus.add(Constant.ID_PAYMENT_WALLET_RETURN_URL);
                break;
            default:
                break;
        }
    }

    public void resetAll() {
        code = StringUtil.generateSalt();
        checkActiveStatus = false;
        role = DbConstant.ROLE_ID_NOT_LOGIN;
        rememberLogin = false;
        accountDto = new AccountDto();
        myMenus = new ArrayList<>();
        account = new Account();
        accountDto.setRoleId((role));
        accuracyCompanyFilePathMap = new LinkedHashMap<>();
        updateMenuByRole();
    }

    public AccountDto getAccountDto() {
        if (accountDto == null) {
            resetAll();
        }
        return accountDto;
    }

    public void onChangePassword() {
        if (oldPassword.equals("")) {
            facesNoticeController.addErrorMessage("Bạn vui lòng nhập mật khẩu cũ");
            return;
        }
        if (newPassword.equals("")) {
            facesNoticeController.addErrorMessage("Bạn vui lòng nhập mật khẩu mới");
            return;
        }
        if (oldPassword.equals(newPassword)) {
            facesNoticeController.addErrorMessage("Mật khẩu mới không được giống mật khẩu hiện tại");
            return;
        }

        if (newPassword.length() < DbConstant.ACCOUNT_MINLENGTH_PASSWORD_USER) {
            facesNoticeController.addErrorMessage("Mật khẩu phải có tối thiểu 12 ký tự, mật khẩu phải bao gồm chữ cái, chữ số và ký tự đặc biệt.");
            return;
        }

        if (!newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{12,}$")) {
            facesNoticeController.addErrorMessage("Mật khẩu phải có tối thiểu 12 ký tự, mật khẩu phải bao gồm chữ cái, chữ số và ký tự đặc biệt.");
            return;
        }
        if ("".equals(newRepassword)) {
            facesNoticeController.addErrorMessage("Bạn vui lòng nhập nhắc lại mật khẩu");
            return;
        }
        if (!newPassword.equals(newRepassword)) {
            facesNoticeController.addErrorMessage("Mật khẩu mới và nhắc lại mật khẩu không giống nhau");
            return;
        }

        Account user = accountRepository.findAccountByAccountId(accountDto.getAccountId());
        if (user == null) {
            facesNoticeController.addErrorMessage("Tài khoản của bạn đã không còn tồn tại");
            return;
        }
        if (!user.getPassword().equals(StringUtil.encryptPassword(oldPassword, user.getSalt()))) {
            facesNoticeController.addErrorMessage("Mật khẩu hiện tại không đúng");
            return;
        }
        if (user.getPassword().equals(StringUtil.encryptPassword(newPassword))) {
            facesNoticeController.addErrorMessage("Mật khẩu mới phải khác mật khẩu cũ");
            return;
        }
        user.setPassword(StringUtil.encryptPassword(newPassword, user.getSalt()));
//        user.setFirstTimeLogin(true);
//        user.setTimeToChangePassword(new Date());
        accountRepository.save(user);
        facesNoticeController.addSuccessMessage("Thay đổi mật khẩu thành công");
        resetAll();
        FacesUtil.redirect("/frontend/index.xhtml");
        facesNoticeController.openModal("loginPopup");
    }

    public void onCheckEmail() {
            if (StringUtils.isBlank(accountDto.getEmail().trim())) {
                facesNoticeController.addErrorMessage("Bạn vui lòng nhập email đã đăng ký");
                return;
            }
            if (!accountDto.getEmail().trim().matches("^\\s*[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})\\s*$")) {
                facesNoticeController.addErrorMessage("Email không đúng định dạng");
                return;
            }
            account = accountRepository.findAccountByEmail(accountDto.getEmail().trim());
            if (account == null || account.getRoleId() != DbConstant.ROLE_ID_USER) {
                facesNoticeController.addErrorMessage("Email không tồn tại");
                return;
            }
            if (account.getStatus().equals(DbConstant.ACCOUNT_LOCK_STATUS)) {
                facesNoticeController.addErrorMessage("Tài khoản của bạn đã bị khóa vui lòng mở khóa đề đổi mật khẩu");
                return;
            }
        code = StringUtil.generateSalt();
        EmailUtil.getInstance().sendLostPasswordEmail(account.getEmail(), code, account.getFullName());
        facesNoticeController.addSuccessMessage("Mã xác nhận đã được gửi về tài khoản email của bạn");
        facesNoticeController.activeFunction("countdownCheckMail");
    }

    public void onCheckCode() {
        if (StringUtils.isBlank(inputCode)) {
            facesNoticeController.addErrorMessage("Bạn vui lòng nhập mã xác nhận");
            facesNoticeController.resetSubmitButton();
            return;
        }
        if (!code.equals(inputCode.trim())) {
            facesNoticeController.addErrorMessage("Mã xác nhận không chính xác, vui lòng nhập lại mã xác nhận");
            facesNoticeController.resetSubmitButton();
            return;
        }
        facesNoticeController.closeModal("lostPassPopup");
        facesNoticeController.openModal("CreateNewPassword");
    }

    public void removeCode() {
        code = "";
        facesNoticeController.activeFunction("onTakeNewCode");
    }

    public void onCreateNewPassword() {
        if (newPassword.equals("")) {
            facesNoticeController.addErrorMessage("Bạn vui lòng nhập mật khẩu mới");
            return;
        }

        if (newRepassword.equals("")) {
            facesNoticeController.addErrorMessage("Bạn vui lòng nhập lại mật khẩu mới");
            return;
        }
        if (newPassword.length() < DbConstant.ACCOUNT_MINLENGTH_PASSWORD_USER) {
            facesNoticeController.addErrorMessage("Mật khẩu phải có tối thiểu 12 ký tự");
            return;
        }
        if (!newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{12,}$")) {
            facesNoticeController.addErrorMessage("Mật khẩu không đúng định dạng");
            return;
        }
        if (!newPassword.equals(newRepassword)) {
            facesNoticeController.addErrorMessage("Mật khẩu mới và nhắc lại mật khẩu không giống nhau");
            return;
        }
        account.setPassword(StringUtil.encryptPassword(newPassword, account.getSalt()));
//        account.setFirstTimeLogin(true);
//        account.setTimeToChangePassword(new Date());
        accountRepository.save(account);
        facesNoticeController.addSuccessMessage("Lấy lại mật khẩu thành công");
        facesNoticeController.closeModal("CreateNewPassword");
        facesNoticeController.openModal("loginPopup");
    }

    public void sendActiveEmail() {
//        if (StringUtils.isBlank(accountDto.getUsername())) {
//            facesNoticeController.addErrorMessage("Bạn vui lòng nhập tài khoản");
//            return;
//        }
//        if (StringUtils.isBlank(accountDto.getPassword())) {
//            facesNoticeController.addErrorMessage("Bạn vui lòng nhập mật khẩu");
//            return;
//        }

        Account account = accountRepository.findAccountByUserName("abc");
//        if (account == null) {
//            facesNoticeController.addErrorMessage("Tài khoản hoặc mật khẩu không chính xác");
//            return;
//        }
//        if (account.getRoleId() != DbConstant.ROLE_ID_USER) {
//            facesNoticeController.addErrorMessage("Tài khoản hoặc mật khẩu không chính xác");
//            return;
//        }
        accountRepository.save(account);
        link = buildHttpURI() + "/frontend/account/activation.xhtml?token=" + "abcdefgh";
        EmailUtil.getInstance().sendCreateUserEmail(account.getEmail(), link , account.getUserName());
        facesNoticeController.addSuccessMessage("Email đã được gửi về tài khoản: " + encodeEmail + " vui lòng kiểm tra email để kích hoạt tài khoản");
        checkActiveStatus = false;
    }

    private String buildHttpURI() {
        String httpUrl = String.format("%s://%s:%s%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                request.getContextPath()
        );
        return httpUrl;
    }

    public void setAccountDto(AccountDto accountDto) {
        this.accountDto = accountDto;
    }

    public String upperCaseFirstChar(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public boolean hasLogged() {
        return role != DbConstant.ROLE_ID_NOT_LOGIN;
    }
}
