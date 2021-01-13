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
import vn.compedia.website.auction.dto.api.ApiPaperSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiPaper;
import vn.compedia.website.auction.repository.ApiPaperRepository;
import vn.compedia.website.auction.repository.PaymentRepository;
import vn.compedia.website.auction.service.ApiPaperService;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.PaperElement;
import vn.compedia.website.auction.xml.PaperRoot;
import vn.compedia.website.auction.xml.PaperXmlParseUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.*;

@Named
@Setter
@Getter
@Scope(value="session")
public class ApiPaperController extends BaseController {
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private ApiBaseController apiBaseController;
    @Autowired
    private ApiPaperRepository apiPaperRepository;
    @Autowired
    protected ApiPaperService apiPaperService;
    @Autowired
    protected PaymentRepository paymentRepository;

    private LazyDataModel<ApiPaper> lazyDataModel;
    private ApiPaper apiPaper;
    private ApiPaperSearchDto apiPaperSearchDto;

    public void initData(){
        if(!FacesContext.getCurrentInstance().isPostback()){
            init();
            resetAll();
            onSearch();
        }
    }

    public void api() {

        apiPaperRepository.deleteAllRecords();
        List<ApiPaper> apiPaperList = new ArrayList<>();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/DanhMucGiayToAll\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/giayto", replaces);

            PaperRoot paperRootElement = PaperXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            // List lấy từ API
            for (PaperElement paperElement : paperRootElement.getElements()) {
                ApiPaper apiPaperAdd = new ApiPaper(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), paperElement.getTen());
                apiPaperList.add(apiPaperAdd);
            }
            apiPaperService.uploadedPaper(apiPaperList);
            actionSystemController.onSave("Đồng bộ danh mục giấy tờ", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }

    public void resetAll(){
        apiPaper = new ApiPaper();
        apiPaperSearchDto = new ApiPaperSearchDto();
        lazyDataModel = new LazyDataModel<ApiPaper>() {
            @Override
            public List<ApiPaper> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void onSearch(){
        lazyDataModel = new LazyDataModel<ApiPaper>() {
            @Override
            public List<ApiPaper> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiPaperSearchDto.setPageIndex(first);
                apiPaperSearchDto.setPageSize(pageSize);
                apiPaperSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiPaperSearchDto.setSortOrder(sort);
                return apiPaperRepository.search(apiPaperSearchDto);
            }

            @Override
            public ApiPaper getRowData(String rowKey) {
                List<ApiPaper> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiPaper obj : listNhatKy) {
                    if (obj.getApiPaperId() .equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = apiPaperRepository.countSearch(apiPaperSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }

    @Override
    protected EScope getMenuId() {
        return EScope.API_PAPER;
    }
}
