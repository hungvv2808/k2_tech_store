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
import vn.compedia.website.auction.controller.admin.api.GetApiPDCController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.dto.api.HistorySyncSearchDto;
import vn.compedia.website.auction.dto.base.DistrictDto;
import vn.compedia.website.auction.dto.base.DistrictSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.service.*;
import vn.compedia.website.auction.system.socket.SocketServer;
import vn.compedia.website.auction.system.socket.dto.UpdateScopeResponseDto;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.*;

@Named
@Scope(value = "session")
@Getter
@Setter
public class DistrictController extends BaseController {

    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private GetApiPDCController getTokenApiController;
    @Inject
    private CommonController commonController;

    @Autowired
    protected CommuneRepository communeRepository;
    @Autowired
    protected StatusApiRepository statusApiRepository;
    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private DistrictService districtService;
    @Autowired
    private CommuneService communeService;
    @Autowired
    protected StatusVersionRepository statusVersionRepository;
    @Autowired
    protected DistrictRepository districtRepository;
    @Autowired
    protected ProvinceRepository provinceRepository;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    protected HistorySyncRepository historySyncRepository;
    @Autowired
    protected ApiService apiService;
    @Autowired
    protected HistorySyncService historySyncService;
    @Autowired
    protected ProvinceVersionRepository provinceVersionRepository;
    @Autowired
    protected DistrictVersionRepository districtVersionRepository;
    @Autowired
    protected  CommuneVersionRepository communeVersionRepository;

    private District district;
    private DistrictApi districtApi;
    private LazyDataModel<DistrictDto> lazyDataModel;
    private LazyDataModel<HistorySync> lazyDataModelVersion;
    private HistorySyncSearchDto historySyncSearchDto;
    private String labelVersion;
    private StatusVersion statusVersion;
    private DistrictSearchDto districtSearchDto;
    private DistrictDto districtDto;
    private List<SelectItem> districtList;
    private District districtCopy;
    private List<SelectItem> provinceList;
    private String labelSynchronized;
    private boolean showSuccess;
    private boolean showSuccessVersion;
    private StatusApi statusApi;
    private List<HistorySync> historySyncList = new ArrayList<>();
    private Long idVersion;
    private HistorySync historySync;
    private Long versionUse = 0L;
    private List<Account> accountList= new ArrayList<>();

    private List<ProvinceVersion> provinceVersionList = new ArrayList<>();
    private List<DistrictVersion> districtVersionList = new ArrayList<>();
    private List<CommuneVersion> communeVersionList = new ArrayList<>();
    private List<Province> provinceListAdd = new ArrayList<>();
    private List<District> districtListAdd = new ArrayList<>();
    private List<Commune> communeListAdd = new ArrayList<>();
    private List<ProvinceVersion> provinceListCheck = new ArrayList<>();
    private List<DistrictVersion> districtListCheck = new ArrayList<>();
    private List<CommuneVersion> communeListCheck = new ArrayList<>();

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            statusApi = statusApiRepository.findById(DbConstant.STATUS_API_ID).orElse(null);
            if(statusApi == null || DbConstant.STATUS_API_STATUS_INACTIVE == statusApi.getStatus()) {
                labelSynchronized ="Đồng bộ dữ liệu";
            } else {
                labelSynchronized ="Đang đồng bộ dữ liệu";
            }
            statusVersion = statusVersionRepository.findById(DbConstant.STATUS_VERSION_ID).orElse(null);
            if(statusVersion == null || DbConstant.STATUS_VERSION_STATUS_INACTIVE == statusVersion.getStatus()) {
                labelVersion = "Quản lý version";
            } else {
                labelVersion = "Đang thay đổi version";
            }
            versionUse = 0L;
            resetAll();
        }
    }

    public void resetAll() {
        districtList = new ArrayList<>();
        district = new District();
        districtDto = new DistrictDto();
        districtSearchDto = new DistrictSearchDto();
        historySyncSearchDto = new HistorySyncSearchDto();
        provinceList = new ArrayList<>();
        List<Province> province = (List<Province>) provinceRepository.findAll();
        for (Province prv : province) {
            provinceList.add(new SelectItem(prv.getProvinceId(), commonController.upperCaseFirstChar(prv.getName())));
        }
        onSearch();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        if (showSuccess) {
            FacesUtil.addSuccessMessage("Đồng bộ thành công");
            onSearch();
            showSuccess = false;
        }
        versionUse = 0L;
        historySyncList = (List<HistorySync>) historySyncRepository.findAll();
        if(historySyncList.size() == 0) {
            idVersion = 1L;
        } else {
            HistorySync his = new HistorySync();
            for(HistorySync historySync : historySyncList) {
                his.setVersionId(historySync.getVersionId());
                if(historySync.getStatusApply() == 1) {
                    versionUse = historySync.getVersionId();
                }
            }
            idVersion = his.getVersionId();
        }
        if (showSuccessVersion) {
            FacesUtil.addSuccessMessage("Thay đổi version thành công");
            onSearch();
            showSuccessVersion = false;
        }
    }

    public void onSaveData() {

        List<Account> checkDistrict = accountRepository.findAccountsByDistrictId(district.getDistrictId());
        if ((checkDistrict.size() != 0 && district.getDistrictId() != null)) {
            FacesUtil.addErrorMessage("Không được sửa vì " + districtCopy.getName() + " đang được sử dụng");
            FacesUtil.updateView("growl");
            FacesUtil.closeDialog("dialogInsertUpdate");
            return;
        }

        district.setCode(district.getCode().trim());
        if (district.getDistrictId() == null) {
            district.setDistrictApiId(0L);
        }

        if (district.getDistrictId() == null || !district.getCode().equalsIgnoreCase(districtCopy.getCode().trim())) {
            List<District> oldCode = districtRepository.findByCode(district.getCode());
            if (oldCode.size() != 0) {
                FacesUtil.addErrorMessage("Mã quận/huyện đã tồn tại");
                FacesUtil.updateView("growl");
                return;
            }
        }

        district.setName(district.getName().trim());
        if (district.getDistrictId() == null || !district.getName().trim().equalsIgnoreCase(districtCopy.getName().trim())) {
            List<District> oldCode = districtRepository.findByNameAndProvinceId(district.getName(), district.getProvinceId());
            if (oldCode.size() != 0) {
                FacesUtil.addErrorMessage("Tên quận/huyện đã tồn tại trong tỉnh bạn chọn");
                FacesUtil.updateView("growl");
                return;
            }
        }

        if (district.getCreateBy() == null) {
            district.setCreateBy(authorizationController.getAccountDto().getAccountId());
            district.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        } else {
            district.setCreateBy(districtCopy.getCreateBy());
            district.setCreateDate(districtCopy.getCreateDate());
            district.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        }

        district.setVersionId(idVersion);
        statusApi = statusApiRepository.findById(DbConstant.STATUS_API_ID).orElse(null);
        if(statusApi.getDistrictIdFirstAdd() == 1 ) {
            districtApi = new DistrictApi();
            BeanUtils.copyProperties(district, districtApi);
            if (districtApi.getDistrictId() == null) {
                districtApi.setDistrictId(districtRepository.getDistrictIdLast() + 1L);
            }
            districtService.save(districtApi);
        } else if (statusApi.getDistrictIdFirstAdd() == 0 && district.getDistrictId() == null) {
            districtApi = new DistrictApi();
            BeanUtils.copyProperties(district, districtApi);
            districtApi.setDistrictId(150000L);
            districtService.save(districtApi);
            statusApi.setDistrictIdFirstAdd(1);
            statusApiRepository.save(statusApi);
        } else {
            districtService.save(district);
        }
        if (district.getDistrictId() == null) {
            actionSystemController().onSave("Tạo thông tin quận/huyện " + district.getName(), authorizationController.getAccountDto().getAccountId());
        } else {
            actionSystemController().onSave("Sửa thông tin quận/huyện " + district.getName(), authorizationController.getAccountDto().getAccountId());
        }
        FacesUtil.addSuccessMessage("Lưu thành công");
        FacesUtil.closeDialog("dialogInsertUpdate");
        resetAll();
        FacesUtil.updateView("growl");
        FacesUtil.updateView("searchForm");
        onSearch();
    }
    public void showVersion() {
        lazyDataModelVersion = new LazyDataModel<HistorySync>() {
            @Override
            public List<HistorySync> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                historySyncSearchDto.setPageIndex(first);
                historySyncSearchDto.setPageSize(pageSize);
                historySyncSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                historySyncSearchDto.setSortOrder(sort);
                return historySyncRepository.search(historySyncSearchDto);
            }

            @Override
            public HistorySync getRowData(String rowKey) {
                List<HistorySync> historySyncList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (HistorySync obj : historySyncList) {
                    if (obj.getHistorySyncId().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = historySyncRepository.countSearch(historySyncSearchDto).intValue();
        lazyDataModelVersion.setRowCount(count);
        FacesUtil.updateView("dlFormVersion");
    }

    public void changeVersion(HistorySync obj) {
        accountList = (List<Account>) accountRepository.findAll();
        provinceListCheck = provinceVersionRepository.findAllByVersionId(obj.getVersionId());
        districtListCheck = districtVersionRepository.findAllByVersionId(obj.getVersionId());
        communeListCheck = communeVersionRepository.findAllByVersionId(obj.getVersionId());
        for(Account account : accountList) {
            int k = 0;
            int m = 0;
            int n = 0;
            if (account.getProvinceId() != null) {
                for (ProvinceVersion pr : provinceListCheck) {
                    if (account.getProvinceId().intValue() == pr.getProvinceId().intValue()) {
                        k++;
                    }
                }
            }

            if(account.getDistrictId() != null) {
                for(DistrictVersion dv : districtListCheck) {
                    if(account.getDistrictId().intValue() == dv.getDistrictId().intValue()) {
                        m++;
                    }
                }
            }

            if(account.getCommuneId() != null) {
                for(CommuneVersion cv : communeListCheck) {
                    if(account.getCommuneId().intValue() == cv.getCommuneId().intValue())
                        n++;
                }
            }

            if(account.getProvinceId() != null && k == 0) {
                FacesUtil.addErrorMessage("Không thể áp dụng version " + (obj.getVersionId()) +" vì version này không có địa chỉ mà các tài khoản đã sử dụng");
                FacesUtil.closeDialog("dialogChangeVersion");
                FacesUtil.updateView("growl");
                return;
            }

            if(account.getDistrictId() != null && m == 0) {
                FacesUtil.addErrorMessage("Không thể áp dụng version " + (obj.getVersionId()) +" vì version này không có địa chỉ mà các tài khoản đã sử dụng");
                FacesUtil.closeDialog("dialogChangeVersion");
                FacesUtil.updateView("growl");
                return;
            }

            if(account.getCommuneId() != null && n == 0) {
                FacesUtil.addErrorMessage("Không thể áp dụng version " + (obj.getVersionId()) +" vì version này không có địa chỉ mà các tài khoản đã sử dụng");
                FacesUtil.closeDialog("dialogChangeVersion");
                FacesUtil.updateView("growl");
                return;
            }
        }
        statusVersion.setStatus(DbConstant.STATUS_VERSION_STATUS_ACTIVE);
        statusVersionRepository.save(statusVersion);
        resetAll();
        labelVersion= "Đang thay đổi version";
        FacesUtil.updateView("formTop");
        FacesUtil.closeDialog("dialogChangeVersion");
        new Thread(() -> {

            List<HistorySync> hc = (List<HistorySync>) historySyncRepository.findAll();
            for (HistorySync historySync : hc) {
                historySync.setStatusApply(0L);
            }
            apiService.uploadHistorySync(hc);
            HistorySync his = new HistorySync();
            his = (HistorySync) historySyncRepository.findAllByVersionId(obj.getVersionId());
            his.setStatusApply(1L);
            historySyncRepository.save(his);
            List<ProvinceVersion> pn = provinceVersionRepository.findAllByVersionId(versionUse);
            List<ProvinceVersion> pr = provinceVersionRepository.findAllByVersionId(obj.getVersionId());
            if (pn.size() == 0) {
                provinceListAdd = (List<Province>) provinceRepository.findAll();
                if (provinceListAdd.size() != 0) {
                    for (Province province : provinceListAdd) {
                        ProvinceVersion pv = new ProvinceVersion();
                        pv.setProvinceId(province.getProvinceId());
                        pv.setCode(province.getCode());
                        pv.setName(province.getName());
                        pv.setStatus(province.getStatus());
                        pv.setVersionId(province.getVersionId());
                        pv.setProvinceApiId(province.getProvinceApiId());
                        pv.setCreateBy(province.getCreateBy());
                        pv.setCreateDate(province.getCreateDate());
                        pv.setUpdateBy(province.getUpdateBy());
                        pv.setUpdateDate(province.getUpdateDate());
                        provinceVersionList.add(pv);
                    }
                }

                districtListAdd = (List<District>) districtRepository.findAll();
                if (districtListAdd.size() != 0) {
                    for (District dt : districtListAdd) {
                        DistrictVersion dv = new DistrictVersion();
                        dv.setDistrictId(dt.getDistrictId());
                        dv.setProvinceId(dt.getProvinceId());
                        dv.setCode(dt.getCode());
                        dv.setName(dt.getName());
                        dv.setStatus(dt.getStatus());
                        dv.setDistrictApiId(dt.getDistrictApiId());
                        dv.setVersionId(dt.getVersionId());
                        dv.setCreateBy(dt.getCreateBy());
                        dv.setCreateDate(dt.getCreateDate());
                        dv.setUpdateBy(dt.getUpdateBy());
                        dv.setUpdateDate(dt.getUpdateDate());
                        districtVersionList.add(dv);
                    }
                }

                communeListAdd = (List<Commune>) communeRepository.findAll();
                if (communeListAdd.size() != 0) {
                    for (Commune cm : communeListAdd) {
                        CommuneVersion cv = new CommuneVersion();
                        cv.setCommuneId(cm.getCommuneId());
                        cv.setProvinceId(cm.getProvinceId());
                        cv.setDistrictId(cm.getDistrictId());
                        cv.setCode(cm.getCode());
                        cv.setName(cm.getName());
                        cv.setStatus(cm.getStatus());
                        cv.setVersionId(cm.getVersionId());
                        cv.setCreateDate(cm.getCreateDate());
                        cv.setCreateBy(cm.getCreateBy());
                        cv.setUpdateBy(cm.getUpdateBy());
                        cv.setUpdateDate(cm.getUpdateDate());
                        communeVersionList.add(cv);
                    }
                }

                apiService.uploadProvinceVersion(provinceVersionList);
                apiService.uploadDistrictVersion(districtVersionList);
                apiService.uploadCommuneVersion(communeVersionList);
            }
            provinceRepository.deleteAllRecords();
            districtRepository.deleteAllRecords();
            communeRepository.deleteAllRecords();
            List<ProvinceApi> provinceListChange = new ArrayList<>();
            provinceVersionRepository.deleteAllByVersionId(obj.getVersionId());
            for (ProvinceVersion provinceVersion : pr) {
                ProvinceApi pv = new ProvinceApi();
                pv.setProvinceId(provinceVersion.getProvinceId());
                pv.setCode(provinceVersion.getCode());
                pv.setName(provinceVersion.getName());
                pv.setStatus(provinceVersion.getStatus());
                pv.setCreateDate(provinceVersion.getCreateDate());
                pv.setCreateBy(provinceVersion.getCreateBy());
                pv.setUpdateDate(provinceVersion.getUpdateDate());
                pv.setUpdateBy(provinceVersion.getUpdateBy());
                pv.setProvinceApiId(provinceVersion.getProvinceApiId());
                pv.setVersionId(provinceVersion.getVersionId());
                provinceListChange.add(pv);
            }
            List<DistrictVersion> dr = districtVersionRepository.findAllByVersionId(obj.getVersionId());
            districtVersionRepository.deleteAllByVersionId(obj.getVersionId());
            List<DistrictApi> districtListChange = new ArrayList<>();
            for (DistrictVersion districtVersion : dr) {
                DistrictApi dt = new DistrictApi();
                dt.setDistrictId(districtVersion.getDistrictId());
                dt.setProvinceId(districtVersion.getProvinceId());
                dt.setCode(districtVersion.getCode());
                dt.setName(districtVersion.getName());
                dt.setStatus(districtVersion.getStatus());
                dt.setCreateDate(districtVersion.getCreateDate());
                dt.setCreateBy(districtVersion.getCreateBy());
                dt.setUpdateDate(districtVersion.getUpdateDate());
                dt.setUpdateBy(districtVersion.getUpdateBy());
                dt.setDistrictApiId(districtVersion.getDistrictApiId());
                dt.setVersionId(districtVersion.getVersionId());
                districtListChange.add(dt);
            }

            List<CommuneVersion> cv = communeVersionRepository.findAllByVersionId(obj.getVersionId());
            communeVersionRepository.deleteAllByVersionId(obj.getVersionId());
            List<Commune> communeListChange = new ArrayList<>();
            for (CommuneVersion communeVersion : cv) {
                Commune cm = new Commune();
                cm.setCommuneId(communeVersion.getCommuneId());
                cm.setDistrictId(communeVersion.getDistrictId());
                cm.setProvinceId(communeVersion.getProvinceId());
                cm.setCode(communeVersion.getCode());
                cm.setName(communeVersion.getName());
                cm.setStatus(communeVersion.getStatus());
                cm.setCreateDate(communeVersion.getCreateDate());
                cm.setCreateBy(communeVersion.getCreateBy());
                cm.setUpdateDate(communeVersion.getUpdateDate());
                cm.setUpdateBy(communeVersion.getUpdateBy());
                cm.setVersionId(communeVersion.getVersionId());
                communeListChange.add(cm);
            }
            apiService.uploadProvinceComeBack(provinceListChange);
            apiService.uploadDistrictComeBack(districtListChange);
            apiService.uploadCommuneComeBack(communeListChange);
            statusVersion.setStatus(DbConstant.STATUS_VERSION_STATUS_INACTIVE);
            statusVersionRepository.save(statusVersion);
            labelVersion = "Quản lý version";
            showSuccessVersion = true;
            actionSystemController().onSave("Thay đổi version tỉnh, quận, huyện từ version " +versionUse+ " sang version "+obj.getVersionId(), authorizationController.getAccountDto().getAccountId());
            SocketServer.sendToClients(Arrays.asList(authorizationController.getAccountDto().getAccountId()),
                    new UpdateScopeResponseDto("updateScreenDistrict", null, null));

        }).start();
    }

    public void showUpdatePopup(District obj) {
        districtCopy = new DistrictDto();
        BeanUtils.copyProperties(obj, district);
        BeanUtils.copyProperties(obj, districtCopy);
    }


    public void resetDialog() {
        district = new District();
    }

    public void onDelete(District deleteObj) {
        try {
            List<Account> checkDistrict = accountRepository.findAccountsByDistrictId(deleteObj.getDistrictId());
            if (checkDistrict.size() == 0) {
                districtService.deleteDistrictByDistrictId(deleteObj.getDistrictId());
                communeService.deleteCommunesByDistrictId(deleteObj.getDistrictId());
                actionSystemController().onSave("Xóa thông tin " + deleteObj.getName(), authorizationController.getAccountDto().getAccountId());
                FacesUtil.addSuccessMessage("Xóa thành công");
                onSearch();
            } else {
                FacesUtil.addErrorMessage("Xóa thất bại vì " + deleteObj.getName() + " đã được sử dụng để tạo tài khoản");
            }

        } catch (Exception e) {
            FacesUtil.addErrorMessage("Xóa thất bại");
        }
    }

    public void dataSynchronization() {
        statusApi = statusApiRepository.findById(DbConstant.STATUS_API_ID).orElse(null);
        if (statusApi.getStatus().equals(DbConstant.STATUS_API_STATUS_ACTIVE)) {
            FacesUtil.addErrorMessage("Hệ thống đang được đồng bộ từ một thiết bị khác, vui lòng thử lại sau");
            FacesUtil.updateView("growl");
            FacesUtil.updateView("formTop");
            return;
        }
        statusApi.setStatus(DbConstant.STATUS_API_STATUS_ACTIVE);
        statusApiRepository.save(statusApi);
        labelSynchronized ="Đang đồng bộ dữ liệu";
        resetAll();
        FacesUtil.updateView("formTop");
        new Thread(() -> {
            try {
                getTokenApiController.rest();
                statusApi.setStatus(DbConstant.STATUS_API_STATUS_INACTIVE);
                statusApiRepository.save(statusApi);
                labelSynchronized ="Đồng bộ dữ liệu";
                actionSystemController().onSave("Đồng bộ dữ liệu tỉnh, quận, huyện ", authorizationController.getAccountDto().getAccountId());
                SocketServer.sendToClients(Arrays.asList(authorizationController.getAccountDto().getAccountId()),
                        new UpdateScopeResponseDto("updateScreenDistrict", null, null));
                showSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<DistrictDto>() {

            @Override
            public List<DistrictDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                districtSearchDto.setPageIndex(first);
                districtSearchDto.setPageSize(pageSize);
                districtSearchDto.setSortField(sortField);
                String sort;
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                districtSearchDto.setSortOrder(sort);
                return districtRepository.search(districtSearchDto);
            }

            @Override
            public DistrictDto getRowData(String rowKey) {
                List<DistrictDto> districtList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (DistrictDto obj : districtList) {
                    if (obj.getCode().equals(value) || obj.getName().equals(value)) {
                        return obj;
                    }

                }
                return null;
            }
        };
        int count = districtRepository.countSearch(districtSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView(" ");
    }

    @Override
    protected EScope getMenuId() {
        return EScope.DISTRICT;
    }


}

