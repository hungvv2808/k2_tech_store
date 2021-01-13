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
import vn.compedia.website.auction.dto.base.CommuneDto;
import vn.compedia.website.auction.dto.base.CommuneSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.service.ApiService;
import vn.compedia.website.auction.service.CommuneService;
import vn.compedia.website.auction.service.HistorySyncService;
import vn.compedia.website.auction.system.socket.SocketServer;
import vn.compedia.website.auction.system.socket.dto.UpdateScopeResponseDto;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private GetApiPDCController getTokenApiController;
    @Inject
    private CommonController commonController;

    @Autowired
    protected CommuneRepository communeRepository;
    @Autowired
    protected DistrictRepository districtRepository;
    @Autowired
    private CommuneService communeService;
    @Autowired
    protected StatusApiRepository statusApiRepository;
    @Autowired
    protected ProvinceRepository provinceRepository;
    @Autowired
    protected HistorySyncRepository historySyncRepository;
    @Autowired
    protected StatusVersionRepository statusVersionRepository;
    @Autowired
    protected ApiService apiService;
    @Autowired
    protected HistorySyncService historySyncService;

    @Autowired
    protected ProvinceVersionRepository provinceVersionRepository;
    @Autowired
    protected DistrictVersionRepository districtVersionRepository;
    @Autowired
    protected CommuneVersionRepository communeVersionRepository;
    @Autowired
    protected AccountRepository accountRepository;
    private CommuneApi communeApi;
    private Commune commune;
    private LazyDataModel<CommuneDto> lazyDataModel;
    private LazyDataModel<HistorySync> lazyDataModelVersion;
    private HistorySyncSearchDto historySyncSearchDto;
    private String labelVersion;
    private StatusVersion statusVersion;
    private CommuneSearchDto communeSearchDto;
    private List<SelectItem> districtList;
    private Commune communeCopy;
    private List<SelectItem> provinceList;
    private String labelSynchronized;
    private boolean showSuccess;
    private StatusApi statusApi;
    private List<HistorySync> historySyncList = new ArrayList<>();
    private Long idVersion;
    private HistorySync historySync;
    private Long versionUse = 0L;
    private boolean showSuccessVersion;
    private List<Account> accountList = new ArrayList<>();

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
            if (statusApi == null || DbConstant.STATUS_API_STATUS_INACTIVE == statusApi.getStatus()) {
                labelSynchronized = "Đồng bộ dữ liệu";
            } else {
                labelSynchronized = "Đang đồng bộ dữ liệu";
            }
            statusVersion = statusVersionRepository.findById(DbConstant.STATUS_VERSION_ID).orElse(null);
            if (statusVersion == null || DbConstant.STATUS_VERSION_STATUS_INACTIVE == statusVersion.getStatus()) {
                labelVersion = "Quản lý version";
            } else {
                labelVersion = "Đang thay đổi version";
            }

            resetAll();
        }
    }

    public void changeVersion(HistorySync obj) {
        accountList = (List<Account>) accountRepository.findAll();
        provinceListCheck = provinceVersionRepository.findAllByVersionId(obj.getVersionId());
        districtListCheck = districtVersionRepository.findAllByVersionId(obj.getVersionId());
        communeListCheck = communeVersionRepository.findAllByVersionId(obj.getVersionId());
        for (Account account : accountList) {
            int k = 0;
            int m = 0;
            int n = 0;
            if (account.getProvinceId() != null) {
                for (ProvinceVersion pr : provinceListCheck) {
                    if (account.getProvinceId().intValue() == pr.getProvinceId().intValue())
                        k++;
                }
            }

            if (account.getDistrictId() != null) {
                for (DistrictVersion dv : districtListCheck) {
                    if (account.getDistrictId().intValue() == dv.getDistrictId().intValue())
                        m++;
                }
            }

            if (account.getCommuneId() != null) {
                for (CommuneVersion cv : communeListCheck) {
                    if (account.getCommuneId().intValue() == cv.getCommuneId().intValue())
                        n++;
                }
            }

            if (account.getProvinceId() != null && k == 0) {
                FacesUtil.addErrorMessage("Không thể áp dụng version " + (obj.getVersionId()) +" vì version này không có địa chỉ mà các tài khoản đã sử dụng");
                FacesUtil.closeDialog("dialogChangeVersion");
                FacesUtil.updateView("growl");
                return;
            }

            if (account.getDistrictId() != null && m == 0) {
                FacesUtil.addErrorMessage("Không thể áp dụng version " + (obj.getVersionId()) +" vì version này không có địa chỉ mà các tài khoản đã sử dụng");
                FacesUtil.closeDialog("dialogChangeVersion");
                FacesUtil.updateView("growl");
                return;
            }

            if (account.getCommuneId() != null && n == 0) {
                FacesUtil.addErrorMessage("Không thể áp dụng version " + (obj.getVersionId()) +" vì version này không có địa chỉ mà các tài khoản đã sử dụng");
                FacesUtil.closeDialog("dialogChangeVersion");
                FacesUtil.updateView("growl");
                return;
            }
        }
        statusVersion.setStatus(DbConstant.STATUS_VERSION_STATUS_ACTIVE);
        statusVersionRepository.save(statusVersion);
        resetAll();
        labelVersion = "Đang thay đổi version";
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
            actionSystemController().onSave("Thay đổi version tỉnh, quận, huyện từ version " + versionUse + " sang version " + obj.getVersionId(), authorizationController.getAccountDto().getAccountId());
            SocketServer.sendToClients(Arrays.asList(authorizationController.getAccountDto().getAccountId()),
                    new UpdateScopeResponseDto("updateScreenCommune", null, null));

        }).start();
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

    public void resetAll() {
        commune = new Commune();
        communeCopy = new Commune();
        communeSearchDto = new CommuneSearchDto();
        districtList = new ArrayList<>();
        historySyncSearchDto = new HistorySyncSearchDto();
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
        historySyncList = (List<HistorySync>) historySyncRepository.findAll();
        if (historySyncList.size() == 0) {
            idVersion = 1L;
        }
        else {
            HistorySync his = new HistorySync();
            for (HistorySync historySync : historySyncList) {
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

        statusApi = statusApiRepository.findById(DbConstant.STATUS_API_ID).orElse(null);
        if (statusApi.getCommuneIdFirstAdd() == 1) {
            communeApi = new CommuneApi();
            BeanUtils.copyProperties(commune, communeApi);
            if(communeApi.getCommuneId() == null) {
                communeApi.setCommuneId(communeRepository.getCommuneIdLast() + 1L);
            }
            communeService.save(communeApi);
        } else if(statusApi.getCommuneIdFirstAdd() == 0 && commune.getCommuneId() == null){
            communeApi = new CommuneApi();
            BeanUtils.copyProperties(commune, communeApi);
            communeApi.setCommuneId(100000L);
            communeService.save(communeApi);
            statusApi.setCommuneIdFirstAdd(1);
            statusApiRepository.save(statusApi);
        } else {
            communeService.save(commune);
        }

        if (commune.getCommuneId() == null) {
            actionSystemController().onSave("Tạo thông tin phường/xã " + commune.getName(), authorizationController.getAccountDto().getAccountId());
        } else {
            actionSystemController().onSave("Sửa thông tin phường/xã " + commune.getName(), authorizationController.getAccountDto().getAccountId());
        }
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
        labelSynchronized = "Đang đồng bộ dữ liệu";
        resetAll();
        FacesUtil.updateView("formTop");
        new Thread(() -> {
            try {
                getTokenApiController.rest();
                statusApi.setStatus(DbConstant.STATUS_API_STATUS_INACTIVE);
                statusApiRepository.save(statusApi);
                labelSynchronized = "Đồng bộ dữ liệu";
                actionSystemController().onSave("Đồng bộ dữ liệu tỉnh, quận, huyện ", authorizationController.getAccountDto().getAccountId());
                SocketServer.sendToClients(Arrays.asList(authorizationController.getAccountDto().getAccountId()),
                        new UpdateScopeResponseDto("updateScreenCommune", null, null));
                showSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void onDelete(Commune deleteObj) {
        try {
            List<Account> checkDistrict = accountRepository.findAccountsByCommuneId(deleteObj.getCommuneId());
            if (checkDistrict.size() == 0) {
                communeService.deleteCommunebyCommuneId(deleteObj.getCommuneId());
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


