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
import vn.compedia.website.auction.dto.api.ApiBusinessJobSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiBusinessJob;
import vn.compedia.website.auction.repository.ApiBusinessJobRepository;
import vn.compedia.website.auction.service.ApiBusinessJobService;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.BusinessJobElement;
import vn.compedia.website.auction.xml.BusinessJobRoot;
import vn.compedia.website.auction.xml.BusinessJobXmlParseUtil;

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
public class ApiBusinessJobController extends BaseController {

    @Inject
    private ApiBaseController apiBaseController;
    @Inject
    protected UploadSingleImageController uploadSingleImageController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;

    @Autowired
    protected ApiBusinessJobRepository apiBusinessJobRepository;
    @Autowired
    protected ApiBusinessJobService apiBusinessJobService;

    private ApiBusinessJob apiBusinessJob;
    private ApiBusinessJobSearchDto apiBusinessJobSearchDto;
    private LazyDataModel<ApiBusinessJob> lazyDataModel;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
            onSearch();
        }
    }

    public void resetAll() {
        apiBusinessJob = new ApiBusinessJob();
        apiBusinessJobSearchDto = new ApiBusinessJobSearchDto();

        uploadSingleImageController.resetAll(null);
        lazyDataModel = new LazyDataModel<ApiBusinessJob>() {
            @Override
            public List<ApiBusinessJob> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
        actionSystemController.resetAll();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void api() {

        apiBusinessJobRepository.deleteAllRecords();
        List<ApiBusinessJob> apiBusinessJobsList = new ArrayList<>();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/NganhNgheKinhDoanhAll\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/nganhnghe", replaces);

            BusinessJobRoot businessJobRootElement = BusinessJobXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            for (BusinessJobElement businessJobElement : businessJobRootElement.getElements()) {
                if(!businessJobElement.getCap().equals(1L)) {
                    ApiBusinessJob apiBusinessJobAdd = new ApiBusinessJob(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), businessJobElement.getTen());
                    apiBusinessJobsList.add(apiBusinessJobAdd);
                }
            }
            apiBusinessJobService.uploadApiBusinessJob(apiBusinessJobsList);
            actionSystemController.onSave("Đồng bộ danh mục ngành nghề kinh doanh", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }


    public void onSearch() {

        lazyDataModel = new LazyDataModel<ApiBusinessJob>() {
            @Override
            public List<ApiBusinessJob> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiBusinessJobSearchDto.setPageIndex(first);
                apiBusinessJobSearchDto.setPageSize(pageSize);
                apiBusinessJobSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiBusinessJobSearchDto.setSortOrder(sort);
                return apiBusinessJobRepository.search(apiBusinessJobSearchDto);
            }

            @Override
            public ApiBusinessJob getRowData(String rowKey) {
                List<ApiBusinessJob> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiBusinessJob obj : listNhatKy) {
                    if (obj.getBusinessJobId() .equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = apiBusinessJobRepository.countSearch(apiBusinessJobSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");

    }

    @Override
    protected EScope getMenuId() {
        return EScope.API_BUSINESS;
    }

}

