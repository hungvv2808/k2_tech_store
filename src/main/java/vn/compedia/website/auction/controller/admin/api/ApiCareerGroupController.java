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
import vn.compedia.website.auction.dto.api.ApiCareerGroupSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiCareerGroup;
import vn.compedia.website.auction.repository.ApiCareerGroupRepository;
import vn.compedia.website.auction.service.ApiCareerGroupService;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.CareerGroupElement;
import vn.compedia.website.auction.xml.CareerGroupRoot;
import vn.compedia.website.auction.xml.CareerGroupXmlParseUtil;

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
public class ApiCareerGroupController extends BaseController{
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private ApiBaseController apiBaseController;
    @Autowired
    private ApiCareerGroupService apiCareerGroupService;
    @Autowired
    private ApiCareerGroupRepository apiCareerGroupRepository;

    private LazyDataModel<ApiCareerGroup> lazyDataModel;
    private ApiCareerGroup apiCareerGroup;
    private ApiCareerGroupSearchDto apiCareerGroupSearchDto;

    public void initData(){
        if(!FacesContext.getCurrentInstance().isPostback()){
            init();
            resetAll();
            onSearch();
        }
    }

    public void resetAll(){
        apiCareerGroup = new ApiCareerGroup();
        apiCareerGroupSearchDto = new ApiCareerGroupSearchDto();
        lazyDataModel = new LazyDataModel<ApiCareerGroup>() {
            @Override
            public List<ApiCareerGroup> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
    }
    public void onSearch(){
        lazyDataModel = new LazyDataModel<ApiCareerGroup>() {
            @Override
            public List<ApiCareerGroup> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiCareerGroupSearchDto.setPageIndex(first);
                apiCareerGroupSearchDto.setPageSize(pageSize);
                apiCareerGroupSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiCareerGroupSearchDto.setSortOrder(sort);
                return apiCareerGroupRepository.search(apiCareerGroupSearchDto);
            }

            @Override
            public ApiCareerGroup getRowData(String rowKey) {
                List<ApiCareerGroup> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiCareerGroup obj : listNhatKy) {
                    if (obj.getCareerGroupId() .equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = apiCareerGroupRepository.countSearch(apiCareerGroupSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");

    }

    public void api() {

        apiCareerGroupRepository.deleteAllRecords();
        List<ApiCareerGroup> apiCareerGroupList = new ArrayList<>();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/NganhNgheKinhDoanhAll\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/nganhnghe", replaces);

            CareerGroupRoot careerGroupRootElement = CareerGroupXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            // List lấy từ API
            for (CareerGroupElement careerGroupElement : careerGroupRootElement.getElements()) {
                if(careerGroupElement.getCap().equals(1L)) {
                    ApiCareerGroup apiCareerGroupAdd = new ApiCareerGroup(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), careerGroupElement.getTen());
                    apiCareerGroupList.add(apiCareerGroupAdd);
                }
            }
            apiCareerGroupService.uploadApiCareerGroup(apiCareerGroupList);
            actionSystemController.onSave("Đồng bộ danh mục nhóm ngành nghề", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }

    @Override
    protected EScope getMenuId() {
        return EScope.API_CAREER_GROUP;
    }
}
