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
import vn.compedia.website.auction.dto.api.ApiRegulatoryAgenciesSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiRegulatoryAgencies;
import vn.compedia.website.auction.repository.ApiRegulatoryAgenciesRepository;
import vn.compedia.website.auction.repository.AuctionRegisterRepository;
import vn.compedia.website.auction.service.ApiRegulatoryAgenciesService;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.RegulatoryAgenciesElement;
import vn.compedia.website.auction.xml.RegulatoryAgenciesRoot;
import vn.compedia.website.auction.xml.RegulatoryAgenciesXmlParseUtil;

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
public class ApiRegulatoryAgenciesController extends BaseController {

    @Inject
    protected UploadSingleImageController uploadSingleImageController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private ApiBaseController apiBaseController;
    @Autowired
    protected AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    protected ApiRegulatoryAgenciesService apiRegulatoryAgenciesService;
    @Autowired
    protected ApiRegulatoryAgenciesRepository apiRegulatoryAgenciesRepository;

    private ApiRegulatoryAgencies apiRegulatoryAgencies;
    private ApiRegulatoryAgenciesSearchDto apiRegulatoryAgenciesSearchDto;
    private LazyDataModel<ApiRegulatoryAgencies> lazyDataModel;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
            onSearch();
        }
    }

    public void api() {

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/CapCoQuanQuanLyAll\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/capcqql", replaces);

            RegulatoryAgenciesRoot regulatoryAgenciesElement = RegulatoryAgenciesXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            // List lấy từ API
            apiRegulatoryAgenciesRepository.deleteAllRecords();
            List<ApiRegulatoryAgencies> apiNationList = new ArrayList<>();
            for (RegulatoryAgenciesElement nationElement : regulatoryAgenciesElement.getElements()) {
                ApiRegulatoryAgencies nationAdd = new ApiRegulatoryAgencies(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), nationElement.getTen());
                apiNationList.add(nationAdd);
            }
            apiRegulatoryAgenciesService.uploadRegulatoryAgencies(apiNationList);
            actionSystemController.onSave("Đồng bộ danh mục cấp cơ quan quản lý", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }

    public void resetAll() {
        apiRegulatoryAgencies = new ApiRegulatoryAgencies();
        apiRegulatoryAgenciesSearchDto = new ApiRegulatoryAgenciesSearchDto();

        uploadSingleImageController.resetAll(null);
        lazyDataModel = new LazyDataModel<ApiRegulatoryAgencies>() {
            @Override
            public List<ApiRegulatoryAgencies> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
        actionSystemController.resetAll();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void onSearch() {

        lazyDataModel = new LazyDataModel<ApiRegulatoryAgencies>() {
            @Override
            public List<ApiRegulatoryAgencies> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiRegulatoryAgenciesSearchDto.setPageIndex(first);
                apiRegulatoryAgenciesSearchDto.setPageSize(pageSize);
                apiRegulatoryAgenciesSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiRegulatoryAgenciesSearchDto.setSortOrder(sort);
                return apiRegulatoryAgenciesRepository.search(apiRegulatoryAgenciesSearchDto);
            }

            @Override
            public ApiRegulatoryAgencies getRowData(String rowKey) {
                List<ApiRegulatoryAgencies> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiRegulatoryAgencies obj : listNhatKy) {
                    if (obj.getRegulatoryAgenciesId() .equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = apiRegulatoryAgenciesRepository.countSearch(apiRegulatoryAgenciesSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }

    @Override
    protected EScope getMenuId() {
        return EScope.API_MANAGEMENT_LEVEL;
    }

}

