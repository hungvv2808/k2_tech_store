package vn.compedia.website.auction.controller.frontend.regulation;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.frontend.AuthorizationFEController;
import vn.compedia.website.auction.controller.frontend.BaseFEController;
import vn.compedia.website.auction.controller.frontend.common.FacesNoticeController;
import vn.compedia.website.auction.controller.frontend.common.PaginationController;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.dto.regulation.RegulationSearchDto;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.util.*;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Named
@Scope(value = "session")
@Getter
@Setter
public class RegulationFEController extends BaseFEController {
    @Inject
    private FacesNoticeController facesNoticeController;
    @Inject
    private RegulationIsInFinishController regulationIsInFinishController;
    @Inject
    private AuthorizationFEController authorizationFEController;
    @Inject
    private HttpServletRequest request;
    @Autowired
    protected RegulationRepository regulationRepository;
    @Autowired
    protected AssetRepository assetRepository;
    @Autowired
    protected RegulationFileRepository regulationFileRepository;
    @Autowired
    protected AssetImageRepository assetImageRepository;
    @Autowired
    protected AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    protected AccountRepository accountRepository;

    private PaginationController<RegulationDto> pagination;
    private RegulationSearchDto regulationSearchDto;
    private AuctionRegister auctionRegister;
    private Regulation regulation;
    private List<RegulationDto> regulationsDtoList;
    private List<Regulation> regulationList;
    private List<Asset> assetList;
    private List<AssetDto> assetDtoList;
    private RegulationFile regulationFile;
    private AssetImage assetImage;
    private Date newDate;
    private boolean checkAuctionRegister;
    private Account account;
    private Asset asset;
    private String keyword;

    public RegulationFEController() {
        pagination = new PaginationController<>();
        pagination.resetAll();
    }

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            pagination.setRequest(request);
            resetAll();
        }
    }

    public void resetAll() {
        regulationSearchDto = new RegulationSearchDto();

        // asset search
        regulationSearchDto.setCode(FacesUtil.getRequestParameter("keyword"));
        String auctionStatus = FacesUtil.getRequestParameter("auctionStatus");
        regulationSearchDto.setFromDate(DateUtil.formatDatePattern(FacesUtil.getRequestParameter("fromDate"), DateUtil.DDMMYYYY));
        regulationSearchDto.setToDate(DateUtil.formatDatePattern(FacesUtil.getRequestParameter("toDate"), DateUtil.DDMMYYYY));
        regulationSearchDto.setAuctionStatus(StringUtils.isBlank(auctionStatus) ? null : Integer.parseInt(auctionStatus));

        regulationsDtoList = new ArrayList<>();
        regulation = new Regulation();
        assetList = new ArrayList<>();
        regulationFile = new RegulationFile();
        assetImage = new AssetImage();
        newDate = new Date();
        checkAuctionRegister = true;

        onSearch();
    }

    public void resetDialog() {
        regulationSearchDto = new RegulationSearchDto();
        keyword = "";
    }

    public void findAsset(AssetDto as) {
        if (regulation.getStartTime().getTime() - newDate.getTime() > 0) {
            auctionRegister = auctionRegisterRepository.findAuctionRegisterByAssetIdAndAccountId(as.getAssetId(), authorizationFEController.getAccountDto().getAccountId());
            if (auctionRegister == null) {
                checkAuctionRegister = !checkAuctionRegister;
            }
        }
        assetImage = assetImageRepository.findAssetImageByAssetId(as.getAssetId());
        as.setFilePath(assetImage.getFilePath());
    }

    public void onSearchQuery() {

        if (regulationSearchDto.getFromDate() != null && regulationSearchDto.getToDate() != null) {
            regulationSearchDto.setFromDate(DateUtil.formatDate(regulationSearchDto.getFromDate(), DateUtil.FROM_DATE_FORMAT));
            regulationSearchDto.setToDate(DateUtil.formatDate(regulationSearchDto.getToDate(), DateUtil.TO_DATE_FORMAT));
            if (regulationSearchDto.getFromDate().compareTo(regulationSearchDto.getToDate()) >= 0) {
                setErrorForm("Ngày đến ngày phải lớn hơn ngày từ ngày");
                return;
            }
        }

        Map<String, String> search = new LinkedHashMap<>();
        String fromDate = DateUtil.formatToPattern(regulationSearchDto.getFromDate(), DateUtil.DDMMYYYY);
        String toDate = DateUtil.formatToPattern(regulationSearchDto.getToDate(), DateUtil.DDMMYYYY);
        search.put("auctionStatus", regulationSearchDto.getAuctionStatus() == null ? "" : String.valueOf(regulationSearchDto.getAuctionStatus()));
        search.put("fromDate", fromDate == null ? "" : fromDate);
        search.put("toDate", toDate == null ? "" : toDate);
        search.put("keyword", regulationSearchDto.getCode().trim());

        String redirect = String.format(
                "/frontend/regulation/regulation_list.xhtml?%s",
                StringUtil.buildQueryParam(search)
        );
        FacesUtil.redirect(redirect);
    }

    public void onSearch() {

//        regulationSearchDto.setStatus(DbConstant.DA_DANG_TAI);
        pagination.setLazyDataModel(new LazyDataModel<RegulationDto>() {
            public List<RegulationDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                regulationSearchDto.setPageIndex(first);
                regulationSearchDto.setPageSize(pageSize);
                regulationSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder == null || sortOrder.equals(SortOrder.DESCENDING)) {
                    sort = "DESC";
                } else {
                    sort = "ASC";
                }
                regulationSearchDto.setSortOrder(sort);
                return regulationRepository.searchForGuest(regulationSearchDto);
            }

            @Override
            public int getRowCount() {
                return regulationRepository.countSearchForGuest(regulationSearchDto).intValue();
            }
        });
        pagination.loadData();
        keyword = null;
    }

    public String formatDate(Date date) {
        return DateUtil.formatToPattern(date, DateUtil.MMDDYYYYHHMMSS);
    }

    public void showPopup(Long id) {
        account = accountRepository.findAccountByAccountId(authorizationFEController.getAccountDto().getAccountId());
        asset = assetRepository.findAssetByAssetId(id);
        auctionRegister = auctionRegisterRepository.findAuctionRegisterByAssetIdAndAccountId(asset.getAssetId(), account.getAccountId());
    }

    @Override
    protected String getMenuId() {
        return Constant.ID_REGULATION_FRONTEND;
    }
}
