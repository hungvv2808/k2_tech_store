package vn.compedia.website.auction.controller.admin.user;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.controller.admin.common.CityDistrictController;
import vn.compedia.website.auction.controller.frontend.common.AddressFEController;
import vn.compedia.website.auction.dto.common.CityDistrictDto;
import vn.compedia.website.auction.dto.user.AccountDto;
import vn.compedia.website.auction.dto.user.AccountSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.model.api.ApiSex;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.util.*;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = "session")
@Getter
@Setter
public class AccountController extends BaseController {
    @Inject
    protected AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private CityDistrictController cityDistrictController;
    @Inject
    private AddressFEController addressFEController;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    protected AccountDetailRepository accountDetailRepository;
    @Autowired
    protected RegulationRepository regulationRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private RegulationFileRepository regulationFileRepository;
    @Autowired
    private ApiSexRepository apiSexRepository;
    @Autowired
    private PlacesOfIssueRepository placesOfIssueRepository;

    private boolean publicRegister; // true public
    private Account account;
    private LazyDataModel<AccountDto> lazyDataModel;
    private AccountSearchDto accountSearchDto;
    private List<SelectItem> accountList;
    private AccountDto objBackup;
    private boolean married;
    private List<SelectItem> listProvinceAccount;
    private List<PlacesOfIssue> placesOfIssueList;
    private LazyDataModel<AccountDto> accountDtos;
    private Regulation regulation;
    private List<Asset> assetList;
    private Province province;
    private AccountDto accountDto;
    private AccountDto accountDtoAddress;
    private Integer tabindex;
    private Date now;
    private final String ROLE_NAME_USER = "người dùng";
    private boolean showValidate;
    private String filePath;
    private List<SelectItem> apiSexList;

    public void initData(boolean publicRegister) {
        this.publicRegister = publicRegister;
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        filePath = null;
        showValidate = false;
        now = new Date();
        tabindex = 0;
        assetList = new ArrayList<>();
        regulation = new Regulation();
        accountList = new ArrayList<>();
        accountDto = new AccountDto();
        accountSearchDto = new AccountSearchDto();
        accountSearchDto.setOrg(null);
        accountDto.setOrg(false);
        married = false;
        listProvinceAccount = new ArrayList<>();
        placesOfIssueList = (List<PlacesOfIssue>) placesOfIssueRepository.findAll();
        for (PlacesOfIssue dto : placesOfIssueList) {
            listProvinceAccount.add(new SelectItem(dto.getPlacesOfIssueId(), dto.getName()));
        }
        apiSexList = new ArrayList<>();
        List<ApiSex> listApiSex = (List<ApiSex>) apiSexRepository.findAll();
        for (ApiSex sex : listApiSex) {
            apiSexList.add(new SelectItem(sex.getSexId(), sex.getName()));
        }
        cityDistrictController.resetAll();
        onSearch();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    private boolean validate() {
        if (accountDto.isOrg()) {
            if (StringUtils.isBlank(accountDto.getOrgName())) {
                setErrorForm("Bạn vui lòng nhập tên tổ chức");
                FacesUtil.updateView("growl");
                return false;
            }
            if (StringUtils.isBlank(accountDto.getBusinessLicense())) {
                setErrorForm("Bạn vui lòng nhập số đăng ký kinh doanh");
                FacesUtil.updateView("growl");
                return false;
            }
            if (StringUtils.isBlank(accountDto.getOrgPhone())) {
                setErrorForm("Bạn vui lòng nhập số điện thoại tổ chức");
                FacesUtil.updateView("growl");
                return false;
            }
            if (StringUtils.isBlank(accountDto.getFullName())) {
                setErrorForm("Bạn vui lòng nhập tên người đại diện");
                FacesUtil.updateView("growl");
                return false;
            }
            if (StringUtils.isBlank(accountDto.getPosition())) {
                setErrorForm("Bạn vui lòng nhập chức vụ");
                FacesUtil.updateView("growl");
                return false;
            }
            if (accountDto.getPhone().trim().equals(accountDto.getOrgPhone().trim())) {
                setErrorForm("Số điện thoại của người đại diện không được trùng số điện thoại tổ chức");
                FacesUtil.updateView("growl");
                return false;
            }
        }
        if(!accountDto.isOrg()) {
            if (accountDto.getDateOfBirth() == null) {
                setErrorForm("Bạn vui lòng chọn ngày tháng năm sinh");
                FacesUtil.updateView("growl");
                return false;
            }
            if (accountDto.getDateOfIssue().getTime() <= accountDto.getDateOfBirth().getTime()) {
                setErrorForm("Ngày cấp CMND/CCCD phải lớn hơn ngày sinh");
                FacesUtil.updateView("growl");
                return false;
            }
            if (married) {
                if (StringUtils.isBlank(accountDto.getRelativeName())) {
                    setErrorForm("Bạn vui lòng nhập tên vợ/chồng");
                    FacesUtil.updateView("growl");
                    return false;
                }
                if (StringUtils.isBlank(accountDto.getRelativeIdCardNumber())) {
                    setErrorForm("Bạn vui lòng nhập số CMND/CCCD vợ/chồng");
                    FacesUtil.updateView("growl");
                    return false;
                }
                if (!accountDto.getRelativeIdCardNumber().matches("^[0-9]{9}(?:[0-9]{3})?$|")) {
                    setErrorForm("Số CMND/CCCD vợ/chồng không đúng định dạng");
                    FacesUtil.updateView("growl");
                    return false;
                }
                if (accountDto.getIdCardNumber().compareToIgnoreCase(accountDto.getRelativeIdCardNumber()) == 0) {
                    setErrorForm("Số CMND/CCCD của vợ/chồng không được trùng nhau");
                    FacesUtil.updateView("growl");
                    return false;
                }
            }
        }
        if (StringUtils.isBlank(accountDto.getFullName())) {
            setErrorForm("Bạn vui lòng nhập họ và tên");
            FacesUtil.updateView("growl");
            return false;
        }
        if (StringUtils.isBlank(accountDto.getPhone())) {
            setErrorForm("Bạn vui lòng nhập số điện thoại");
            FacesUtil.updateView("growl");
            return false;
        }
        if (StringUtils.isBlank(accountDto.getEmail())) {
            setErrorForm("Bạn vui lòng nhập email");
            FacesUtil.updateView("growl");
            return false;
        }
        if (!StringUtil.isValidEmailAddress(accountDto.getEmail())) {
            setErrorForm("Email không đúng định dạng");
            FacesUtil.updateView("growl");
            return false;
        }
        if (StringUtils.isBlank(accountDto.getUsername())) {
            setErrorForm("Bạn vui lòng nhập tên đăng nhập");
            FacesUtil.updateView("growl");
            return false;
        }
        if (accountDto.getSex() == null) {
            setErrorForm("Bạn vui lòng chọn giới tính");
            FacesUtil.updateView("growl");
            return false;
        }
        if (StringUtils.isBlank(accountDto.getIdCardNumber())) {
            setErrorForm("Bạn vui lòng nhập số CMND/CCCD");
            FacesUtil.updateView("growl");
            return false;
        }
        if (!accountDto.getIdCardNumber().matches("^[0-9]{9}(?:[0-9]{3})?$|")) {
            setErrorForm("Số CMND/CCCD không đúng định dạng");
            FacesUtil.updateView("growl");
            return false;
        }
        if (accountDto.getProvinceIdOfIssue() == null) {
            setErrorForm("Bạn vui lòng chọn nơi cấp CMND/CCCD");
            FacesUtil.updateView("growl");
            return false;
        }
        if (accountDto.getDateOfIssue() == null) {
            setErrorForm("Bạn vui lòng nhập ngày cấp CMND/CCCD");
            FacesUtil.updateView("growl");
            return false;
        }
        if (StringUtils.isBlank(accountDto.getPermanentResidence())) {
            setErrorForm("Bạn vui lòng nhập hộ khẩu thường trú");
            FacesUtil.updateView("growl");
            return false;
        }
        accountDto.setUsername(accountDto.getUsername().trim());
        if (accountDto.getAccountId() == null || !objBackup.getUsername().equalsIgnoreCase(accountDto.getUsername().trim())) {
            Account oldAccount = accountRepository.findAccountByUsernameAndRoleId(accountDto.getUsername(),DbConstant.ROLE_ID_USER);
            if (oldAccount != null) {
                setErrorForm("Tên đăng nhập đã tồn tại");
                FacesUtil.updateView("growl");
                return false;
            }
        }
        accountDto.setEmail(accountDto.getEmail().trim());
        if (accountDto.getAccountId() == null || !objBackup.getEmail().equalsIgnoreCase(accountDto.getEmail().trim())) {
            Account oldAccount = accountRepository.findByEmail(accountDto.getEmail());
            if (oldAccount != null) {
                setErrorForm("Email đã tồn tại");
                FacesUtil.updateView("growl");
                return false;
            }
        }
        accountDto.setIdCardNumber(accountDto.getIdCardNumber().trim());
        if (accountDto.getAccountId() == null || objBackup.getIdCardNumber() == null || !objBackup.getIdCardNumber().equalsIgnoreCase(accountDto.getIdCardNumber().trim())) {
            Account oldAccount = accountRepository.findByIdCardNumber(accountDto.getIdCardNumber());
            if (oldAccount != null) {
                setErrorForm("Số CMND/CCCD đã tồn tại");
                FacesUtil.updateView("growl");
                return false;
            }
        }
        if(!accountDto.isOrg()) {
            accountDto.setPhone(accountDto.getPhone().trim());
            if (accountDto.getAccountId() == null || !objBackup.getPhone().equalsIgnoreCase(accountDto.getPhone().trim())) {
                Account oldAccount = accountRepository.findByPhone(accountDto.getPhone());
                if (oldAccount != null) {
                    setErrorForm("Số điện thoại đã tồn tại");
                    FacesUtil.updateView("growl");
                    return false;
                }
                Account checkAccountOrg = accountRepository.findByOrgPhone(accountDto.getPhone());
                if (checkAccountOrg != null) {
                    setErrorForm("Số điện thoại đã tồn tại");
                    FacesUtil.updateView("growl");
                    return false;
                }
            }
            if(married) {
                accountDto.setRelativeIdCardNumber(accountDto.getRelativeIdCardNumber().trim());
                if (accountDto.getAccountId() == null || objBackup.getRelativeIdCardNumber() == null || !objBackup.getRelativeIdCardNumber().equalsIgnoreCase(accountDto.getRelativeIdCardNumber().trim())) {
                    Account oldAccount = accountRepository.findByRelativeIdCardNumber(accountDto.getRelativeIdCardNumber());
                    if (oldAccount != null) {
                        setErrorForm("Số CMND/CCCD vợ/chồng đã tồn tại");
                        FacesUtil.updateView("growl");
                        return false;
                    }
                }
            }
        }
        if(accountDto.isOrg()) {
            accountDto.setPhone(accountDto.getPhone().trim());
            if (accountDto.getAccountId() == null || !objBackup.getPhone().equalsIgnoreCase(accountDto.getPhone().trim())) {
                Account oldAccount = accountRepository.findByPhone(accountDto.getPhone());
                if (oldAccount != null) {
                    setErrorForm("Số điện thoại của người đại diện đã tồn tại");
                    FacesUtil.updateView("growl");
                    return false;
                }
                Account checkAccountOrg = accountRepository.findByOrgPhone(accountDto.getPhone());
                if (checkAccountOrg != null) {
                    setErrorForm("Số điện thoại của người đại diện đã tồn tại");
                    FacesUtil.updateView("growl");
                    return false;
                }
            }
            accountDto.setOrgPhone(accountDto.getOrgPhone().trim());
            if (accountDto.getAccountId() == null || !objBackup.getPhone().equalsIgnoreCase(accountDto.getPhone().trim())) {
                Account oldAccount = accountRepository.findByPhone(accountDto.getOrgPhone());
                if (oldAccount != null) {
                    setErrorForm("Số điện thoại tổ chức đã tồn tại");
                    FacesUtil.updateView("growl");
                    return false;
                }
                Account checkAccountOrg = accountRepository.findByOrgPhone(accountDto.getOrgPhone());
                if (checkAccountOrg != null) {
                    setErrorForm("Số điện thoại tổ chức đã tồn tại");
                    FacesUtil.updateView("growl");
                    return false;
                }
            }
        }
        return true;
    }

    public void onSaveData() {
        showValidate = true;
        if (!validate()) {
            return;
        }
        accountDto.setProvinceId(cityDistrictController.getCityDistrictDto().getProvinceId());
        accountDto.setDistrictId(cityDistrictController.getCityDistrictDto().getDistrictId());
        accountDto.setCommuneId(cityDistrictController.getCityDistrictDto().getCommuneId());
        Account account = new Account();
        BeanUtils.copyProperties(accountDto, account);
        if(!married){
            account.setRelativeIdCardNumber(null);
            account.setRelativeName(null);
        }
        //send mail
        if (account.getAccountId() == null) {
            account.setRoleId(DbConstant.ROLE_ID_USER);
            account.setAccountStatus(DbConstant.ACCOUNT_ACTIVE_STATUS);
            account.setSalt(StringUtil.generateSalt());
            String password = publicRegister ? account.getPassword() : StringUtil.generatePassword();
            account.setPassword(StringUtil.encryptPassword(password, account.getSalt()));
            account.setCreateBy(authorizationController.getAccountDto().getAccountId());
            account.setUpdateBy(authorizationController.getAccountDto().getAccountId());
            account.setTimeToChangePassword(new Date());
            //send email
            EmailUtil.getInstance().sendPasswordUserEmail(ROLE_NAME_USER, account.getEmail(), password, account.getUsername(), account.getFullName());

        } else {
            account.setSalt(objBackup.getSalt());
            account.setPassword(objBackup.getPassword());
            account.setCreateBy(objBackup.getCreateBy());
            account.setCreateDate(objBackup.getCreateDate());
            account.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        }
        if (account.getAccountId() == null) {
            actionSystemController().onSave("Tạo thông tin tài khoản " + account.getUsername(), authorizationController.getAccountDto().getAccountId());
            setSuccessForm("Bạn đã tạo thành công tài khoản, truy cập \"" + account.getEmail() + "\" để lấy mật khẩu ");
        } else {
            actionSystemController().onSave("Sửa thông tin tài khoản " + account.getUsername(), authorizationController.getAccountDto().getAccountId());
            setSuccessForm("Chỉnh sửa tài khoản thành công");
        }
        accountRepository.save(account);

        FacesUtil.closeDialog("dialogInsertUpdate");
        resetAll();
        FacesUtil.updateView("growl");
        onSearch();
        FacesUtil.updateView("dlg");
    }

    public void showUpdatePopup(AccountDto obj) {
        married = false;
        if (obj.isOrg()) {
            tabindex = 1;
        } else {
            tabindex = 0;
        }
        if (StringUtils.isBlank(obj.getRelativeIdCardNumber())|| StringUtils.isBlank(obj.getRelativeName())) {
            married = false;
        }else {
            married = true;
        }

        objBackup = new AccountDto();
        BeanUtils.copyProperties(obj, accountDto);
        BeanUtils.copyProperties(accountDto, objBackup);
        cityDistrictController.resetAll();
        CityDistrictDto cityDistrictDto = new CityDistrictDto(obj.getProvinceId(), obj.getDistrictId(), obj.getCommuneId());
        cityDistrictController.setCityDistrictDto(cityDistrictDto);
        cityDistrictController.loadData();
    }

    public void resetDialog() {
        accountDto = new AccountDto();
        cityDistrictController.resetAll();
        married = false;
        tabindex = 0;
    }

    public void onDelete(AccountDto deleteObj) {
        try {
            accountRepository.deleteById(deleteObj.getAccountId());
            actionSystemController().onSave("Xóa thông tin tài khoản " + deleteObj.getFullName(), authorizationController.getAccountDto().getAccountId());
            setSuccessForm("Xóa thành công");
            FacesUtil.updateView("growl");
            onSearch();
        } catch (Exception e) {
            setErrorForm("Xóa thất bại");
            FacesUtil.updateView("growl");
        }
    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<AccountDto>() {
            @Override
            public List<AccountDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                accountSearchDto.setPageIndex(first);
                accountSearchDto.setPageSize(pageSize);
                accountSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                accountSearchDto.setSortOrder(sort);
                return accountRepository.search(accountSearchDto);
            }

            @Override
            public AccountDto getRowData(String rowKey) {
                List<AccountDto> AccountList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (AccountDto obj : AccountList) {
                    if (obj.getEmail().equals(value) || obj.getPhone().equals(value) || obj.getIdCardNumber().equals(value) || obj.getFullName().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = accountRepository.countSearch(accountSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }

    public void onSearchDetail() {
        accountSearchDto.setAccountId(accountDto.getAccountId());
        province = provinceRepository.findByProvinceId(accountDto.getProvinceIdOfIssue());
        accountDtos = new LazyDataModel<AccountDto>() {
            @Override
            public List<AccountDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                accountSearchDto.setPageIndex(first);
                accountSearchDto.setPageSize(pageSize);
                accountSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                accountSearchDto.setSortOrder(sort);
                return accountDetailRepository.search(accountSearchDto);
            }

            @Override
            public AccountDto getRowData(String rowKey) {
                List<AccountDto> AccountList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (AccountDto obj : AccountList) {
                    if (obj.getName().equals(value) || obj.getAssetName().equals(value)) {
                        return obj;
                    }

                }
                return null;
            }
        };
        int count = accountDetailRepository.countSearch(accountSearchDto).intValue();
        accountDtos.setRowCount(count);
    }

//    public void onResetPassword(Account dto) {
//        Account account = accountRepository.findByAccountId(dto.getAccountId());
//        account.setSalt(StringUtil.generateSalt());
//        String password = publicRegister ? account.getPassword() : StringUtil.generateSalt();
//        account.setPassword(StringUtil.encryptPassword(password, account.getSalt()));
//        accountRepository.save(account);
//        if (account.getAccountId() != null) {
//            //send email
//            EmailUtil.getInstance().sendPasswordUserEmail(ROLE_NAME_USER, account.getEmail(), password);
//            setSuccessForm("Tạo lại mật khẩu thành công");
//        } else {
//            setErrorForm("Tạo lại mật khẩu thất bại");
//        }
//    }

    public void onTabChange() {
        accountDto.setOrg(!accountDto.isOrg());
        if (accountDto.isOrg()) {
            tabindex = 1;
        } else {
            tabindex = 0;
        }
    }

    public void reset() {
        accountDtos = new LazyDataModel<AccountDto>() {
            @Override
            public List<AccountDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        accountDtos.setRowCount(0);
        accountSearchDto.setKeyword(null);
        onSearchDetail();
    }

    public void onClickDetail(AccountDto accountDtoDetail) {
        accountDto = new AccountDto();
        accountSearchDto = new AccountSearchDto();
        BeanUtils.copyProperties(accountDtoDetail, accountDto);
        FacesUtil.redirect("/admin/quan-ly-nguoi-dung/chi-tiet-nguoi-dung.xhtml");
        onSearchDetail();
    }

    public void getRegulationInfor(Long regulationId,Long accountId, Long acction) {
        if (regulationId == null) {
            setDefaultValue();
        } else {
            regulation = regulationRepository.findById(regulationId).orElse(null);
            if(acction == 1) {
                assetList = assetRepository.findAssetsByRegulationId(regulationId, accountId);
            }
            if(acction == 2) {
                assetList = assetRepository.findAssetsByRegulationId(regulationId);
            }
        }
    }

    public void setDefaultValue() {
        regulation = new Regulation();
        assetList = new ArrayList<>();
    }

    public void onUnlock(AccountDto ac) {
        AccountDto acc = new AccountDto();
        BeanUtils.copyProperties(ac,acc);
        ac.setAccountStatus(DbConstant.ACCOUNT_ACTIVE_STATUS);
        ac.setLoginFailed(0);
        ac.setFirstTimeLogin(true);
        account = new Account();
        BeanUtils.copyProperties(ac, account);
        accountRepository.save(account);
        if(acc.getAccountStatus().equals(DbConstant.ACCOUNT_LOCK_STATUS)) {
            setSuccessForm("Mở khóa tài khoản thành công");
        } else {
            setSuccessForm("Kích hoạt tài khoản thành công");
        }
        actionSystemController.onSave("Mở khoá thông tin tài khoản " + (ac.isOrg() ? "tổ chức" : "người dùng") + " \"" + ac.getFullName() + "\"", authorizationController.getAccountDto().getAccountId());
        resetAll();
        onSearch();
    }

    public void onLock(AccountDto ac) {
        account = new Account();
        ac.setAccountStatus(DbConstant.ACCOUNT_LOCK_STATUS);
        ac.setLoginFailed(5);
        BeanUtils.copyProperties(ac, account);
        accountRepository.save(account);
        actionSystemController.onSave("Khoá thông tin tài khoản " + (ac.isOrg() ? "tổ chức" : "người dùng") + " \"" + ac.getFullName() + "\"", authorizationController.getAccountDto().getAccountId());
        setSuccessForm("Khóa tài khoản thành công");
        resetAll();
        onSearch();
    }

    public String upperCaseFirstChar(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    //Get file path regulation
    public void getFilePathRegulation(AccountDto accountDto) {
        filePath = accountDto.getFilePath();
    }

    public String cutStringAssetName(String assetName) {
        return assetName.length() > 105 ? assetName.substring(0, 100) + " ..." : assetName;
    }

    @Override
    protected EScope getMenuId() {
        return EScope.ACCOUNT_USER;
    }
}


