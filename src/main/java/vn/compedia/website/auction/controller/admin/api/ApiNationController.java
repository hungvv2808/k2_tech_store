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
import vn.compedia.website.auction.dto.api.ApiNationSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiNation;
import vn.compedia.website.auction.repository.ApiNationRepository;
import vn.compedia.website.auction.service.ApiNationService;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.NationElement;
import vn.compedia.website.auction.xml.NationRoot;
import vn.compedia.website.auction.xml.NationXmlParseUtil;

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
public class ApiNationController extends BaseController {

    @Inject
    protected UploadSingleImageController uploadSingleImageController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private ApiBaseController apiBaseController;
    @Autowired
    protected ApiNationService apiNationService;
    @Autowired
    protected ApiNationRepository apiNationRepository;

    private ApiNation apiNation;
    private ApiNationSearchDto apiNationSearchDto;
    private LazyDataModel<ApiNation> lazyDataModel;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
            onSearch();
        }
    }

    public void api() {

        apiNationRepository.deleteAllRecords();
        List<ApiNation> apiNationList = new ArrayList<>();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/DanTocAll\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/dantoc", replaces);

            NationRoot nationRootElement = NationXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            // List lấy từ API
            for (NationElement nationElement : nationRootElement.getElements()) {
                ApiNation nationAdd = new ApiNation(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), nationElement.getTen());
                apiNationList.add(nationAdd);
            }
            apiNationService.uploadNation(apiNationList);
            actionSystemController.onSave("Đồng bộ danh mục dân tộc", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }



    public void resetAll() {
        apiNation = new ApiNation();
        apiNationSearchDto = new ApiNationSearchDto();

        uploadSingleImageController.resetAll(null);
        lazyDataModel = new LazyDataModel<ApiNation>() {
            @Override
            public List<ApiNation> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
        actionSystemController.resetAll();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }




    public void onSearch() {

        lazyDataModel = new LazyDataModel<ApiNation>() {
            @Override
            public List<ApiNation> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiNationSearchDto.setPageIndex(first);
                apiNationSearchDto.setPageSize(pageSize);
                apiNationSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiNationSearchDto.setSortOrder(sort);
                return apiNationRepository.search(apiNationSearchDto);
            }

            @Override
            public ApiNation getRowData(String rowKey) {
                List<ApiNation> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiNation obj : listNhatKy) {
                    if (obj.getNationId().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = apiNationRepository.countSearch(apiNationSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");

    }

    @Override
    protected EScope getMenuId() {
        return EScope.API_FOLK;
    }

}

