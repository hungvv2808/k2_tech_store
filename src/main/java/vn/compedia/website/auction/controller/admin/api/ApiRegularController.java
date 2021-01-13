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
import vn.compedia.website.auction.dto.api.ApiRegularSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiRegular;
import vn.compedia.website.auction.repository.ApiRegularRepository;
import vn.compedia.website.auction.service.ApiRegularService;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.RegularElement;
import vn.compedia.website.auction.xml.RegularRoot;
import vn.compedia.website.auction.xml.RegularXmlParseUtil;

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
public class ApiRegularController extends BaseController {

    @Inject
    protected UploadSingleImageController uploadSingleImageController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private ApiBaseController apiBaseController;
    @Autowired
    protected ApiRegularService apiRegularService;
    @Autowired
    protected ApiRegularRepository apiRegularRepository;

    private ApiRegular apiRegular;
    private ApiRegularSearchDto apiRegularSearchDto;
    private LazyDataModel<ApiRegular> lazyDataModel;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
            onSearch();
        }
    }

    public void api() {

        apiRegularRepository.deleteAllRecords();
        List<ApiRegular> apiRegularList = new ArrayList<>();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/ChuyenMonAll\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/chuyenmon", replaces);

            RegularRoot regularRootElement = RegularXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            // List lấy từ API
            for (RegularElement regularElement : regularRootElement.getElements()) {
                ApiRegular regularAdd = new ApiRegular(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), regularElement.getTen());
                apiRegularList.add(regularAdd);
            }
            apiRegularService.uploadRigular(apiRegularList);
            actionSystemController.onSave("Đồng bộ danh mục chuyên môn", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }

    public void resetAll() {
        apiRegular = new ApiRegular();
        apiRegularSearchDto = new ApiRegularSearchDto();

        uploadSingleImageController.resetAll(null);
        lazyDataModel = new LazyDataModel<ApiRegular>() {
            @Override
            public List<ApiRegular> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
        actionSystemController.resetAll();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void onSearch() {

        lazyDataModel = new LazyDataModel<ApiRegular>() {
            @Override
            public List<ApiRegular> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiRegularSearchDto.setPageIndex(first);
                apiRegularSearchDto.setPageSize(pageSize);
                apiRegularSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiRegularSearchDto.setSortOrder(sort);
                return apiRegularRepository.search(apiRegularSearchDto);
            }

            @Override
            public ApiRegular getRowData(String rowKey) {
                List<ApiRegular> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiRegular obj : listNhatKy) {
                    if (obj.getRegularId().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = apiRegularRepository.countSearch(apiRegularSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }

    @Override
    protected EScope getMenuId() {
        return EScope.API_REGULAR;
    }

}

