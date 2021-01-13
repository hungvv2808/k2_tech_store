package vn.compedia.website.auction.controller.admin.api;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.dto.api.ApiForeignCurrencySearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiForeignCurrency;
import vn.compedia.website.auction.repository.ApiForeignCurrencyRepository;
import vn.compedia.website.auction.service.ApiForeignCurrencyService;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.ForeignCurrencyElement;
import vn.compedia.website.auction.xml.ForeignCurrencyRoot;
import vn.compedia.website.auction.xml.ForeignCurrencyXmlParseUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.*;

@Named
@Scope(value = "session")
@Getter
@Setter
public class ApiForeignCurrencyController extends BaseController {

    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private ApiBaseController apiBaseController;
    @Autowired
    private ApiForeignCurrencyRepository apiForeignCurrencyRepository;
    @Autowired
    private ApiForeignCurrencyService apiForeignCurrencyService;

    private LazyDataModel<ApiForeignCurrency> lazyDataModel;
    private ApiForeignCurrency apiForeignCurrency;
    private ApiForeignCurrencySearchDto apiForeignCurrencySearchDto;

    public void initData(){
        if(!FacesContext.getCurrentInstance().isPostback()){
            init();
            resetAll();
            onSearch();
        }
    }

    public void resetAll(){
        apiForeignCurrency = new ApiForeignCurrency();
        apiForeignCurrencySearchDto = new ApiForeignCurrencySearchDto();
        lazyDataModel = new LazyDataModel<ApiForeignCurrency>() {
            @Override
            public List<ApiForeignCurrency> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
    }

    public void api() {

        apiForeignCurrencyRepository.deleteAllRecords();
        List<ApiForeignCurrency> apiForeignCurrencyList = new ArrayList<>();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/NgoaiTeAll\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/ngoaite", replaces);

            ForeignCurrencyRoot foreignCurrencyRootElement =   ForeignCurrencyXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            for (ForeignCurrencyElement foreignCurrencyElement : foreignCurrencyRootElement.getElements()) {
                ApiForeignCurrency apiForeignCurrencyAdd = new ApiForeignCurrency(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), foreignCurrencyElement.getTen());
                apiForeignCurrencyList.add(apiForeignCurrencyAdd);
            }
            apiForeignCurrencyService.uploadApiForeignCurrency(apiForeignCurrencyList);
            actionSystemController.onSave("Đồng bộ danh mục ngoại tệ", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }

    public void onSearch(){
        lazyDataModel = new LazyDataModel<ApiForeignCurrency>() {
            @Override
            public List<ApiForeignCurrency> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiForeignCurrencySearchDto.setPageIndex(first);
                apiForeignCurrencySearchDto.setPageSize(pageSize);
                apiForeignCurrencySearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiForeignCurrencySearchDto.setSortOrder(sort);
                return apiForeignCurrencyRepository.search(apiForeignCurrencySearchDto);
            }

            @Override
            public ApiForeignCurrency getRowData(String rowKey) {
                List<ApiForeignCurrency> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiForeignCurrency obj : listNhatKy) {
                    if (obj.getForeignCurrencyId() .equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = apiForeignCurrencyRepository.countSearch(apiForeignCurrencySearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");

    }

    @Override
    protected EScope getMenuId() {
        return EScope.API_CURRENCY;
    }
}
