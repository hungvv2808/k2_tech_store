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
import vn.compedia.website.auction.dto.api.ApiEducationSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiEducation;
import vn.compedia.website.auction.repository.ApiEducationRepository;
import vn.compedia.website.auction.service.ApiEducationService;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.EducationElement;
import vn.compedia.website.auction.xml.EducationRoot;
import vn.compedia.website.auction.xml.EducationXmlParseUtil;

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
public class ApiEducationController extends BaseController {

    @Inject
    protected UploadSingleImageController uploadSingleImageController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private ApiBaseController apiBaseController;
    @Autowired
    protected ApiEducationService apiEducationService;
    @Autowired
    protected ApiEducationRepository apiEducationRepository;

    private ApiEducation apiEducation;
    private ApiEducationSearchDto apiEducationSearchDto;
    private LazyDataModel<ApiEducation> lazyDataModel;

    public void initData() throws IOException {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
            onSearch();
        }
    }

    public void api() {

        apiEducationRepository.deleteAllRecords();
        List<ApiEducation> apiEducationList = new ArrayList<>();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/TrinhDoHocVanAll\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/hocvan", replaces);

            EducationRoot educationRootElement = EducationXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            //List lấy từ API
            for (EducationElement educationElement : educationRootElement.getElements()) {
                ApiEducation educationAdd = new ApiEducation(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), educationElement.getTen());
                apiEducationList.add(educationAdd);
            }
            apiEducationService.uploadEducation(apiEducationList);
            actionSystemController.onSave("Đồng bộ danh mục học vấn", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }



    public void resetAll() {
        apiEducation = new ApiEducation();
        apiEducationSearchDto = new ApiEducationSearchDto();

        uploadSingleImageController.resetAll(null);
        lazyDataModel = new LazyDataModel<ApiEducation>() {
            @Override
            public List<ApiEducation> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
        actionSystemController.resetAll();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }




    public void onSearch() {

        lazyDataModel = new LazyDataModel<ApiEducation>() {
            @Override
            public List<ApiEducation> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiEducationSearchDto.setPageIndex(first);
                apiEducationSearchDto.setPageSize(pageSize);
                apiEducationSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiEducationSearchDto.setSortOrder(sort);
                return apiEducationRepository.search(apiEducationSearchDto);
            }

            @Override
            public ApiEducation getRowData(String rowKey) {
                List<ApiEducation> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiEducation obj : listNhatKy) {
                    if (obj.getEducationId().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = apiEducationRepository.countSearch(apiEducationSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");

    }

    @Override
    protected EScope getMenuId() {
        return EScope.API_EDUCATION;
    }

}

