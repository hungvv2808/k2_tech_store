package vn.tech.website.store.controller.admin.user;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.controller.admin.BaseController;
import vn.tech.website.store.controller.admin.auth.AuthorizationController;
import vn.tech.website.store.controller.admin.common.CityDistrictController;
import vn.tech.website.store.dto.common.CityDistrictDto;
import vn.tech.website.store.dto.user.AccountDto;
import vn.tech.website.store.dto.user.AdministratorsCentralStaffSearchDto;
import vn.tech.website.store.entity.EScope;
import vn.tech.website.store.model.Account;
import vn.tech.website.store.model.Province;
import vn.tech.website.store.model.Role;
import vn.tech.website.store.repository.AccountRepository;
import vn.tech.website.store.repository.AdministratorsCentralStaffRepository;
import vn.tech.website.store.repository.ProvinceRepository;
import vn.tech.website.store.repository.RoleRepository;
import vn.tech.website.store.util.DbConstant;
import vn.tech.website.store.util.EmailUtil;
import vn.tech.website.store.util.FacesUtil;
import vn.tech.website.store.util.StringUtil;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Named
@Scope("session")
@Getter
@Setter
public class AdministratorsCentralStaffController extends BaseController {

    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private CityDistrictController cityDistrictController;

    @Autowired
    protected AdministratorsCentralStaffRepository administratorsCentralStaffRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    private ProvinceRepository provinceRepository;

    private AdministratorsCentralStaffSearchDto adminSearchDto;
    private AccountDto accountDto;
    private AccountDto objBackup;
    private List<SelectItem> listProvinceAccount;
    private List<Province> provinceList;
    private List<Role> permissionList;
    private LazyDataModel<AccountDto> lazyDataModel;
    private Date now;
    private Account account;

    private final String ROLE_NAME_ADMIN = "quản trị";

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        now = new Date();
        adminSearchDto = new AdministratorsCentralStaffSearchDto();
        accountDto = new AccountDto();
        permissionList = roleRepository.findRolesByTypeAndStatus(DbConstant.ROLE_TYPE_ADMIN, DbConstant.ROLE_STATUS_ACTIVE);
        listProvinceAccount = new ArrayList<>();
        provinceList = (List<Province>) provinceRepository.findAll();
        for (Province dto : provinceList) {
            listProvinceAccount.add(new SelectItem(dto.getProvinceId(), dto.getName()));
        }

        adminSearchDto.setStatus(-1);
        accountDto.setSex(-1);
        cityDistrictController.resetAll();
        onSearch();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    private boolean validateField(AccountDto accountDto) {
        if (StringUtils.isBlank(accountDto.getFullName().trim())) {
            setErrorForm("Họ và tên là trường bắt buộc");
            FacesUtil.updateView("growl");
            return false;
        }

        if (!StringUtils.isBlank(accountDto.getPhone().trim())) {
            accountDto.setPhone(accountDto.getPhone().trim());
            if (accountDto.getAccountId() == null || !objBackup.getPhone().equalsIgnoreCase(accountDto.getPhone().trim())) {
                Account oldAccount = accountRepository.findByPhone(accountDto.getPhone());
                if (oldAccount != null) {
                    FacesUtil.addErrorMessage("Số điện thoại đã tồn tại");
                    FacesUtil.updateView("growl");
                    return false;
                }
            }
        }

        if (StringUtils.isBlank(accountDto.getUsername().trim())) {
            setErrorForm("Tên đăng nhập là trường bắt buộc");
            return false;
        }

        if (StringUtils.isBlank(accountDto.getEmail())) {
            setErrorForm("Địa chỉ email là trường bắt buộc");
            return false;
        }

        if (!StringUtil.isValidEmailAddress(accountDto.getEmail())) {
            setErrorForm("Địa chỉ email không đúng định dạng");
            FacesUtil.updateView("growl");
            return false;
        }

        accountDto.setEmail(accountDto.getEmail().trim());
        if (accountDto.getAccountId() == null || !objBackup.getEmail().equalsIgnoreCase(accountDto.getEmail().trim())) {
            Account oldAccount = accountRepository.findByEmail(accountDto.getEmail());
            if (oldAccount != null) {
                FacesUtil.addErrorMessage("Email đã tồn tại");
                FacesUtil.updateView("growl");
                return false;
            }
        }

        if (accountDto.getRoleId() == -1) {
            setErrorForm("Nhóm quyền là trường bắt buộc");
            return false;
        }

        accountDto.setUsername(accountDto.getUsername().trim());
        if (accountDto.getAccountId() == null || !objBackup.getUsername().equalsIgnoreCase(accountDto.getUsername().trim())) {
            Account oldAccount = accountRepository.getAccountByUsernameAndRoleId(accountDto.getUsername(),DbConstant.ROLE_ID_USER);
            if (oldAccount != null) {
                FacesUtil.addErrorMessage("Tên đăng nhập đã tồn tại");
                FacesUtil.updateView("growl");
                return false;
            }
        }

        return true;
    }

    public void showPopup(AccountDto ac) {
        objBackup = new AccountDto();
        accountDto = new AccountDto();
        BeanUtils.copyProperties(ac, accountDto);
        BeanUtils.copyProperties(ac, objBackup);
        CityDistrictDto cityDistrictDto = new CityDistrictDto(ac.getProvinceId(), ac.getDistrictId(), ac.getCommuneId());
        cityDistrictController.setCityDistrictDto(cityDistrictDto);
        cityDistrictController.loadData();
    }

    public void onSaveData() {
        if (!validateField(accountDto)) {
            return;
        }

        Account account = new Account();
        accountDto.setPosition(accountDto.getPosition().trim());
        accountDto.setProvinceId(cityDistrictController.getCityDistrictDto().getProvinceId());
        accountDto.setDistrictId(cityDistrictController.getCityDistrictDto().getDistrictId());
        accountDto.setCommuneId(cityDistrictController.getCityDistrictDto().getCommuneId());
        BeanUtils.copyProperties(accountDto, account);

        if (account.getAccountId() == null) {
            String password = StringUtil.generatePassword();
            account.setSalt(StringUtil.generateSalt());
            account.setPassword(StringUtil.encryptPassword(password, account.getSalt()));
            if (account.getAccountId() == null) {
                account.setAccountStatus(DbConstant.ACCOUNT_ACTIVE_STATUS);
            }
            account.setFirstTimeLogin(false);
            account.setCreateBy(authorizationController.getAccountDto().getAccountId());
            account.setUpdateBy(authorizationController.getAccountDto().getAccountId());
            account.setTimeToChangePassword(new Date());
            // send email
            EmailUtil.getInstance().sendPasswordAdminEmail(ROLE_NAME_ADMIN, account.getEmail(), password, account.getFullName(), account.getUsername());
        } else {
            account.setSalt(objBackup.getSalt());
            account.setPassword(objBackup.getPassword());
            account.setAccountStatus(DbConstant.ACCOUNT_ACTIVE_STATUS);
            account.setFirstTimeLogin(objBackup.isFirstTimeLogin());
            account.setCreateBy(objBackup.getCreateBy());
            account.setCreateDate(objBackup.getCreateDate());
            account.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        }
        administratorsCentralStaffRepository.save(account);
        FacesUtil.closeDialog("dialogInsertMember");
        if (accountDto.getAccountId() == null) {
            FacesUtil.addSuccessMessage("Tài khoản được tạo thành công, truy cập \"" + accountDto.getEmail() + "\" để lấy mật khẩu hệ thống !");
        } else {
            FacesUtil.addSuccessMessage("Lưu thành công !");
        }
        resetAll();
        FacesUtil.updateView("growl");
        onSearch();
        FacesUtil.updateView("dlForm");
    }

    public void onDelete(AccountDto ac) {
        Account acc = new Account();
        ac.setAccountStatus(DbConstant.ACCOUNT_DELETE_STATUS);
        BeanUtils.copyProperties(ac,acc);
        accountRepository.save(acc);
        FacesUtil.addSuccessMessage("Xóa thành công");
        onSearch();
    }

    public void onUnlock(AccountDto ac) {
        ac.setAccountStatus(DbConstant.ACCOUNT_ACTIVE_STATUS);
        ac.setLoginFailed(0);
        ac.setFirstTimeLogin(true);
        account = new Account();
        BeanUtils.copyProperties(ac, account);
        accountRepository.save(account);
        EmailUtil.getInstance().onUnLockAccount(account.getEmail(), account.getFullName());
        FacesUtil.addSuccessMessage("Mở khóa tài khoản thành công");
        resetAll();
        onSearch();
    }

    public void onLock(AccountDto ac) {
        ac.setAccountStatus(DbConstant.ACCOUNT_LOCK_STATUS);
        ac.setLoginFailed(5);
        account = new Account();
        BeanUtils.copyProperties(ac, account);
        accountRepository.save(account);
        FacesUtil.addSuccessMessage("Khóa tài khoản thành công");
        EmailUtil.getInstance().onLockAccount(account.getEmail(), account.getFullName());
        resetAll();
        onSearch();
    }

    public void onResetPassword(AccountDto dto) {
        String password = StringUtil.generatePassword();
        Account account = new Account();
        BeanUtils.copyProperties(dto, account);
        account.setSalt(StringUtil.generateSalt());
        account.setPassword(StringUtil.encryptPassword(password, account.getSalt()));
        account.setFirstTimeLogin(false);
        accountRepository.save(account);
        // send email
        EmailUtil.getInstance().sendResetPasswordUserEmail(account.getEmail(),account.getUsername(), password, account.getFullName());
        FacesUtil.addSuccessMessage("Cấp lại mật khẩu thành công, truy cập \"" + account.getEmail() + "\" để lấy mật khẩu truy cập hệ thống!");
    }

    public void resetDialog() {
        accountDto = new AccountDto();
        cityDistrictController.resetAll();
    }

    public void onSearch() {
        lazyDataModel = new LazyDataModel<AccountDto>() {
            @Override
            public List<AccountDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                adminSearchDto.setPageIndex(first);
                adminSearchDto.setPageSize(pageSize);
                adminSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                adminSearchDto.setSortOrder(sort);
                return administratorsCentralStaffRepository.search(adminSearchDto);
            }

            @Override
            public AccountDto getRowData(String rowKey) {
                List<AccountDto> accounts = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (AccountDto obj : accounts) {
                    if (obj.getAccountId().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = administratorsCentralStaffRepository.countSearch(adminSearchDto).intValue();

        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }

    public String convertStringStatusAccount(Integer status) {
        return status == DbConstant.ACCOUNT_ACTIVE_STATUS ? "Hoạt động" : "Đã bị khoá";
    }
    @Override
    protected EScope getMenuId() {
        return EScope.ACCOUNT_OFFICIALS;
    }
}
