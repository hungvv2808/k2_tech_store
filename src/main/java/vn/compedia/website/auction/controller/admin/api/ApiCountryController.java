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
import vn.compedia.website.auction.dto.api.ApiCountrySearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiCountry;
import vn.compedia.website.auction.repository.ApiCountryRepository;
import vn.compedia.website.auction.repository.PaymentRepository;
import vn.compedia.website.auction.service.ApiCountryService;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.CountryElement;
import vn.compedia.website.auction.xml.CountryRoot;
import vn.compedia.website.auction.xml.CountryXmlParseUtil;

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
public class ApiCountryController extends BaseController {
    @Inject
    protected UploadSingleImageController uploadSingleImageController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private ApiBaseController apiBaseController;
    @Autowired
    private ApiCountryService apiCountryService;
    @Autowired
    protected ApiCountryRepository apiCountryRepository;
    @Autowired
    protected PaymentRepository paymentRepository;

    private ApiCountry apiCountry;
    private ApiCountrySearchDto apiCountrySearchDto;
    private LazyDataModel<ApiCountry> lazyDataModel;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
            onSearch();
        }
    }

    public void api() {

        apiCountryRepository.deleteAllRecords();
        List<ApiCountry> apiCountryList = new ArrayList<>();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/QuocGiaAll\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/quocgia", replaces);

            CountryRoot countryRootElement = CountryXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            // List lấy từ API
            for (CountryElement countryElement : countryRootElement.getElements()) {
                ApiCountry apiCountryAdd = new ApiCountry(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), countryElement.getTen());
                apiCountryList.add(apiCountryAdd);
            }
            apiCountryService.uploadCountry(apiCountryList);
            actionSystemController.onSave("Đồng bộ danh mục quốc gia", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }

    public void resetAll() {
        apiCountry = new ApiCountry();
        apiCountrySearchDto = new ApiCountrySearchDto();

        uploadSingleImageController.resetAll(null);
        lazyDataModel = new LazyDataModel<ApiCountry>() {
            @Override
            public List<ApiCountry> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
        actionSystemController.resetAll();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void onSearch() {

        lazyDataModel = new LazyDataModel<ApiCountry>() {
            @Override
            public List<ApiCountry> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiCountrySearchDto.setPageIndex(first);
                apiCountrySearchDto.setPageSize(pageSize);
                apiCountrySearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiCountrySearchDto.setSortOrder(sort);
                return apiCountryRepository.search(apiCountrySearchDto);
            }

            @Override
            public ApiCountry getRowData(String rowKey) {
                List<ApiCountry> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiCountry obj : listNhatKy) {
                    if (obj.getCountryId() .equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = apiCountryRepository.countSearch(apiCountrySearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");

    }

    @Override
    protected EScope getMenuId() {
        return EScope.API_NATION;
    }

}

