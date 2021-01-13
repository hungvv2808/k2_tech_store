package vn.compedia.website.auction.controller.admin.base;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.api.GetApiPDCController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.dto.api.HistorySyncSearchDto;
import vn.compedia.website.auction.dto.base.ProvinceSearchDto;
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
@Getter
@Setter
@Scope(value = "session")
public class ProvinceController extends BaseController {

    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private GetApiPDCController getTokenApiController;

    @Autowired
    protected DistrictRepository districtRepository;
    @Autowired
    protected StatusApiRepository statusApiRepository;
    @Autowired
    protected StatusVersionRepository statusVersionRepository;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private DistrictService districtService;
    @Autowired
    private CommuneService communeService;
    @Autowired
    protected ApiService apiService;
    @Autowired
    protected HistorySyncService historySyncService;
    @Autowired
    protected CommuneRepository communeRepository;
    @Autowired
    protected ProvinceRepository provinceRepository;
    @Autowired
    protected HistorySyncRepository historySyncRepository;
    @Autowired
    protected ProvinceVersionRepository provinceVersionRepository;
    @Autowired
    protected DistrictVersionRepository districtVersionRepository;
    @Autowired
    protected  CommuneVersionRepository communeVersionRepository;

    private Province province;
    private ProvinceApi provinceApi;
    private LazyDataModel<Province> lazyDataModel;
    private LazyDataModel<HistorySync> lazyDataModelVersion;
    private ProvinceSearchDto provinceSearchDto;
    private HistorySyncSearchDto historySyncSearchDto;
    private List<SelectItem> provinceList;
    private Province provinceCopy;
    private Province provinceCheck;
    private String labelSynchronized;
    private String labelVersion;
    private List<Account> account;
    private List<Account> accountCheck;
    private StatusApi statusApi;
    private StatusVersion statusVersion;
    private boolean showSuccess;
    private boolean checkNoteLabel = true;
    private List<HistorySync> historySyncList = new ArrayList<>();
    private Long idVersion;
    private HistorySync historySync;
    private Long versionUse = 0L;
    private boolean showSuccessVersion;
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
    private Account account1;
    private Long provinceId;
    private Long provinceIdOfIssue;



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
            if(statusVersion == null || DbConstant.STATUS_VERSION_STATUS_INACTIVE == statusVersion.getStatus()) {
                labelVersion = "Quản lý version";
            } else {
                labelVersion = "Đang thay đổi version";
            }

            resetAll();
        }
    }

    public void resetAll() {
        account1 = new Account();
        provinceList = new ArrayList<>();
        province = new Province();
        provinceSearchDto = new ProvinceSearchDto();
        historySyncSearchDto = new HistorySyncSearchDto();
        actionSystemController.resetAll();
        onSearch();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");

        if (showSuccess) {
            FacesUtil.addSuccessMessage("Đồng bộ thành công");
            onSearch();
            showSuccess = false;
        }
        versionUse = 0L;
        List<HistorySync> hsList = new ArrayList<>();
        hsList = (List<HistorySync>) historySyncRepository.findAll();
        List<Province> pro = (List<Province>) provinceRepository.findAll();
        if (hsList.size() != 0) {
            for (HistorySync hs : hsList) {
                if (hs.getStatusApply() == 1) {
                    versionUse = hs.getVersionId();
                }
            }
        }
        if(pro.size() == 0 && hsList.size() == 0) {
            statusApi = statusApiRepository.findById(DbConstant.STATUS_API_ID).orElse(null);
            statusApi.setProvinceIdFirstAdd(0);
            statusApi.setDistrictIdFirstAdd(0);
            statusApi.setCommuneIdFirstAdd(0);
            statusApiRepository.save(statusApi);
        }
        if (showSuccessVersion) {
            FacesUtil.addSuccessMessage("Thay đổi version thành công");
            onSearch();
            showSuccessVersion = false;
        }
    }


    public void onSaveData() {
        if(province.getProvinceId() != null) {
            if (accountRepository.existsAccountsByProvinceIdOrProvinceIdOfIssue(province.getProvinceId(),province.getProvinceId())) {
                FacesUtil.addErrorMessage("Không được sửa vì " + provinceCopy.getName() + " đang được sử dụng");
                FacesUtil.updateView("growl");
                FacesUtil.closeDialog("dialogInsertUpdate");
                return;
            }
        }
            List<Province> pro = (List<Province>) provinceRepository.findAll();
            List<HistorySync> history = (List<HistorySync>) historySyncRepository.findAll();
            if(pro.size() == 0 && history.size() == 0) {
                FacesUtil.addErrorMessage("Bạn cần phải đồng bộ dữ liệu trước khi thêm tỉnh thành lần đầu tiên");
                FacesUtil.updateView("growl");
                return;
            }
            province.setCode(province.getCode().trim());
            if(province.getProvinceId() == null) {
                province.setProvinceApiId(0L);
            }
            if (province.getProvinceId() == null || !province.getCode().equalsIgnoreCase(provinceCopy.getCode().trim())) {
                checkNoteLabel = false;
                List<Province>  oldCode = provinceRepository.findByCode(province.getCode());
                if (oldCode.size() != 0) {
                    FacesUtil.addErrorMessage("Mã tỉnh/thành phố đã tồn tại");
                    FacesUtil.updateView("growl");
                    return;
                }
            }

            province.setName(province.getName().trim());
            if (province.getProvinceId() == null || !province.getName().trim().equalsIgnoreCase(provinceCopy.getName().trim())) {
                List<Province> oldCode = provinceRepository.findByName(province.getName());
                if (oldCode.size() != 0) {
                    FacesUtil.addErrorMessage("Tên tỉnh/thành phố đã tồn tại");
                    FacesUtil.updateView("growl");
                    return;
                }
            }

      /*  if (province.getProvinceId() != null) {
            accountCheck = new ArrayList<>();
            accountCheck = accountRepository.findByProvinceIdOfIssue(province.getProvinceId());
            provinceCheck = provinceRepository.findByProvinceId(province.getProvinceId());
            if (accountCheck.size() != 0) {
                if (!(province.getCode().equals(provinceCheck.getCode())) || !(province.getName().equals(provinceCheck.getName())) || !(province.getStatus().equals(provinceCheck.getStatus()))) {
                    FacesUtil.addErrorMessage("Lưu thất bại vì tỉnh thành đã được sử dụng để tạo tài khoản");
                    FacesUtil.updateView("growl");
                    return;
                }
            }
        }*/


            if (province.getCreateBy() == null) {
                province.setCreateBy(authorizationController.getAccountDto().getAccountId());
                province.setUpdateBy(authorizationController.getAccountDto().getAccountId());
            } else {
                province.setCreateBy(provinceCopy.getCreateBy());
                province.setCreateDate(provinceCopy.getCreateDate());
                province.setUpdateBy(authorizationController.getAccountDto().getAccountId());
            }
            historySyncList = (List<HistorySync>) historySyncRepository.findAll();
            if (historySyncList.size() == 0) {
                idVersion = 1L;
            } else {
                HistorySync his = new HistorySync();
                for (HistorySync historySync : historySyncList) {
                    his.setVersionId(historySync.getVersionId());
                }
                idVersion = his.getVersionId();
            }
            province.setVersionId(idVersion);

            statusApi = statusApiRepository.findById(DbConstant.STATUS_API_ID).orElse(null);
            if(statusApi.getProvinceIdFirstAdd() == 1 ) {
                provinceApi = new ProvinceApi();
                BeanUtils.copyProperties(province,provinceApi);
                if(provinceApi.getProvinceId() == null) {
                    provinceApi.setProvinceId(provinceRepository.getProvinceIdLast() + 1L);
                }
                provinceService.save(provinceApi);
            } else if(statusApi.getProvinceIdFirstAdd() == 0 && province.getProvinceId() == null){
                provinceApi = new ProvinceApi();
                BeanUtils.copyProperties(province,provinceApi);
                provinceApi.setProvinceId(50000L);
                provinceService.save(provinceApi);
                statusApi.setProvinceIdFirstAdd(1);
                statusApiRepository.save(statusApi);
            } else {
                provinceService.save(province);
            }

            historySyncList = (List<HistorySync>) historySyncRepository.findAll();
            if (historySyncList.size() == 0) {
                historySync = new HistorySync();
                Date now = new Date();
                historySync.setVersionId(idVersion);
                historySync.setCreateDate(now);
                historySync.setStatusApply(1L);
                historySyncService.save(historySync);
            }
            if (province.getProvinceId() == null) {
                actionSystemController.onSave("Tạo thông tin tỉnh thành phố " + province.getName(), authorizationController.getAccountDto().getAccountId());
            } else {
                actionSystemController.onSave("Sửa thông tin tỉnh thành phố " + province.getName(), authorizationController.getAccountDto().getAccountId());
            }
            FacesUtil.addSuccessMessage("Lưu thành công");
            FacesUtil.closeDialog("dialogInsertUpdate");
            resetAll();
            FacesUtil.updateView("growl");
            FacesUtil.updateView("searchForm");
            onSearch();



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
                    if (account.getProvinceId().intValue() == pr.getProvinceId().intValue())
                        k++;
                }
            }

            if(account.getDistrictId() != null) {
                for(DistrictVersion dv : districtListCheck) {
                    if(account.getDistrictId().intValue() == dv.getDistrictId().intValue())
                        m++;
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
            actionSystemController.onSave("Thay đổi version tỉnh, quận, huyện từ version " +versionUse+ " sang version "+obj.getVersionId(), authorizationController.getAccountDto().getAccountId());
            SocketServer.sendToClients(Arrays.asList(authorizationController.getAccountDto().getAccountId()),
                    new UpdateScopeResponseDto("updateScreen", null, null));
            showSuccessVersion = true;
        }).start();


    }

    public void clearData() {
        if (province.getProvinceId() != null && province.getProvinceId() > 0) {
            BeanUtils.copyProperties(provinceCopy, province);
        } else
            resetDialog();
    }

    public void showUpdatePopup(Province obj) {
        provinceCopy = new Province();
        BeanUtils.copyProperties(obj, province);
        BeanUtils.copyProperties(obj, provinceCopy);
    }


    public void resetDialog() {
        province = new Province();
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
        resetAll();
        labelSynchronized = "Đang đồng bộ dữ liệu";
        FacesUtil.updateView("formTop");
        new Thread(() -> {
            try {
                getTokenApiController.rest();
                statusApi.setStatus(DbConstant.STATUS_API_STATUS_INACTIVE);
                statusApiRepository.save(statusApi);
                labelSynchronized = "Đồng bộ dữ liệu";
                actionSystemController.onSave("Đồng bộ dữ liệu tỉnh, quận, huyện ", authorizationController.getAccountDto().getAccountId());
                SocketServer.sendToClients(Arrays.asList(authorizationController.getAccountDto().getAccountId()),
                        new UpdateScopeResponseDto("updateScreen", null, null));
                showSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void onDelete(Province deleteObj) {
        try {
            account = new ArrayList<>();
            account = accountRepository.findByProvinceIdOfIssue(deleteObj.getProvinceId());
            List<Account> accounts = accountRepository.findAccountsByProvinceId(deleteObj.getProvinceId());
            if (account.size() == 0 && accounts.size() == 0) {
                communeService.deleteCommune(deleteObj.getProvinceId());
                districtService.deleteDistrict(deleteObj.getProvinceId());
                provinceService.deleteProvince(deleteObj.getProvinceId());
                actionSystemController.onSave("Xóa thông tin " + deleteObj.getName(), authorizationController.getAccountDto().getAccountId());
                FacesUtil.addSuccessMessage("Xóa thành công");
                onSearch();
            } else {
                FacesUtil.addErrorMessage("Xóa thất bại vì " + deleteObj.getName() + " đã được sử dụng để tạo tài khoản");
            }

        } catch (Exception e) {
            FacesUtil.addErrorMessage("Xóa thất bại");
        }
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

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<Province>() {

            @Override
            public List<Province> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                provinceSearchDto.setPageIndex(first);
                provinceSearchDto.setPageSize(pageSize);
                provinceSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                provinceSearchDto.setSortOrder(sort);
                return provinceRepository.search(provinceSearchDto);
            }

            @Override
            public Province getRowData(String rowKey) {
                List<Province> provinceList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (Province obj : provinceList) {
                    if (obj.getCode().equals(value) || obj.getName().equals(value) || obj.getStatus().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = provinceRepository.countSearch(provinceSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }

    public boolean checkEditButton(Long provinceId, Long provinceIdOfIssue) {
        List<Long> listProviceId = accountRepository.findProvinceId();
        List<Long> listProvinceIdOfIssue = accountRepository.findProvinceIdOfIssue();
        if (listProviceId.contains(provinceId)) {
            return true;
        }
        if (listProvinceIdOfIssue.contains(provinceIdOfIssue)){
            return true;
        }
        return false;
    }

    @Override
    protected EScope getMenuId() {
        return EScope.PROVINCE;
    }
}

