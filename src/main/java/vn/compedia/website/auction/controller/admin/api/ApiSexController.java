package vn.compedia.website.auction.controller.admin.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.controller.admin.common.UploadSingleImageController;
import vn.compedia.website.auction.dto.api.ApiSexSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiSex;
import vn.compedia.website.auction.repository.ApiSexRepository;
import vn.compedia.website.auction.repository.PaymentRepository;
import vn.compedia.website.auction.service.ApiSexService;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.SexElement;
import vn.compedia.website.auction.xml.SexRoot;
import vn.compedia.website.auction.xml.SexXmlParseUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.*;

@Named
@Log4j2
@Scope(value = "session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiSexController extends BaseController {

    @Inject
    protected UploadSingleImageController uploadSingleImageController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private ApiBaseController apiBaseController;
    @Autowired
    protected ApiSexRepository apiSexRepository;
    @Autowired
    protected ApiSexService apiSexService;
    @Autowired
    protected PaymentRepository paymentRepository;

    private ApiSex apiSex;
    private ApiSexSearchDto apiSexSearchDto;
    private LazyDataModel<ApiSex> lazyDataModel;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
            onSearch();
        }
    }

    public void api() {

        apiSexRepository.deleteAllRecords();
        List<ApiSex> apiSexList = new ArrayList<>();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/GioiTinhAll\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/gioitinh", replaces);

            SexRoot sexRootElement = SexXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            // List lấy từ API
            for (SexElement sexElement : sexRootElement.getElements()) {
                ApiSex sexAdd = new ApiSex(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), sexElement.getId(), sexElement.getTen());
                apiSexList.add(sexAdd);
            }
            apiSexService.uploadSex(apiSexList);
            actionSystemController.onSave("Đồng bộ danh mục giới tính", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }


    public void resetAll() {
        apiSex = new ApiSex();
        apiSexSearchDto = new ApiSexSearchDto();
        uploadSingleImageController.resetAll(null);
        lazyDataModel = new LazyDataModel<ApiSex>() {
            @Override
            public List<ApiSex> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
        actionSystemController.resetAll();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void onSearch() {
        lazyDataModel = new LazyDataModel<ApiSex>() {
            @Override
            public List<ApiSex> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiSexSearchDto.setPageIndex(first);
                apiSexSearchDto.setPageSize(pageSize);
                apiSexSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiSexSearchDto.setSortOrder(sort);
                return apiSexRepository.search(apiSexSearchDto);
            }

            @Override
            public ApiSex getRowData(String rowKey) {
                List<ApiSex> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiSex obj : listNhatKy) {
                    if (obj.getSexId() .equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };

        int count = apiSexRepository.countSearch(apiSexSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");

    }

    @Override
    protected EScope getMenuId() {
        return EScope.API_SEX;
    }

}

