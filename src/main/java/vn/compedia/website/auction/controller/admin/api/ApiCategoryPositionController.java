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
import vn.compedia.website.auction.dto.api.ApiCategoryPositionSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiCategoryPosition;
import vn.compedia.website.auction.repository.ApiCategoryPositionRepository;
import vn.compedia.website.auction.repository.AuctionRegisterRepository;
import vn.compedia.website.auction.repository.PaymentRepository;
import vn.compedia.website.auction.service.ApiCategoryPositionService;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.CategoryPositionRoot;
import vn.compedia.website.auction.xml.CategoryPositionXmlParseUtil;
import vn.compedia.website.auction.xml.CategoryPositoryElement;

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
public class ApiCategoryPositionController extends BaseController {

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
    protected PaymentRepository paymentRepository;
    @Autowired
    protected ApiCategoryPositionService apiCategoryPositionService;
    @Autowired
    protected ApiCategoryPositionRepository apiCategoryPositionRepository;

    private ApiCategoryPosition ApiCategoryPosition;
    private ApiCategoryPositionSearchDto apiCategoryPositionSearchDto;
    private LazyDataModel<ApiCategoryPosition> lazyDataModel;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
            onSearch();
        }
    }

    public void resetAll() {
        ApiCategoryPosition = new ApiCategoryPosition();
        apiCategoryPositionSearchDto = new ApiCategoryPositionSearchDto();

        uploadSingleImageController.resetAll(null);
        lazyDataModel = new LazyDataModel<ApiCategoryPosition>() {
            @Override
            public List<ApiCategoryPosition> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
        actionSystemController.resetAll();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void api() {

        apiCategoryPositionRepository.deleteAllRecords();
        List<ApiCategoryPosition> apiCategoryPositionList = new ArrayList<>();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/ChucVuAll\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/chucvu", replaces);

            CategoryPositionRoot categoryPositionRootElement = CategoryPositionXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            // List lấy từ API
            for (CategoryPositoryElement categoryPositoryElement : categoryPositionRootElement.getElements()) {
                ApiCategoryPosition apiCategoryPosition = new ApiCategoryPosition(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), categoryPositoryElement.getTen());
                apiCategoryPositionList.add(apiCategoryPosition);
            }
            apiCategoryPositionService.uploadApiCategoryPosition(apiCategoryPositionList);
            actionSystemController.onSave("Đồng bộ danh mục chức vụ", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }

    public void onSearch() {

        lazyDataModel = new LazyDataModel<ApiCategoryPosition>() {
            @Override
            public List<ApiCategoryPosition> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiCategoryPositionSearchDto.setPageIndex(first);
                apiCategoryPositionSearchDto.setPageSize(pageSize);
                apiCategoryPositionSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiCategoryPositionSearchDto.setSortOrder(sort);
                return apiCategoryPositionRepository.search(apiCategoryPositionSearchDto);
            }

            @Override
            public ApiCategoryPosition getRowData(String rowKey) {
                List<ApiCategoryPosition> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiCategoryPosition obj : listNhatKy) {
                    if (obj.getCategoryPositionId() .equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = apiCategoryPositionRepository.countSearch(apiCategoryPositionSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }
    @Override
    protected EScope getMenuId() {
        return EScope.API_POSITION;
    }

}

