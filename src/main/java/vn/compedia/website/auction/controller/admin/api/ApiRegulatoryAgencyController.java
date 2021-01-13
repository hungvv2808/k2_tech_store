package vn.compedia.website.auction.controller.admin.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.controller.admin.common.UploadSingleImageController;
import vn.compedia.website.auction.dto.api.ApiRegulatoryAgencySearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiRegulatoryAgency;
import vn.compedia.website.auction.repository.ApiRegulatoryAgencyRepository;
import vn.compedia.website.auction.service.ApiRegulatoryAgencyService;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.RegulatoryAgencyElement;
import vn.compedia.website.auction.xml.RegulatoryAgencyRoot;
import vn.compedia.website.auction.xml.RegulatoryAgencyXmlParseUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.*;

@Named
@Scope(value = "session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiRegulatoryAgencyController extends BaseController {

    @Inject
    protected UploadSingleImageController uploadSingleImageController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private ApiBaseController apiBaseController;
    @Autowired
    protected ApiRegulatoryAgencyService apiRegulatoryAgencyService;
    @Autowired
    protected ApiRegulatoryAgencyRepository apiRegulatoryAgencyRepository;

    private ApiRegulatoryAgency apiRegulatoryAgency;
    private ApiRegulatoryAgencySearchDto apiRegulatoryAgencySearchDto;
    private LazyDataModel<ApiRegulatoryAgency> lazyDataModel;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
            onSearch();
        }
    }

    public void api() {

        apiRegulatoryAgencyRepository.deleteAllRecords();
        List<ApiRegulatoryAgency> apiRegulatoryAgencyList = new ArrayList<>();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/DanhSachCoQuan\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/coquan", replaces);

            RegulatoryAgencyRoot regulatoryAgencyRoottElement = RegulatoryAgencyXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            // List lấy từ API
            for (RegulatoryAgencyElement regulatoryAgencyRootElement : regulatoryAgencyRoottElement.getElements()) {
                ApiRegulatoryAgency nationAdd = new ApiRegulatoryAgency(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), regulatoryAgencyRootElement.getTen());
                apiRegulatoryAgencyList.add(nationAdd);
            }
            apiRegulatoryAgencyService.uploadRegulatoryAgency(apiRegulatoryAgencyList);
            actionSystemController.onSave("Đồng bộ danh mục cơ quan quản lý", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }

    public void resetAll() {
        apiRegulatoryAgency = new ApiRegulatoryAgency();
        apiRegulatoryAgencySearchDto = new ApiRegulatoryAgencySearchDto();

        uploadSingleImageController.resetAll(null);
        lazyDataModel = new LazyDataModel<ApiRegulatoryAgency>() {
            @Override
            public List<ApiRegulatoryAgency> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
        actionSystemController.resetAll();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void onSearch() {

        lazyDataModel = new LazyDataModel<ApiRegulatoryAgency>() {
            @Override
            public List<ApiRegulatoryAgency> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiRegulatoryAgencySearchDto.setPageIndex(first);
                apiRegulatoryAgencySearchDto.setPageSize(pageSize);
                apiRegulatoryAgencySearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiRegulatoryAgencySearchDto.setSortOrder(sort);
                return apiRegulatoryAgencyRepository.search(apiRegulatoryAgencySearchDto);
            }

            @Override
            public ApiRegulatoryAgency getRowData(String rowKey) {
                List<ApiRegulatoryAgency> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiRegulatoryAgency obj : listNhatKy) {
                    if (obj.getRegulatoryAgencyId() .equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = apiRegulatoryAgencyRepository.countSearch(apiRegulatoryAgencySearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }

    @Override
    protected EScope getMenuId() {
        return EScope.API_MANAGEMENT_AGENCY;
    }

}

