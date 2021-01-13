package vn.compedia.website.auction.controller.admin.api;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.service.ApiService;
import vn.compedia.website.auction.service.HistorySyncService;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.util.PropertiesUtil;
import vn.compedia.website.auction.xml.DvhcElement;
import vn.compedia.website.auction.xml.DvhcRoot;
import vn.compedia.website.auction.xml.DvhcXmlParseUtil;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@Log4j2
@Named
@Getter
@Setter
@Scope(value = "session")
public class GetApiPDCController implements Serializable {

    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ApiBaseController apiBaseController;
    @Autowired
    protected ProvinceRepository provinceRepository;
    @Autowired
    protected DistrictRepository districtRepository;
    @Autowired
    protected CommuneRepository communeRepository;
    @Autowired
    protected StatusApiRepository statusApiRepository;
    @Autowired
    protected HistorySyncRepository historySyncRepository;
    @Autowired
    protected ProvinceVersionRepository provinceVersionRepository;
    @Autowired
    protected DistrictVersionRepository districtVersionRepository;
    @Autowired
    protected CommuneVersionRepository communeVersionRepository;
    @Autowired
    protected HistorySyncService historySyncService;
    @Autowired
    protected ApiService apiService;

    private StatusApi statusApi;
    private Integer dem = 0;
    private List<HistorySync> historySyncList = new ArrayList<>();
    private List<ProvinceVersion> provinceVersionList = new ArrayList<>();
    private List<DistrictVersion> districtVersionList = new ArrayList<>();
    private List<CommuneVersion> communeVersionList = new ArrayList<>();
    private Long idVersion;
    private HistorySync historySync;

    public void rest() {
        dem = 0;
        historySyncList = (List<HistorySync>) historySyncRepository.findAll();
        if (historySyncList.size() == 0) {
            idVersion = 0L;
        } else {
            HistorySync his = new HistorySync();
            for (HistorySync historySync : historySyncList) {
                his.setVersionId(historySync.getVersionId());
            }
            idVersion = his.getVersionId();
        }

        // List lấy từ Db
        List<Province> provinceListDb = (List<Province>) provinceRepository.findAll();
        if(provinceListDb.size() != 0) {
            for (Province province : provinceListDb) {
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

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/DanhSachDonViHanhChinh\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/donvihanhchinh", replaces);

            DvhcRoot dvhcRootElements = DvhcXmlParseUtil.convertStringToXml(rs);
            List<ProvinceApi> provinceListApi = new ArrayList<>();
            Date now = new Date();

            // List lấy từ API
            for (DvhcElement dvhcElementProvince : dvhcRootElements.getElements()) {
                if (dvhcElementProvince.getCapId().equals(1L) || dvhcElementProvince.getCapId().equals(4L) && (Long.parseLong(dvhcElementProvince.getMa()) < 1000L)) {
                    int i = 0;

                    for (Province provinceCheck : provinceListDb) {
                        if (dvhcElementProvince.getTen().equalsIgnoreCase(provinceCheck.getName()) && (dvhcElementProvince.getMa().equals(provinceCheck.getCode()))) {
                            provinceCheck.setProvinceApiId(dvhcElementProvince.getId());
                            provinceCheck.setVersionId(idVersion);
                            i++;
                        }
                        if (i != 0) {
                            break;
                        }
                    }
                    if (i == 0) {
                        int k = 0;
                        for (Province provinceCheck : provinceListDb) {
                            if (provinceCheck.getCode().equalsIgnoreCase(dvhcElementProvince.getMa()) || provinceCheck.getProvinceId().equals(dvhcElementProvince.getId()) ) {
                                provinceCheck.setCode(dvhcElementProvince.getMa());
                                provinceCheck.setName(dvhcElementProvince.getTen());
                                provinceCheck.setProvinceApiId(dvhcElementProvince.getId());
                                provinceCheck.setVersionId(idVersion);
                                dem++;
                                k++;
                                if (k != 0) {
                                    break;
                                }
                            }
                        }
                        if (k == 0) {
                            ProvinceApi provinceAdd = new ProvinceApi(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(),dvhcElementProvince.getId() ,dvhcElementProvince.getMa(), dvhcElementProvince.getTen(), true, dvhcElementProvince.getId(), idVersion);
                            provinceListApi.add(provinceAdd);
                        }
                    }
                }
            }

            // add version id
            for (Province pro : provinceListDb) {
                pro.setVersionId(idVersion);
            }
            apiService.uploadProvinceDb(provinceListDb);
            if (provinceListApi.size() != 0) {
                dem++;
            }
            apiService.uploadProvince(provinceListApi);
            saveDistrict();
        } catch (IOException e) {
            e.printStackTrace();
            FacesUtil.addErrorMessage("Đồng bộ thất bại");
        }

    }

    // phai tao ham moi de lay id cua tinh sau khi thuc hien save
    public void saveDistrict() {
        // List lấy từ Db
        List<Province> provinceListDb = (List<Province>) provinceRepository.findAll();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/DanhSachDonViHanhChinh\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/donvihanhchinh", replaces);

            DvhcRoot dvhcRootElements = DvhcXmlParseUtil.convertStringToXml(rs);
            List<DistrictApi> districtListApi = new ArrayList<>();
            Date now = new Date();

            // List lấy từ API
            List<District> districtListAlldb = (List<District>) districtRepository.findAll();
            if(districtListAlldb.size() != 0) {
                for (District dt : districtListAlldb) {
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
            for (DvhcElement dvhcElementProvince : dvhcRootElements.getElements()) {
                if (dvhcElementProvince.getCapId().equals(1L) || dvhcElementProvince.getCapId().equals(4L) && (Long.parseLong(dvhcElementProvince.getMa()) < 1000L)) {
                    // save huyen
                    for (DvhcElement dvhcElementDistrict : dvhcRootElements.getElements()) {
                        int m = 0;
                        int z = 0;
                        if ((dvhcElementDistrict.getCapId().equals(2L) ||dvhcElementDistrict.getCapId().equals(8L) || dvhcElementDistrict.getCapId().equals(5L) || dvhcElementDistrict.getCapId().equals(9L)) && dvhcElementProvince.getId().equals(dvhcElementDistrict.getChaId())) {
                            int check = 0;
                            for (DvhcElement dvhcElementDistrictCoincide : dvhcRootElements.getElements()) {
                                // xu ly truong hop xa duoc len thi tran thi id cua thi tran se lon hon id xa
                                if ((dvhcElementDistrictCoincide.getCapId().equals(2L) ||dvhcElementDistrict.getCapId().equals(8L) || dvhcElementDistrict.getCapId().equals(5L) || dvhcElementDistrict.getCapId().equals(9L)) && dvhcElementProvince.getId().equals(dvhcElementDistrictCoincide.getChaId())) {
                                    if (dvhcElementDistrict.getMa().equals(dvhcElementDistrictCoincide.getMa())) {
                                        check = check + 1;
                                        if (dvhcElementDistrict.getId() > dvhcElementDistrictCoincide.getId()) {
                                            for (District districtCheck : districtListAlldb) {
                                                if (dvhcElementDistrict.getTen().equalsIgnoreCase(districtCheck.getName()) && (dvhcElementDistrict.getMa().equalsIgnoreCase(districtCheck.getCode()))) {
                                                    districtCheck.setDistrictApiId(dvhcElementDistrict.getId());
                                                    districtCheck.setVersionId(idVersion);
                                                    z++;
                                                }
                                                if (z != 0) {
                                                    break;
                                                }
                                            }
                                            int n = 0;
                                            if (z == 0) {
                                                for (District districtCheck : districtListAlldb) {
                                                    if (districtCheck.getCode().equalsIgnoreCase(dvhcElementDistrict.getMa()) || districtCheck.getDistrictId().equals(dvhcElementDistrict.getId())) {
                                                        districtCheck.setName(dvhcElementDistrict.getTen());
                                                        districtCheck.setDistrictApiId(dvhcElementDistrict.getId());
                                                        districtCheck.setCode(dvhcElementDistrict.getMa());
                                                        districtCheck.setVersionId(idVersion);
                                                        dem++;
                                                        n++;
                                                    }
                                                    if (n != 0) {
                                                        break;
                                                    }
                                                }
                                            }
                                            if (n == 0 && z == 0) {
                                                for (Province province : provinceListDb) {
                                                    if (province.getProvinceApiId().equals(dvhcElementDistrict.getChaId())) {
                                                        DistrictApi districtAdd = new DistrictApi(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(),dvhcElementDistrict.getId(), province.getProvinceId(), dvhcElementDistrict.getMa(), dvhcElementDistrict.getTen(), true, dvhcElementDistrict.getId(), idVersion);
                                                        districtListApi.add(districtAdd);
                                                        dem++;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (check == 1) {
                                for (District districtCheck : districtListAlldb) {
                                    if (dvhcElementDistrict.getTen().equalsIgnoreCase(districtCheck.getName()) && (dvhcElementDistrict.getMa().equalsIgnoreCase(districtCheck.getCode()))) {
                                        districtCheck.setDistrictApiId(dvhcElementDistrict.getId());
                                        districtCheck.setVersionId(idVersion);

                                        m++;
                                    }
                                    if (m != 0) {
                                        break;
                                    }
                                }
                                int n = 0;
                                if (m == 0) {
                                    for (District districtCheck : districtListAlldb) {
                                        if (districtCheck.getCode().equalsIgnoreCase(dvhcElementDistrict.getMa()) || districtCheck.getDistrictId().equals(dvhcElementDistrict.getId())) {
                                            districtCheck.setName(dvhcElementDistrict.getTen());
                                            districtCheck.setCode(dvhcElementDistrict.getMa());
                                            districtCheck.setDistrictApiId(dvhcElementDistrict.getId());
                                            districtCheck.setVersionId(idVersion);
                                            dem++;
                                            n++;
                                        }
                                        if (n != 0) {
                                            break;
                                        }
                                    }
                                }
                                if (n == 0 && m == 0) {
                                    for (Province province : provinceListDb) {
                                        if (province.getProvinceApiId().equals(dvhcElementDistrict.getChaId())) {
                                            DistrictApi districtAdd = new DistrictApi(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), dvhcElementDistrict.getId(), province.getProvinceId(), dvhcElementDistrict.getMa(), dvhcElementDistrict.getTen(), true, dvhcElementDistrict.getId(), idVersion);
                                            districtListApi.add(districtAdd);
                                            dem++;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // add version id
            for (District dis : districtListAlldb) {
                dis.setVersionId(idVersion);
            }
            apiService.uploadDistrictDb(districtListAlldb);
            if (districtListApi.size() != 0) {
                dem++;
            }
            apiService.uploadDistrict(districtListApi);

            saveCommune();
        } catch (IOException e) {
            e.printStackTrace();
            FacesUtil.addErrorMessage("Đồng bộ thất bại");
        }
    }

    public void saveCommune() {
        // List lấy từ Db
        List<District> districtsListDb = (List<District>) districtRepository.findAll();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/DanhSachDonViHanhChinh\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/donvihanhchinh", replaces);

            DvhcRoot dvhcRootElements = DvhcXmlParseUtil.convertStringToXml(rs);
            List<CommuneApi> communeListApi = new ArrayList<>();
            Date now = new Date();

            // List lấy từ API
            List<Commune> communeListAlldb = (List<Commune>) communeRepository.findAll();
            if(communeListAlldb.size() != 0) {
                for (Commune cm : communeListAlldb) {
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

            for (DvhcElement dvhcElementProvince : dvhcRootElements.getElements()) {

                if (dvhcElementProvince.getCapId().equals(1L) || dvhcElementProvince.getCapId().equals(4L) && (Long.parseLong(dvhcElementProvince.getMa()) < 1000L)) {

                    for (DvhcElement dvhcElementDistrict : dvhcRootElements.getElements()) {

                        if ((dvhcElementDistrict.getCapId().equals(2L) ||dvhcElementDistrict.getCapId().equals(8L) || dvhcElementDistrict.getCapId().equals(5L) || dvhcElementDistrict.getCapId().equals(9L)) && dvhcElementProvince.getId().equals(dvhcElementDistrict.getChaId())) {

                            for (DvhcElement dvhcElementCommune : dvhcRootElements.getElements()) {
                                int m = 0;
                                if ((dvhcElementCommune.getCapId().equals(6L) || dvhcElementCommune.getCapId().equals(3L) || dvhcElementCommune.getCapId().equals(7L)) && dvhcElementDistrict.getId().equals(dvhcElementCommune.getChaId())) {
                                    for (Commune communeCheckDb : communeListAlldb) {
                                        if (dvhcElementCommune.getMa().equalsIgnoreCase(communeCheckDb.getCode()) && (dvhcElementCommune.getTen().equalsIgnoreCase(communeCheckDb.getName()))) {
                                            m++;
                                        }
                                        if (m != 0) {
                                            break;
                                        }
                                    }
                                    int n = 0;
                                    if (m == 0) {
                                        for (Commune communeCheckDb : communeListAlldb) {
                                            if (dvhcElementCommune.getMa().equalsIgnoreCase(communeCheckDb.getCode()) || dvhcElementCommune.getId().equals(communeCheckDb.getCommuneId())) {
                                                communeCheckDb.setCode(dvhcElementCommune.getMa());
                                                communeCheckDb.setName(dvhcElementCommune.getTen());
                                                communeCheckDb.setVersionId(idVersion);
                                                if(communeCheckDb.getCommuneId() > 100000L) {
                                                    dem ++;
                                                }
                                                n++;
                                            }
                                            if (n != 0) {
                                                break;
                                            }
                                        }
                                    }

                                    if (n == 0 && m == 0) {
                                        for (District district : districtsListDb) {
                                            if (dvhcElementCommune.getChaId().equals(district.getDistrictApiId())) {
                                                CommuneApi communeAdd = new CommuneApi(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), dvhcElementCommune.getId(), district.getProvinceId(), district.getDistrictId(), dvhcElementCommune.getMa(), dvhcElementCommune.getTen(), true, idVersion);
                                                communeListApi.add(communeAdd);
                                                dem++;
                                                break;
                                            }
                                        }
                                    }

                                }
                            }

                        }
                    }

                }
            }
            // add versionid
            for (Commune com : communeListAlldb) {
                com.setVersionId(idVersion);
            }
            apiService.uploadCommuneDb(communeListAlldb);
            if (communeListApi.size() != 0) {
                dem++;
            }
            apiService.uploadCommune(communeListApi);

            //  endApiController.endApi();
            statusApi = (StatusApi) statusApiRepository.findById(DbConstant.STATUS_API_ID).orElse(null);
            statusApi.setStatus(DbConstant.STATUS_API_STATUS_INACTIVE);
            statusApiRepository.save(statusApi);
            if (dem != 0 || idVersion.equals(0L)) {
                // add versionId
                List<Province>  provinceListAddVersion= (List<Province>) provinceRepository.findAll();
                for(Province province : provinceListAddVersion) {
                    province.setVersionId(idVersion + 1L);
                }
                apiService.uploadProvinceDb(provinceListAddVersion);

                List<District>  districtListAddVersion = (List<District>) districtRepository.findAll();
                for(District district : districtListAddVersion) {
                    district.setVersionId(idVersion +1L);
                }
                apiService.uploadDistrictDb(districtListAddVersion);
                List<Commune> communeListAddVersion = (List<Commune>) communeRepository.findAll();
                for(Commune commune : communeListAddVersion) {
                    commune.setVersionId(idVersion +1L);
                }
                apiService.uploadCommuneDb(communeListAddVersion);
//
                historySyncList = (List<HistorySync>) historySyncRepository.findAll();
                if (historySyncList.size() != 0) {
                    for (HistorySync historySync : historySyncList) {
                        historySync.setStatusApply(0L);
                    }
                }
                apiService.uploadHistorySync(historySyncList);
                historySync = new HistorySync();
                historySync.setVersionId(idVersion + 1L);
                historySync.setCreateDate(now);
                historySync.setStatusApply(1L);
                historySyncService.save(historySync);
                apiService.uploadProvinceVersion(provinceVersionList);
                apiService.uploadDistrictVersion(districtVersionList);
                apiService.uploadCommuneVersion(communeVersionList);

                // xoa version qua gioi han
                int sum = ((List<HistorySync>) historySyncRepository.findAll()).size();
                if (sum == Integer.parseInt(PropertiesUtil.getProperty("versionApi.size")) + 1) {
                    historySyncList = (List<HistorySync>) historySyncRepository.findAll();
                    Long versionMin = 0L;
                    for (HistorySync his : historySyncList) {
                        versionMin = his.getVersionId();
                    }
                    for (HistorySync hs : historySyncList) {
                        if (hs.getVersionId() < versionMin) {
                         versionMin = hs.getVersionId();
                        }
                    }
                    historySyncRepository.deleteAllByVersionId(versionMin);
                    provinceVersionRepository.deleteAllByVersionId(versionMin);
                    districtVersionRepository.deleteAllByVersionId(versionMin);
                    communeVersionRepository.deleteAllByVersionId(versionMin);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
