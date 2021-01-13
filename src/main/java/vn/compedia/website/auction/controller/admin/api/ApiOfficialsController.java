package vn.compedia.website.auction.controller.admin.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.dto.api.ApiOfficialsSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.api.ApiOfficials;
import vn.compedia.website.auction.repository.ApiOfficialsRepository;
import vn.compedia.website.auction.service.ApiOfficialsService;
import vn.compedia.website.auction.util.DateUtil;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.xml.OfficialElement;
import vn.compedia.website.auction.xml.OfficialXmlParseUtil;
import vn.compedia.website.auction.xml.OfficialsRoot;
import vn.compedia.website.auction.xml.TokenAdministrativeUnits;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Named
@Scope(value = "session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiOfficialsController extends BaseController {

    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private ApiBaseController apiBaseController;
    @Autowired
    protected ApiOfficialsService apiOfficialsService;
    @Autowired
    protected ApiOfficialsRepository apiOfficialsRepository;

    private ApiOfficials apiOfficials;
    private ApiOfficialsSearchDto apiOfficialsSearchDto;
    private LazyDataModel<ApiOfficials> lazyDataModel;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
            onSearch();
        }
    }

    public void resetAll() {
        apiOfficials = new ApiOfficials();
        apiOfficialsSearchDto = new ApiOfficialsSearchDto();

        lazyDataModel = new LazyDataModel<ApiOfficials>() {
            @Override
            public List<ApiOfficials> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return new ArrayList<>();
            }
        };
        lazyDataModel.setRowCount(0);
        actionSystemController.resetAll();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void api() {

        apiOfficialsRepository.deleteAllRecords();
        List<ApiOfficials> apiOfficialsList = new ArrayList<>();

        try {
            Map<String, String> replaces = new HashMap<>();
            replaces.put(" xmlns=\"http://ws.wso2.org/dataservice/DanhSachCongChuc\"", "");
            replaces.put("<imageUrl xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>", "");
            String rs = apiBaseController.api("https://lgsp.danang.gov.vn/dldc/1.0.0/congchuc", replaces);

            OfficialsRoot officialsRootElement = OfficialXmlParseUtil.convertStringToXml(rs);
            Date now = new Date();

            // List lấy từ API
            for (OfficialElement officialElement : officialsRootElement.getElements()) {
                Date ngaySinh = DateUtil.formatDatePattern(officialElement.getNgaySinh(), DateUtil.DDMMYYYY);
                Date ngayCapCMND = DateUtil.formatDatePattern(officialElement.getNgayCapCMND(), DateUtil.DDMMYYYY);
                ApiOfficials apiOfficials = new ApiOfficials(
                        now,
                        now,
                        authorizationController.getAccountDto().getAccountId(),
                        authorizationController.getAccountDto().getAccountId(),
                        officialElement.getDienThoai(),
                        officialElement.getSoCMND(),
                        officialElement.getCoQuanId(),
                        officialElement.getChucVu(),
                        officialElement.getNoiCapCMND(),
                        officialElement.getCoQuan(),
                        officialElement.getHoVaTen(),
                        officialElement.getChucVuId(),
                        officialElement.getMa(),
                        ngaySinh,
                        officialElement.getId(),
                        officialElement.getEmail(),
                        ngayCapCMND
                );
                apiOfficialsList.add(apiOfficials);
            }
            apiOfficialsService.uploadApiOfficials(apiOfficialsList);
            actionSystemController.onSave("Đồng bộ danh mục công chức", authorizationController.getAccountDto().getAccountId());
            resetAll();
            onSearch();
            setSuccessForm("Đồng bộ thành công");
        } catch (IOException e) {
            e.printStackTrace();
            setErrorForm("Đồng bộ thất bại");
        }
    }

    public void onSearch() {

        lazyDataModel = new LazyDataModel<ApiOfficials>() {
            @Override
            public List<ApiOfficials> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                apiOfficialsSearchDto.setPageIndex(first);
                apiOfficialsSearchDto.setPageSize(pageSize);
                apiOfficialsSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                apiOfficialsSearchDto.setSortOrder(sort);
                return apiOfficialsRepository.search(apiOfficialsSearchDto);
            }

            @Override
            public ApiOfficials getRowData(String rowKey) {
                List<ApiOfficials> listApi = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ApiOfficials obj : listApi) {
                    if (obj.getApiOfficialsId() .equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = apiOfficialsRepository.countSearch(apiOfficialsSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }
    @Override
    protected EScope getMenuId() {
        return EScope.API_OFFICIALS;
    }

}

