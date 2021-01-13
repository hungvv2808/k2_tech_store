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
import vn.compedia.website.auction.dto.api.ApiReligionSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiReligion;
import vn.compedia.website.auction.repository.ApiReligionRepository;
import vn.compedia.website.auction.repository.PaymentRepository;
import vn.compedia.website.auction.service.ApiReligionService;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.ReligionElement;
import vn.compedia.website.auction.xml.ReligionRoot;
import vn.compedia.website.auction.xml.ReligionXmlParseUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.*;

@Named
@Scope(value = "session")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiReligionController extends BaseController {
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private ApiBaseController apiBaseController;
    @Autowired
    private ApiReligionRepository apiReligionRepository;
    @Autowired
    protected ApiReligionService apiReligionService;
    @Autowired
    protected PaymentRepository paymentRepository;

    private LazyDataModel<ApiReligion> lazyDataModel;
    private ApiReligion apiReligion;
    private ApiReligionSearchDto apiReligionSearchDto;

    public void initData() {
        if(!FacesContext.getCurrentInstance().isPostback()){
            init();
            resetAll();
            onSearch();
        }
    }

    public void api() {

        apiReligionRepository.deleteAllRecords();
        List<ApiReligion> apiReligionList = new ArrayList<>();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/TonGiaoAll\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/tongiao", replaces);

            ReligionRoot religionRootElement = ReligionXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            // List lấy từ API
            for (ReligionElement religionElement : religionRootElement.getElements()) {
                ApiReligion apiReligionAdd = new ApiReligion(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), religionElement.getTen());
                apiReligionList.add(apiReligionAdd);
            }
            apiReligionService.uploadReligion(apiReligionList);
            actionSystemController.onSave("Đồng bộ danh mục tôn giáo", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }

    public void resetAll(){
        apiReligion = new ApiReligion();
        apiReligionSearchDto = new ApiReligionSearchDto();
        lazyDataModel = new LazyDataModel<ApiReligion>() {
            @Override
            public List<ApiReligion> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void onSearch() {
        lazyDataModel = new LazyDataModel<ApiReligion>() {
            @Override
            public List<ApiReligion> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiReligionSearchDto.setPageIndex(first);
                apiReligionSearchDto.setPageSize(pageSize);
                apiReligionSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiReligionSearchDto.setSortOrder(sort);
                return apiReligionRepository.search(apiReligionSearchDto);
            }

            @Override
            public ApiReligion getRowData(String rowKey) {
                List<ApiReligion> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiReligion obj : listNhatKy) {
                    if (obj.getReligionId() .equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = apiReligionRepository.countSearch(apiReligionSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }

    @Override
    protected EScope getMenuId() {
        return EScope.API_RELIGION;
    }
}
