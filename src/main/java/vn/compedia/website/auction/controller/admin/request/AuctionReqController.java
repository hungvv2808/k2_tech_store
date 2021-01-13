package vn.compedia.website.auction.controller.admin.request;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.CommonController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.controller.admin.common.CityDistrictController;
import vn.compedia.website.auction.controller.admin.regulation.RegulationController;
import vn.compedia.website.auction.controller.admin.regulation.RegulationDetailController;
import vn.compedia.website.auction.dto.request.AuctionReqDto;
import vn.compedia.website.auction.dto.request.AuctionReqSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.AuctionReq;
import vn.compedia.website.auction.model.Regulation;
import vn.compedia.website.auction.repository.AccountRepository;
import vn.compedia.website.auction.repository.AuctionReqRepository;
import vn.compedia.website.auction.repository.RegulationRepository;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.util.StringUtil;

import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = "session")

@Getter
@Setter
public class AuctionReqController extends BaseController {
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private CityDistrictController cityDistrictController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private RegulationController regulationController;
    @Inject
    private RegulationDetailController regulationDetailController;
    @Inject
    private HttpServletRequest request;
    @Inject
    private CommonController commonController;

    @Autowired
    protected AuctionReqRepository auctionReqRepository;
    @Autowired
    protected RegulationRepository regulationRepository;
    @Autowired
    protected AccountRepository accountRepository;

    private String confirmMessage;

    private Regulation regulation;
    private AuctionReq auctionReq;
    private AuctionReqDto auctionReqDto;
    private AuctionReqSearchDto searchDto;
    private LazyDataModel<AuctionReqDto> lazyDataModel;
    private List<SelectItem> accountList;
    private List<SelectItem> statusList;
    private String moTa;
    private Long auctionReqId;
    private Long accountId;
    public String CHUA_XU_LY_STRING = "Chưa xử lý";
    public String DANG_XU_LY_STRING = "Đang xử lý";
    public String KY_HOP_DONG_STRING = "Ký hợp đồng";
    public String KHONG_KY_HOP_DONG_STRING = "Không ký hợp đồng";
    public String value = null;
    public String status;
    public Integer oldStatus;
    private boolean checkButtonEdit;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            status = request.getParameter("status");
            resetAll();
        }
    }

    public void resetAll() {
        oldStatus = null;
        checkButtonEdit = false;
        statusList = new ArrayList<>();
        auctionReq = new AuctionReq();
        auctionReqDto = new AuctionReqDto();
        accountList = new ArrayList<>();
        cityDistrictController.resetAll();
        regulation = new Regulation();
        List<Account> account = accountRepository.findAccountByRoleIdAndAccountStatus(DbConstant.ROLE_ID_USER, DbConstant.ACCOUNT_ACTIVE_STATUS);
        for (Account ac : account) {
            accountList.add(new SelectItem(ac.getAccountId(), commonController.abbreviate(ac.getFullName(), DbConstant.LIMITCHARACTER_HOME)));
        }
        searchDto = new AuctionReqSearchDto();
        onSearch();
        actionSystemController.resetAll();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public List<SelectItem> setStatusListSelectItem(int status, Long auctionReqId) {
        List<SelectItem> statusList = new ArrayList<>();
        List<Long> listAuctionReqId = regulationRepository.findAuctionReqId();
        switch (status) {
            case DbConstant.CHUA_XU_LY_ID:
                statusList.add(new SelectItem(DbConstant.CHUA_XU_LY_ID, CHUA_XU_LY_STRING));
                statusList.add(new SelectItem(DbConstant.DANG_XU_LY_ID, DANG_XU_LY_STRING));
                statusList.add(new SelectItem(DbConstant.KY_HOP_DONG_ID, KY_HOP_DONG_STRING));
                statusList.add(new SelectItem(DbConstant.KHONG_KY_HOP_DONG_ID, KHONG_KY_HOP_DONG_STRING));
                break;
            case DbConstant.DANG_XU_LY_ID:
                statusList.add(new SelectItem(DbConstant.DANG_XU_LY_ID, DANG_XU_LY_STRING));
                statusList.add(new SelectItem(DbConstant.KY_HOP_DONG_ID, KY_HOP_DONG_STRING));
                statusList.add(new SelectItem(DbConstant.KHONG_KY_HOP_DONG_ID, KHONG_KY_HOP_DONG_STRING));
                break;
            case DbConstant.KY_HOP_DONG_ID:
                if (listAuctionReqId.contains(auctionReqId)){
                    statusList.add(new SelectItem(DbConstant.KY_HOP_DONG_ID, KY_HOP_DONG_STRING));
                }else {
                    statusList.add(new SelectItem(DbConstant.KY_HOP_DONG_ID, KY_HOP_DONG_STRING));
                    statusList.add(new SelectItem(DbConstant.KHONG_KY_HOP_DONG_ID, KHONG_KY_HOP_DONG_STRING));
                }
                break;
            case DbConstant.KHONG_KY_HOP_DONG_ID:
                statusList.add(new SelectItem(DbConstant.KY_HOP_DONG_ID, KY_HOP_DONG_STRING));
                statusList.add(new SelectItem(DbConstant.KHONG_KY_HOP_DONG_ID, KHONG_KY_HOP_DONG_STRING));
                break;
        }
        return statusList;
    }

    public void showUpdatePopup(AuctionReqDto obj){
        auctionReqDto = new AuctionReqDto();
        BeanUtils.copyProperties(obj, auctionReqDto);
        Integer roleId = auctionReqRepository.getRoleIdAuctionReq(obj.getAuctionReqId());
        if (roleId != null && DbConstant.ROLE_ID_USER == roleId.intValue()) {
            checkButtonEdit = true;
        } else {
            checkButtonEdit = false;
        }
    }

    public void resetDialog() {
        auctionReqDto = new AuctionReqDto();
        auctionReq = new AuctionReq();
        checkButtonEdit = false;
    }

    public void changePage(AuctionReqDto obj) {
        regulationController.setAuctionReqId(obj.getAuctionReqId());
        regulationController.setRegulationId(null);
        regulationController.setDisableCRUD(false);
        regulationController.resetAll();
        FacesUtil.redirect("/admin/quan-ly-dau-gia/thong-tin-quy-che.xhtml");
    }

    public void onSaveData() {
        if (auctionReqDto.getAccountId() == null) {
            setErrorForm("Cá nhân tổ chức là trường bắt buộc");
            return;
        }
        if (StringUtils.isBlank(auctionReqDto.getAssetName())) {
            setErrorForm("Bạn vui lòng nhập tên tài sản");
            return;
        }

        if (StringUtils.isBlank(StringUtil.removeHtmlTags(auctionReqDto.getAssetDescription()))) {
            setErrorForm("Bạn vui lòng nhập mô tả của tài sản");
            FacesUtil.updateView("growl");
            return;
        }

        BeanUtils.copyProperties(auctionReqDto, auctionReq);
        auctionReq.setCreateBy(authorizationController.getAccountDto().getAccountId());
        auctionReq.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        auctionReq.setUpdateDate(new Date());
        if (auctionReq.getAuctionReqId() == null) {
            actionSystemController.onSave(" Tạo thông tin đơn yêu cầu bán tài sản với tài sản " + auctionReq.getAssetName(), authorizationController.getAccountDto().getAccountId());
        } else {
            actionSystemController.onSave(" Sửa thông tin đơn yêu cầu bán tài sản với tải sản " + auctionReq.getAssetName(), authorizationController.getAccountDto().getAccountId());
        }
        auctionReqRepository.save(auctionReq);

        FacesUtil.addSuccessMessage("Lưu thành công");
        FacesUtil.closeDialog("dialogReq");
        resetAll();
        FacesUtil.updateView("growl");
        onSearch();
        FacesUtil.updateView("searchForm");
    }

    public void onSearch() {
        searchDto.setProviceId(null);
        searchDto.setDistrictId(null);
        searchDto.setCommuneId(null);
        if (cityDistrictController.getCityDistrictDto().getProvinceId() != null){
            searchDto.setProviceId(cityDistrictController.getCityDistrictDto().getProvinceId());
        }
        if (cityDistrictController.getCityDistrictDto().getDistrictId() != null){
            searchDto.setDistrictId(cityDistrictController.getCityDistrictDto().getDistrictId());
        }
        if (cityDistrictController.getCityDistrictDto().getCommuneId() != null){
            searchDto.setCommuneId(cityDistrictController.getCityDistrictDto().getCommuneId());
        }
        if(StringUtils.isNotBlank(status)){
            searchDto.setStatus(Integer.parseInt(status));
            status = "";
        }
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<AuctionReqDto>() {
            @Override
            public List<AuctionReqDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                searchDto.setPageIndex(first);
                searchDto.setPageSize(pageSize);
                searchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                searchDto.setSortOrder(sort);
                return auctionReqRepository.search(searchDto);
            }

            @Override
            public AuctionReqDto getRowData(String rowKey) {
                List<AuctionReqDto> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (AuctionReqDto obj : listNhatKy) {
                    if (obj.getAuctionReqId().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = auctionReqRepository.countSearch(searchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }

    public void onredictRegulation(AuctionReqDto dto){
        regulationDetailController.setAuctionReqId(dto.getAuctionReqId());
        FacesUtil.redirect("/admin/quan-ly-dau-gia/quy-che-dau-gia.xhtml?id=" + dto.getAuctionReqId());
    }

    public void onDateSelect() {
        if (null !=  this.searchDto.getDateTo() && null != this.searchDto.getDateFrom()
                && this.searchDto.getDateTo().compareTo(this.searchDto.getDateFrom()) > 0) {
            this.getSearchDto().setToDate(null);
            FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, "Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
        }
    }

    public void onChangeStatus(AuctionReqDto dto) {
        AuctionReq auctionReq = auctionReqRepository.findById(dto.getAuctionReqId()).orElse(null);
        Account ac = accountRepository.findAccountByAccountId(dto.getAccountId());
        auctionReq.setStatus(dto.getStatus());
        auctionReqRepository.save(auctionReq);
        FacesUtil.addSuccessMessage("Lưu thành công");
        if(ac.isOrg()) {
            actionSystemController.onSave("Thay đổi trạng thái bán tài sản của " + ac.getOrgName() + " từ " + getChangeValue(oldStatus)
                    + " sang trạng thái "
                    + getChangeValue(dto.getStatus()), authorizationController.getAccountDto().getAccountId());
        }
        if(!ac.isOrg()) {
            actionSystemController.onSave("Thay đổi trạng thái bán tài sản của " + ac.getFullName() + " từ " + getChangeValue(oldStatus)
                    + " sang trạng thái "
                    + getChangeValue(dto.getStatus()), authorizationController.getAccountDto().getAccountId());
        }
    }

    public void getMotaByAuctionReqId(Long id) {
        moTa = auctionReqRepository.findMoTaByAuctionReqId(id);
    }

    public String getValueWhenChange(AuctionReqDto obj) {
        if (obj.getStatus() == DbConstant.CHUA_XU_LY_ID) {
            return "Chưa xử lý";
        }
        if (obj.getStatus() == DbConstant.DANG_XU_LY_ID) {
            return "Đang xử lý";
        }
        if (obj.getStatus() == DbConstant.KY_HOP_DONG_ID) {
            return "Ký hợp đồng";
        }
        if (obj.getStatus() == DbConstant.KHONG_KY_HOP_DONG_ID) {
            return "Không ký hợp đồng";
        }
        return "";
    }

    public String getChangeValue(int status) {
        if (status == DbConstant.CHUA_XU_LY_ID) {
            return "Chưa xử lý";
        }
        if (status == DbConstant.DANG_XU_LY_ID) {
            return "Đang xử lý";
        }
        if (status == DbConstant.KY_HOP_DONG_ID) {
            return "Ký hợp đồng";
        }
        if (status == DbConstant.KHONG_KY_HOP_DONG_ID) {
            return "Không ký hợp đồng";
        }
        return "";
    }

    public void createConfirmMessage(AjaxBehaviorEvent e) {
        Long auctionReqId = (Long) e.getComponent().getAttributes().get("auctionReqId");
        AuctionReq oldValue = auctionReqRepository.findById(auctionReqId).orElse(null);
        int newValue = (int) ((SelectOneMenu) e.getComponent()).getValue();
        confirmMessage = "Bạn có chắc chắn muốn thay đổi trạng thái từ "
                + createStyleStatus(oldValue.getStatus())
                + " sang trạng thái "
                + createStyleStatus(newValue)
                + "?";
        setOldStatus(oldValue.getStatus());
        FacesUtil.updateView("searchForm:tblSearchResult:" + e.getComponent().getAttributes().get("rowIndex") + ":confirmDialog");
    }

    private String createStyleStatus(int status) {
        return "<b>" + getChangeValue(status) + "</b>";
    }

    private String createStyleStatusSystem(int status) {
        return   getChangeValue(status);
    }

    public String upperCaseFirstChar(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    @Override
    protected EScope getMenuId() {
        return EScope.AUCTION_REQ;
    }

}

