package vn.tech.website.store.controller.admin.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.controller.admin.common.CityDistrictController;
import vn.tech.website.store.controller.frontend.common.FacesNoticeController;
import vn.tech.website.store.dto.common.CityDistrictDto;
import vn.tech.website.store.dto.user.AccountDto;
import vn.tech.website.store.entity.EFunction;
import vn.tech.website.store.entity.EScope;
import vn.tech.website.store.model.Account;
import vn.tech.website.store.model.FunctionRole;
import vn.tech.website.store.model.Province;
import vn.tech.website.store.model.Role;
import vn.tech.website.store.repository.*;
import vn.tech.website.store.util.*;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
@Named
@Scope(value = "session")
@Getter
@Setter
public class AuthorizationController implements Serializable {
    @Inject
    HttpServletRequest request;
//    @Inject
//    private AuthFunctionController authFunction;
    @Inject
    private CityDistrictController cityDistrictController;
    @Inject
    private FacesNoticeController facesNoticeController;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RoleRepository roleRepository;
//    @Autowired
//    private FunctionRepository functionRepository;
//    @Autowired
//    private FunctionRoleRepository functionRoleRepository;
    @Autowired
    private ProvinceRepository provinceRepository;

    private List<String> functions;
    private int role = DbConstant.ROLE_ID_NOT_LOGIN;
    private AccountDto accountDto;
    private Account account;
    private List<SelectItem> listProvinceAccount;
    private List<Province> provinceList;
    private List<Role> permissionList;
    private Date now;
    private String oldPassword;
    private String newPassword;
    private String newRepassword;
    private boolean married;
    // check show captcha
    private boolean showCaptcha;
    private Integer numberWrongShowCaptcha;
    private boolean requiredCaptcha;
    private int numberWrongPassword = 1;
    private Date plusDate;
    //	private static final Logger LOGGER = LoggerFactory.getLogger(AuthSocketFilter.class);
    private FilterConfig config = null;
    private List<SelectItem> apiSexList;
    // message CAS
    private String MESSAGE_ERROR_GROWL;

    @Value("${LOGIN_SECRET_KEY}")
    private String cryptoSecretKey;
    @Value("${cas.server.logout.url}")
    private String casSecurityServerLogout;
    @Value("${cas.client.login.url}")
    private String casSecurityClientLogin;

    @PostConstruct
    public void init() {
        // TODO: We have to check postback at here
//        updateMenuByRole();
        if (hasLogged()) {
            FacesUtil.redirect("/admin/dashboard.xhtml");
            return;
        }

        // for login CAS
        if (MESSAGE_ERROR_GROWL != null) {
            try {
                setErrorForm(MESSAGE_ERROR_GROWL);
            } finally {
                MESSAGE_ERROR_GROWL = null;
            }
        }

        resetAll();
        // For testing
    }

    public void resetAll() {
        role = DbConstant.ROLE_ID_NOT_LOGIN;
        accountDto = new AccountDto();
        married = false;
        accountDto.setRoleId(role);
        //authFunction.setRole(role);
    }

    public void initChangePassword() {
        if (!hasLogged()) {
            FacesUtil.redirect("/admin/login.xhtml");
            return;
        }
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.oldPassword = "";
            this.newRepassword = "";
            this.newRepassword = "";
            //plusDate = DateUtil.plusDay(accountDto.getTimeToChangePassword(), DbConstant.ACCOUNT_TIME_TO_CHANGE_PASSWORD);
        }
    }

    public void initMyAccount() {
        if (!hasLogged()) {
            FacesUtil.redirect("/admin/login.xhtml");
            return;
        }
        if (!FacesContext.getCurrentInstance().isPostback()) {
            resetMyAccount();
        }
    }

    public void resetMyAccount() {
        account = accountRepository.findByAccountId(accountDto.getAccountId());
        BeanUtils.copyProperties(account, accountDto);
       // married = StringUtils.isNotBlank(accountDto.getRelativeIdCardNumber()) && StringUtils.isNotBlank(accountDto.getRelativeName());
        permissionList = roleRepository.findRolesByType(DbConstant.ROLE_TYPE_ADMIN);
        listProvinceAccount = new ArrayList<>();
        now = new Date();
        provinceList = (List<Province>) provinceRepository.findAll();
        for (Province dto : provinceList) {
            listProvinceAccount.add(new SelectItem(dto.getProvinceId(), dto.getName()));
        }
        cityDistrictController.resetAll();
        CityDistrictDto cityDistrictDto = new CityDistrictDto(accountDto.getProvinceId(), accountDto.getDistrictId(), accountDto.getCommuneId());
        cityDistrictController.setCityDistrictDto(cityDistrictDto);
        cityDistrictController.loadData();
    }

    public void onUpload(FileUploadEvent e) {
//        if (e.getFile().getSize() > Constant.MAX_FILE_SIZE) {
//            setErrorForm(Constant.ERROR_MESSAGE_ID, String.format(getErrorMessage(ErrorConstant.KEY_ERROR_FILE_SIZE), Constant.MAX_FILE_SIZE / 1000000));
//            return;
//        }
        accountDto.setUploadedFile(e.getFile());
        //accountDto.setAvatarPath(FileUtil.saveImageFile(e.getFile()));
    }

    private boolean validateField(AccountDto accountDto) {
//        if (StringUtils.isBlank(accountDto.getFullName().trim())) {
//            setErrorForm("Họ và tên là trường bắt buộc");
//            return false;
//        }

        if (accountDto.getDateOfBirth() != null) {
            if (accountDto.getDateOfBirth().getYear() <= -1899) {
                setErrorForm("Ngày sinh sai định dạng");
                return false;
            }
        }

        if (!accountDto.getPhone().matches("^0[1-9]{1}[0-9]{8,9}$|")) {
            setErrorForm("Số điện thoại không đúng định dạng");
            return false;
        }
//        if (!accountDto.isOrg()) {
//            if (StringUtils.isNotBlank(accountDto.getPhone())) {
//                accountDto.setPhone(accountDto.getPhone().trim());
//                Account oldAccount = accountRepository.findByPhone(accountDto.getPhone());
//                if (oldAccount != null && !account.getPhone().equals(accountDto.getPhone())) {
//                    setErrorForm("Số điện thoại đã tồn tại");
//                    return false;
//                }
//                Account checkAccountOrg = accountRepository.findByOrgPhone(accountDto.getPhone());
//                if (checkAccountOrg != null && !account.getPhone().equals(accountDto.getPhone())) {
//                    setErrorForm("Số điện thoại đã tồn tại");
//                    return false;
//                }
//            }
//        }
//        if (accountDto.isOrg()) {
//
//            if (StringUtils.isNotBlank(accountDto.getPhone())) {
//                accountDto.setPhone(accountDto.getPhone().trim());
//                Account oldAccount = accountRepository.findByPhone(accountDto.getPhone());
//                if (oldAccount != null && !account.getPhone().equals(accountDto.getPhone())) {
//                    setErrorForm("Số điện thoại của người đại diện đã tồn tại");
//                    return false;
//                }
//                Account checkAccountOrg = accountRepository.findByOrgPhone(accountDto.getPhone());
//                if (checkAccountOrg != null && !account.getPhone().equals(accountDto.getPhone())) {
//                    setErrorForm("Số điện thoại của người đại diện đã tồn tại");
//                    return false;
//                }
//            }
//
//            if (StringUtils.isNotBlank(accountDto.getOrgPhone())) {
//                accountDto.setOrgPhone(accountDto.getOrgPhone().trim());
//                Account oldAccountOrg = accountRepository.findByPhone(accountDto.getOrgPhone());
//                if (oldAccountOrg != null && !account.getOrgPhone().equals(accountDto.getOrgPhone())) {
//                    setErrorForm("Số điện thoại tổ chức đã tồn tại");
//                    return false;
//                }
//                Account checkOldAccountOrg = accountRepository.findByOrgPhone(accountDto.getOrgPhone());
//                if (checkOldAccountOrg != null && !account.getOrgPhone().equals(accountDto.getOrgPhone())) {
//                    setErrorForm("Số điện thoại tổ chức đã tồn tại");
//                    return false;
//                }
//            }
//        }

        accountDto.setPhone(accountDto.getPhone().trim());
        Account oldPhone = accountDto.getPhone().compareTo("") != 0 ? accountRepository.findByPhone(accountDto.getPhone()) : null;
        if (oldPhone != null && !accountDto.getAccountId().equals(oldPhone.getAccountId())) {
            setErrorForm("Số điện thoại đã tồn tại");
            return false;
        }

        if (!accountDto.getEmail().matches("^\\s*[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})\\s*$")) {
            setErrorForm("Email không đúng định dạng");
            return false;
        }

        if (StringUtils.isBlank(accountDto.getEmail())) {
            setErrorForm("Email là trường bắt buộc");
            return false;
        }

        if (!StringUtil.isValidEmailAddress(accountDto.getEmail())) {
            setErrorForm("Địa chỉ email không đúng định dạng");
            return false;
        }

        accountDto.setEmail(accountDto.getEmail().trim());
        Account oldEmail = accountRepository.findByEmail(accountDto.getEmail());
        if (oldEmail != null && !accountDto.getAccountId().equals(oldEmail.getAccountId())) {
            setErrorForm("Email đã tồn tại");
            FacesUtil.updateView("growl");
            return false;
        }
        return true;
    }

    public void onSaveData() {
        if (!validateField(accountDto)) {
            FacesUtil.updateView(Constant.ERROR_GROWL_ID);
            return;
        }
        accountDto.setProvinceId(cityDistrictController.getCityDistrictDto().getProvinceId());
        accountDto.setDistrictId(cityDistrictController.getCityDistrictDto().getDistrictId());
        accountDto.setCommuneId(cityDistrictController.getCityDistrictDto().getCommuneId());
        Account account = new Account();
        BeanUtils.copyProperties(accountDto, account);
        account.setUpdateBy(accountDto.getAccountId());
        //account.setFirstTimeLogin(true);
        accountRepository.save(account);
        setSuccessForm("Cập nhật thành công");

        // update view
        FacesUtil.updateView("topbar");
        FacesUtil.updateView(Constant.ERROR_GROWL_ID);
        resetMyAccount();
    }

    public void login() {
        String username = accountDto.getUserName();
        String password = accountDto.getPassword();

        createAccountDefault();

        if (StringUtils.isBlank(username.trim())) {
            setErrorForm("Bạn vui lòng nhập tên đăng nhập");
            return;
        }
        if ("".equals(password)) {
            setErrorForm("Bạn vui lòng nhập mật khẩu");
            return;
        }

        //Account account = accountRepository.getAccountByUsernameAndRoleId(username.trim(), DbConstant.ROLE_ID_USER);

        if (account == null) {
            setErrorForm("Tên đăng nhập hoặc mật khẩu không chính xác!");
            return;
        }
        if (account.getRoleId() == 1) {
            setErrorForm("Tên đăng nhập hoặc mật khẩu không chính xác!");
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
//            if (account.getLoginFailed() > 5) {
//                account.setLoginFailed(account.getLoginFailed() + 1);
//                setErrorForm("Tên đăng nhập hoặc mật khẩu không chính xác, bạn còn " + 5 + " lần thử vui lòng thử lại");
//                accountRepository.save(account);
//            } else {
//                setErrorForm(PropertiesUtil.getProperty("notification.locked.account"));
//                account.setAccountStatus(DbConstant.ACCOUNT_LOCK_STATUS);
//                accountRepository.save(account);
//            }
            return;
        }

//        if (DbConstant.ACCOUNT_INACTIVE_STATUS.equals(account.getAccountStatus())) {
//            setErrorForm("Tài khoản chưa được phê duyệt");
//            return;
//        }
//        if (account.getAccountStatus().equals(DbConstant.ACCOUNT_LOCK_STATUS)) {
//            setErrorForm("Tài khoản của bạn đã bị khóa");
//            return;
//        }
//        if (account.getAccountStatus().equals(DbConstant.ACCOUNT_DELETE_STATUS)) {
//            setErrorForm("Tài khoản này đã bị xóa");
//            return;
//        }

        // process login
        if (!processLogin(account)) {
            return;
        }

        // Redirect to home page
        FacesUtil.redirect("/admin/dashboard.xhtml");
    }

    public boolean processLogin(Account account) {

        Role userRole = roleRepository.findRoleByRoleId(account.getRoleId());
        if (userRole == null) {
            setErrorForm("Quyền hạn không đủ để đăng nhập");
            return false;
        }

        // reset
        requiredCaptcha = false;
        numberWrongPassword = 1;

        BeanUtils.copyProperties(account, accountDto);
        accountDto.setRoleName(userRole.getName());
        setRole(account.getRoleId());

        //account.setLoginFailed(0);
        accountRepository.save(account);

        return true;
    }

    public void logout() {
        //boolean loginFromSso = accountDto.isLoginFromSso();

        // destroy current session
        request.getSession().invalidate();

//        if (loginFromSso) {
//            try {
//                FacesUtil.externalRedirect(String.format(
//                        "%s?service=%s",
//                        casSecurityServerLogout,
//                        URLEncoder.encode(casSecurityClientLogin, "UTF-8")
//                ));
//                return;
//            } catch (Exception e) {
//                log.error(e);
//            }
//        }

        // Redirect to home page
        FacesUtil.redirect("/admin/login.xhtml");
    }

//    private void updateAuthFunction() {
//        authFunction.setRole(role);
//    }

    public AccountDto getAccountDto() {
        if (accountDto == null) {
            resetAll();
        }
        return accountDto;
    }

    public void onChangePassword() {
        if (oldPassword.equals("")) {
            setErrorForm("Bạn vui lòng nhập mật khẩu hiện tại");
            return;
        }
        if (newPassword.equals("")) {
            setErrorForm("Bạn vui lòng nhập mật khẩu mới");
            return;
        }
        if (newPassword.length() < DbConstant.ACCOUNT_MAXLENGTH_PASSWORD_ADMIN) {
            setErrorForm("Mật khẩu phải từ 12 ký tự gồm chữ cái, chữ số và ký tự đặc biệt");
            return;
        }

        if (!newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{12,}$")) {
            setErrorForm("Mật khẩu mới không đúng định dạng, phải gồm chữ cái, chữ số và ký tự đặc biệt");
            return;
        }
        if (StringUtils.isBlank(newRepassword.trim())) {
            setErrorForm("Bạn vui lòng nhập lại mật khẩu mới");
            return;
        }
        if (!newPassword.equals(newRepassword)) {
            setErrorForm("Mật khẩu mới và nhập lại mật khẩu không trùng khớp");
            return;
        }
        Account user = accountRepository.findAccountByAccountId(accountDto.getAccountId());
        if (user == null) {
            setErrorForm("Tài khoản của bạn đã không còn tồn tại");
            return;
        }
        if (!user.getPassword().equals(StringUtil.encryptPassword(oldPassword, user.getSalt()))) {
            setErrorForm("Mật khẩu hiện tại không đúng");
            return;
        }
        if (user.getPassword().equals(StringUtil.encryptPassword(newPassword, user.getSalt()))) {
            setErrorForm("Mật khẩu mới phải khác mật khẩu cũ");
            return;
        }
        user.setPassword(StringUtil.encryptPassword(newPassword, user.getSalt()));
        //user.setFirstTimeLogin(true);
        //user.setTimeToChangePassword(new Date());
        accountRepository.save(user);
        FacesUtil.addSuccessMessage(Constant.ERROR_MESSAGE, "Thay đổi mật khẩu thành công, Vui lòng đăng nhập lại");
        resetAll();
        FacesUtil.redirect("/admin/login.xhtml");
    }

    private void createAccountDefault() {
        String defaultAccount = PropertiesUtil.getProperty("default_account");
        String defaultUserName = PropertiesUtil.getProperty("default_username");
        String defaultPassword = PropertiesUtil.getProperty("default_password");
        String defaultRoleCode = PropertiesUtil.getProperty("default_role_code");
        String defaultRoleName = PropertiesUtil.getProperty("default_role_name");
        String defaultRoleDescription = PropertiesUtil.getProperty("default_role_description");
        if (accountRepository.findAccountByUserName(defaultUserName) == null) {
            //setup role default
            Role role = roleRepository.findByCode(defaultRoleCode);
            if (role == null) {
                role = new Role(
                        null,
                        defaultRoleCode,
                        defaultRoleName,
                        defaultRoleDescription,
                        1, 1, false
                );
                roleRepository.save(role);

//                List<FunctionRole> functionRoleList = new ArrayList<>();
                //update function_role
                Role finalRole = role;
//                functionRepository.findAll().forEach(e -> {
//                    functionRoleList.add(new FunctionRole(null, finalRole.getRoleId(), e.getFunctionId()));
//                });
//                functionRoleRepository.saveAll(functionRoleList);
            }
            //
            Account user = new Account();
            user.setSalt(StringUtil.generateSalt());
            user.setPassword(StringUtil.encryptPassword(defaultPassword, user.getSalt()));
            //user.setFullName("Tài Khoản Mặc Định");
            user.setEmail(defaultAccount);
            user.setUserName(defaultUserName);
            user.setGender(1);
            user.setDateOfBirth(new Date());
            //user.setIdCardNumber("111111111111");
            //user.setDateOfIssue(new Date());
            user.setPhone("0999999999");
            user.setStatus(DbConstant.ACCOUNT_ACTIVE_STATUS);
            user.setRoleId(role.getRoleId());
            //user.setFirstTimeLogin(true);
            user.setCreateDate(new Date());
            user.setUpdateDate(new Date());
            accountRepository.save(user);
        }
    }

//    public boolean hasRole(EScope... scopes) {
//        return authFunction.hasScope(scopes);
//    }

//    public EFunction getActions(EScope scope) {
//       return authFunction.scope(scope);
//    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
        //updateAuthFunction();
    }

    public void setAccountDto(AccountDto accountDto) {
        this.accountDto = accountDto;
    }

    public boolean hasLogged() {
        return role != DbConstant.ROLE_ID_NOT_LOGIN;
    }

//    public List<String> getFunctions() {
//        return functions;
//    }

    private void setErrorForm(String str) {
        FacesUtil.addErrorMessage(str);
    }

    private void setSuccessForm(String str) {
        FacesUtil.addSuccessMessage(str);
    }
}
