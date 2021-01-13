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
import vn.compedia.website.auction.dto.api.ApiFamilyRelationshipSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiFamilyRelationship;
import vn.compedia.website.auction.repository.ApiFamilyRelationshipRepository;
import vn.compedia.website.auction.repository.PaymentRepository;
import vn.compedia.website.auction.service.ApiFamilyRelationshipService;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.FamilyRelationshipElement;
import vn.compedia.website.auction.xml.FamilyRelationshipRoot;
import vn.compedia.website.auction.xml.FamilyRelationshipXmlParseUtil;

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
public class ApiFamilyRelationshipController extends BaseController {
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private ApiBaseController apiBaseController;
    @Autowired
    private ApiFamilyRelationshipService apiFamilyRelationshipService;
    @Autowired
    protected PaymentRepository paymentRepository;
    @Autowired
    private ApiFamilyRelationshipRepository apiFamilyRelationshipRepository;

    private LazyDataModel<ApiFamilyRelationship> lazyDataModel;
    private ApiFamilyRelationship apiFamilyRelationship;
    private ApiFamilyRelationshipSearchDto apiFamilyRelationshipSearchDto;

    public void initData(){
        if(!FacesContext.getCurrentInstance().isPostback()){
            init();
            resetAll();
            onSearch();
        }
    }

    public void api() {

        apiFamilyRelationshipRepository.deleteAllRecords();
        List<ApiFamilyRelationship> apiFamilyRelationshipList = new ArrayList<>();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/QuanHeGiaDinhAll\"", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/quanhegiadinh", replaces);

            FamilyRelationshipRoot familyRelationshipRootElement = FamilyRelationshipXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            // List lấy từ API
            for (FamilyRelationshipElement familyRelationshipElement : familyRelationshipRootElement.getElements()) {
                ApiFamilyRelationship apiFamilyRelationshipAdd = new ApiFamilyRelationship(now, now, authorizationController.getAccountDto().getAccountId(), authorizationController.getAccountDto().getAccountId(), familyRelationshipElement.getTen());
                apiFamilyRelationshipList.add(apiFamilyRelationshipAdd);
            }
            apiFamilyRelationshipService.uploadFamilyRelationship(apiFamilyRelationshipList);
            actionSystemController.onSave("Đồng bộ danh mục quan hệ gia đình", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }

    public void resetAll(){
        apiFamilyRelationship = new ApiFamilyRelationship();
        apiFamilyRelationshipSearchDto = new ApiFamilyRelationshipSearchDto();
        lazyDataModel = new LazyDataModel<ApiFamilyRelationship>() {
            @Override
            public List<ApiFamilyRelationship> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
    }

    public void onSearch(){
        lazyDataModel = new LazyDataModel<ApiFamilyRelationship>() {
            @Override
            public List<ApiFamilyRelationship> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiFamilyRelationshipSearchDto.setPageIndex(first);
                apiFamilyRelationshipSearchDto.setPageSize(pageSize);
                apiFamilyRelationshipSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiFamilyRelationshipSearchDto.setSortOrder(sort);
                return apiFamilyRelationshipRepository.search(apiFamilyRelationshipSearchDto);
            }

            @Override
            public ApiFamilyRelationship getRowData(String rowKey) {
                List<ApiFamilyRelationship> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiFamilyRelationship obj : listNhatKy) {
                    if (obj.getFamilyRegulationId() .equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = apiFamilyRelationshipRepository.countSearch(apiFamilyRelationshipSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }

    @Override
    protected EScope getMenuId() {
        return EScope.API_RELATIONSHIP;
    }
}
