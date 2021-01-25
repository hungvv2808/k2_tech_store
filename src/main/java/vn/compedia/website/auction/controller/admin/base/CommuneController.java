package vn.compedia.website.auction.controller.admin.base;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.CommonController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.dto.base.CommuneDto;
import vn.compedia.website.auction.dto.base.CommuneSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.Commune;
import vn.compedia.website.auction.model.District;
import vn.compedia.website.auction.model.Province;
import vn.compedia.website.auction.repository.AccountRepository;
import vn.compedia.website.auction.repository.CommuneRepository;
import vn.compedia.website.auction.repository.DistrictRepository;
import vn.compedia.website.auction.repository.ProvinceRepository;
import vn.compedia.website.auction.service.CommuneService;
import vn.compedia.website.auction.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = "session")
@Getter
@Setter
public class CommuneController extends BaseController {

    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private CommonController commonController;

    @Autowired
    protected CommuneRepository communeRepository;
    @Autowired
    protected DistrictRepository districtRepository;
    @Autowired
    private CommuneService communeService;
    @Autowired
    protected ProvinceRepository provinceRepository;
    @Autowired
    protected AccountRepository accountRepository;

    private Commune commune;
    private LazyDataModel<CommuneDto> lazyDataModel;
    private String labelVersion;
    private CommuneSearchDto communeSearchDto;
    private List<SelectItem> districtList;
    private Commune communeCopy;
    private List<SelectItem> provinceList;
    private String labelSynchronized;
    private boolean showSuccess;
    private Long idVersion;
    private Long versionUse = 0L;
    private boolean showSuccessVersion;
    private List<Account> accountList = new ArrayList<>();

    private List<Province> provinceListAdd = new ArrayList<>();
    private List<District> districtListAdd = new ArrayList<>();
    private List<Commune> communeListAdd = new ArrayList<>();

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        commune = new Commune();
        communeCopy = new Commune();
        communeSearchDto = new CommuneSearchDto();
        districtList = new ArrayList<>();
        provinceList = new ArrayList<>();
        List<Province> provinces = (List<Province>) provinceRepository.findAll();
        for (Province pr : provinces) {
            provinceList.add(new SelectItem(pr.getProvinceId(), commonController.upperCaseFirstChar(pr.getName())));
        }
        onSearch();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");

        if (showSuccess) {
            FacesUtil.addSuccessMessage("Đồng bộ thành công");
            onSearch();
            showSuccess = false;
        }

        versionUse = 0L;

        if (showSuccessVersion) {
            FacesUtil.addSuccessMessage("Thay đổi version thành công");
            onSearch();
            showSuccessVersion = false;
        }
    }

    public void onSaveData() {
        List<Account> checkCommune = accountRepository.findAccountsByCommuneId(commune.getCommuneId());
        if ((checkCommune.size() != 0 && commune.getCommuneId() != null)) {
            FacesUtil.addErrorMessage("Không được sửa vì " + communeCopy.getName() + " đang được sử dụng");
            FacesUtil.updateView("growl");
            FacesUtil.closeDialog("dialogInsertUpdate");
            return;
        }
        commune.setCode(commune.getCode().trim());
        if (commune.getCommuneId() == null || !commune.getCode().trim().equalsIgnoreCase(communeCopy.getCode().trim())) {
            List<Commune> oldCode = communeRepository.findByCode(commune.getCode());
            if (oldCode.size() != 0) {
                FacesUtil.addErrorMessage("Mã phường/xã đã tồn tại");
                FacesUtil.updateView("growl");
                return;
            }
        }
        commune.setName(commune.getName().trim());
        if (commune.getCommuneId() == null || !commune.getName().equalsIgnoreCase(communeCopy.getName().trim())) {
            List<Commune> oldCode = communeRepository.findByNameAndDistrictId(commune.getName(), commune.getDistrictId());
            if (oldCode.size() != 0) {
                FacesUtil.addErrorMessage("Tên phường/xã đã tồn tại trong quận/huyện bạn chọn");
                FacesUtil.updateView("growl");
                return;
            }
        }

        if (commune.getCreateBy() == null) {
            commune.setCreateBy(authorizationController.getAccountDto().getAccountId());
            commune.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        } else {
            commune.setCreateBy(communeCopy.getCreateBy());
            commune.setCreateDate(communeCopy.getCreateDate());
            commune.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        }
        commune.setVersionId(idVersion);

        FacesUtil.addSuccessMessage("Lưu thành công");
        FacesUtil.closeDialog("dialogInsertUpdate");
        resetAll();
        FacesUtil.updateView("growl");
        FacesUtil.updateView("searchForm");
        onSearch();
    }

    public void clearData() {
        if (commune.getCommuneId() != null && commune.getCommuneId() > 0) {
            BeanUtils.copyProperties(communeCopy, commune);
            onChangeProvince(commune.getDistrictId());
        } else
            resetDialog();
    }

    public void showUpdatePopup(Commune obj) {
        communeCopy = new CommuneDto();
        BeanUtils.copyProperties(obj, commune);
        BeanUtils.copyProperties(obj, communeCopy);
        onChangeProvince(obj.getProvinceId());
    }

    public void onChangeProvince(Long id) {
        districtList = new ArrayList<>();
        if (id != null) {
            List<District> district = districtRepository.findDistrictsByProvinceId(id);
            for (District dtr : district) {
                districtList.add(new SelectItem(dtr.getDistrictId(), commonController.upperCaseFirstChar(dtr.getName())));
            }
        }
    }

    public void resetDialog() {
        commune = new Commune();
    }

    public void onDelete(Commune deleteObj) {
        try {
            List<Account> checkDistrict = accountRepository.findAccountsByCommuneId(deleteObj.getCommuneId());
            if (checkDistrict.size() == 0) {
                communeService.deleteCommunebyCommuneId(deleteObj.getCommuneId());
                FacesUtil.addSuccessMessage("Xóa thành công");
                onSearch();
            } else {
                FacesUtil.addErrorMessage("Xóa thất bại vì " + deleteObj.getName() + " đã được sử dụng để tạo tài khoản");
            }

        } catch (Exception e) {
            FacesUtil.addErrorMessage("Xóa thất bại");
        }
    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<CommuneDto>() {

            @Override
            public List<CommuneDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                communeSearchDto.setPageIndex(first);
                communeSearchDto.setPageSize(pageSize);
                communeSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                communeSearchDto.setSortOrder(sort);
                return communeRepository.search(communeSearchDto);
            }

            @Override
            public CommuneDto getRowData(String rowKey) {
                List<CommuneDto> communeList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (CommuneDto obj : communeList) {
                    if (obj.getCode().equals(value) || obj.getName().equals(value)) {
                        return obj;
                    }

                }
                return null;
            }
        };
        int count = communeRepository.countSearch(communeSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView(" ");
    }

    @Override
    protected EScope getMenuId() {
        return EScope.COMMUNE;
    }

}


