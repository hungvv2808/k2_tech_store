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
import vn.compedia.website.auction.dto.api.ApiJobSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiJob;
import vn.compedia.website.auction.repository.ApiJobRepository;
import vn.compedia.website.auction.service.ApiJobService;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.JobElement;
import vn.compedia.website.auction.xml.JobRoot;
import vn.compedia.website.auction.xml.JobXmlParseUtil;

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
public class ApiJobController extends BaseController {
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private ApiBaseController apiBaseController;
    @Autowired
    private ApiJobRepository apiJobRepository;
    @Autowired
    private ApiJobService apiJobService;

    private LazyDataModel<ApiJob> lazyDataModel;
    private ApiJob apiJob;
    private ApiJobSearchDto apiJobSearchDto;

    public void initData(){
        if(!FacesContext.getCurrentInstance().isPostback()){
            init();
            resetAll();
            onSearch();
        }
    }

    public void resetAll(){
        apiJob = new ApiJob();
        apiJobSearchDto = new ApiJobSearchDto();
        lazyDataModel = new LazyDataModel<ApiJob>() {
            @Override
            public List<ApiJob> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
    }

    public void api() {

        apiJobRepository.deleteAllRecords();
        List<ApiJob> apiJobList = new ArrayList<>();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/NgheNghiepAll\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/nghenghiep", replaces);

            JobRoot jobRootElement = JobXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            // List lấy từ API
            for (JobElement jobElement : jobRootElement.getElements()) {
                ApiJob JobAdd = new ApiJob(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), jobElement.getTen());
                apiJobList.add(JobAdd);
            }
            apiJobService.uploadJob(apiJobList);
            actionSystemController.onSave("Đồng bộ danh mục nghề nghiệp", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }

    public void onSearch(){
        lazyDataModel = new LazyDataModel<ApiJob>() {
            @Override
            public List<ApiJob> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiJobSearchDto.setPageIndex(first);
                apiJobSearchDto.setPageSize(pageSize);
                apiJobSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiJobSearchDto.setSortOrder(sort);
                return apiJobRepository.search(apiJobSearchDto);
            }

            @Override
            public ApiJob getRowData(String rowKey) {
                List<ApiJob> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiJob obj : listNhatKy) {
                    if (obj.getJobId() .equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = apiJobRepository.countSearch(apiJobSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");

    }

    @Override
    protected EScope getMenuId() {
        return EScope.API_JOB;
    }
}
