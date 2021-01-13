package vn.compedia.website.auction.controller.admin.report;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.dto.auction.AssetSearchDto;
import vn.compedia.website.auction.dto.report.ReportAssetDto;
import vn.compedia.website.auction.dto.report.ReportDataTable;
import vn.compedia.website.auction.dto.report.ReportExcelDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.AuctionFormality;
import vn.compedia.website.auction.model.AuctionMethod;
import vn.compedia.website.auction.model.TypeAsset;
import vn.compedia.website.auction.repository.*;
import vn.compedia.website.auction.util.*;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Named
@Log4j2
@Getter
@Setter
@Scope(value = "session")
public class ReportController extends BaseController {
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private AuctionFormalityRepository auctionFormalityRepository;
    @Autowired
    private AuctionMethodRepository auctionMethodRepository;
    @Autowired
    private TypeAssetRepository typeAssetRepository;
    @Autowired
    private FunctionRepository functionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuctionReqRepository auctionReqRepository;

    private ReportExcelDto reportExcelDto;
    private AssetSearchDto assetSearchDto;
    private LazyDataModel<ReportAssetDto> lazyDataModel;
    private List<SelectItem> auctionFormalityList;
    private List<SelectItem> auctionMethodList;
    private List<SelectItem> typeAssetList;
    private List<SelectItem> auctioneerList;
    private String regulationFilePath;

    private static final String ERROR_MESSAGE = "Có lỗi trong quá trình xuất";
    private static final String EMPTY_MESSAGE = "Không tồn tại dữ liệu";

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        reportExcelDto = new ReportExcelDto();
        regulationFilePath = null;
        assetSearchDto = new AssetSearchDto();
        buildAuctionFormalityList();
        buildAuctionMethodList();
        buildTypeAssetList();
        buildAuctioneerList();

        lazyDataModel = new LazyDataModel<ReportAssetDto>() {
            @Override
            public List<ReportAssetDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                return null;
            }
        };
        onSearch();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void clearData() {
        assetSearchDto = new AssetSearchDto();
        onSearch();
    }

    public void choose() {
        Map<String,Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        PrimeFaces.current().dialog().openDynamic("selectCar", options, null);
    }


    public void onSearch() {
        onShowReport();

        lazyDataModel = new LazyDataModel<ReportAssetDto>() {

            @Override
            public List<ReportAssetDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                assetSearchDto.setPageIndex(first);
                assetSearchDto.setPageSize(pageSize);
                assetSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                assetSearchDto.setSortOrder(sort);
                return reportRepository.search(assetSearchDto);
            }

            @Override
            public ReportAssetDto getRowData(String rowKey) {
                List<ReportAssetDto> provinceList = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (ReportAssetDto obj : provinceList) {
                    if (obj.getAssetId().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = reportRepository.countSearch(assetSearchDto).intValue();
        lazyDataModel.setRowCount(count);
    }

    public boolean onDateSelect() {
        if (null != assetSearchDto.getFromDate() && null != assetSearchDto.getToDate()) {
            if (assetSearchDto.getFromDate().compareTo(assetSearchDto.getToDate()) > 0) {
                assetSearchDto.setToDate(null);
                setErrorForm("Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
                return false;
            }
        }
        return true;
    }

    public void onShowReport() {
        List<ReportAssetDto> reportAssetDtoList = reportRepository.searchForExport(assetSearchDto);
        if (reportAssetDtoList != null && !reportAssetDtoList.isEmpty()) {
            reportExcelDto = new ReportExcelDto();
            reportExcelDto.setTotalContract(auctionReqRepository.getTotalAuctionReqContract(assetSearchDto).intValue());
            Map<Long, ReportDataTable> mapReportDataTable = new HashMap<>();
            int count = 0;
            int totalRegulation = 0;
            int totalAsset = 0;
            int totalFormalityRoll = 0;
            int totalFormalityDirect = 0;
            int totalMethodUp = 0;
            int totalMethodDown = 0;
            for (ReportAssetDto item : reportAssetDtoList) {
                if (!mapReportDataTable.containsKey(item.getTypeAssetId())) {
                    ReportDataTable reportDataTable = new ReportDataTable();
                    reportDataTable.setNumericalOrder(++count);
                    reportDataTable.setTypeAsset(item.getTypeAssetName());
                    reportDataTable.setRegulationId(item.getRegulationId());
                    reportDataTable.setTypeAssetId(item.getTypeAssetId());

                    totalFormalityRoll = 0;
                    totalFormalityDirect = 0;
                    totalMethodUp = 0;
                    totalMethodDown = 0;

                    totalRegulation = 1;
                    totalAsset = 1;

                    if (item.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_POLL) {
                        totalFormalityRoll = 1;
                        totalFormalityDirect = 0;
                    }
                    if (item.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_DIRECT) {
                        totalFormalityRoll = 0;
                        totalFormalityDirect = 1;
                        if (item.getAuctionMethodId().intValue() == DbConstant.AUCTION_METHOD_ID_UP) {
                            totalMethodUp = 1;
                            totalMethodDown = 0;
                        }
                        if (item.getAuctionMethodId().intValue() == DbConstant.AUCTION_METHOD_ID_DOWN) {
                            totalMethodUp = 0;
                            totalMethodDown = 1;
                        }
                    }
                    reportDataTable.setTotalRegulation(totalRegulation);
                    reportDataTable.setTotalAsset(totalAsset);
                    reportDataTable.setTotalFormalityRoll(totalFormalityRoll);
                    reportDataTable.setTotalMethodUp(totalMethodUp);
                    reportDataTable.setTotalMethodDown(totalMethodDown);
                    reportDataTable.setTotalFormalityDirect(totalFormalityDirect);
                    mapReportDataTable.put(item.getTypeAssetId(), reportDataTable);
                } else {
                    ReportDataTable reportDataTable = mapReportDataTable.get(item.getTypeAssetId());
                    if (item.getTypeAssetId().equals(reportDataTable.getTypeAssetId())) {
                        if (!item.getRegulationId().equals(reportDataTable.getRegulationId())) {
                            reportDataTable.setRegulationId(item.getRegulationId());
                            totalRegulation = reportDataTable.getTotalRegulation();
                            totalRegulation++;
                        }
                        totalAsset++;
                        if (item.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_POLL) {
                            totalFormalityRoll = reportDataTable.getTotalFormalityRoll();
                            totalFormalityRoll++;
                        }
                        if (item.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_DIRECT) {
                            totalFormalityDirect = reportDataTable.getTotalFormalityDirect();
                            totalFormalityDirect++;
                            if (item.getAuctionMethodId().intValue() == DbConstant.AUCTION_METHOD_ID_UP) {
                                totalMethodUp = reportDataTable.getTotalMethodUp();
                                totalMethodDown = reportDataTable.getTotalMethodDown();
                                totalMethodUp++;
                            }
                            if (item.getAuctionMethodId().intValue() == DbConstant.AUCTION_METHOD_ID_DOWN) {
                                totalMethodUp = reportDataTable.getTotalMethodUp();
                                totalMethodDown = reportDataTable.getTotalMethodDown();
                                totalMethodDown++;
                            }
                        }
                    }

                    reportDataTable.setTotalRegulation(totalRegulation);
                    reportDataTable.setTotalAsset(totalAsset);
                    reportDataTable.setTotalFormalityRoll(totalFormalityRoll);
                    reportDataTable.setTotalMethodUp(totalMethodUp);
                    reportDataTable.setTotalMethodDown(totalMethodDown);
                    reportDataTable.setTotalFormalityDirect(totalFormalityDirect);
                    mapReportDataTable.put(item.getTypeAssetId(), reportDataTable);
                }
            }
            reportExcelDto.setReportAssetDtoList(reportAssetDtoList);

            List<ReportDataTable> reportDataTableList = new ArrayList<>(mapReportDataTable.values());
            reportDataTableList.sort(Comparator.comparing(ReportDataTable::getNumericalOrder));

            reportExcelDto.setReportDataTableList(reportDataTableList);
            reportExcelDto.setExportDate(new Date());
            reportExcelDto.setFromDate(assetSearchDto.getFromDate());
            reportExcelDto.setToDate(assetSearchDto.getToDate());
        }
    }

    public StreamedContent getDownloadFile() {
        try {
            if (!validate()) {
                return null;
            }
            assetSearchDto.setPageSize(0);
            return getDownloadFileAssetList(assetSearchDto);
        } catch (Exception e) {
            FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, "Lấy dữ liệu bị lỗi");
            log.error("[getDataReport] cause error: ", e);
        }
        return null;
    }

    private StreamedContent getDownloadFileAssetList(AssetSearchDto assetSearchDto) throws ParseException {
        List<ReportAssetDto> reportAssetDtoList = reportRepository.searchForExport(assetSearchDto);
        if (reportAssetDtoList != null && !reportAssetDtoList.isEmpty()) {
            ReportExcelDto reportExcelDto = new ReportExcelDto();
            reportExcelDto.setTotalContract(auctionReqRepository.getTotalAuctionReqContract(assetSearchDto).intValue());
            Map<Long, ReportDataTable> mapReportDataTable = new HashMap<>();
            int count = 0;
            int totalRegulation = 0;
            int totalAsset = 0;
            int totalFormalityRoll = 0;
            int totalFormalityDirect = 0;
            int totalMethodUp = 0;
            int totalMethodDown = 0;
            for (ReportAssetDto item : reportAssetDtoList) {
                if (!mapReportDataTable.containsKey(item.getTypeAssetId())) {
                    ReportDataTable reportDataTable = new ReportDataTable();
                    reportDataTable.setNumericalOrder(++count);
                    reportDataTable.setTypeAsset(item.getTypeAssetName());
                    reportDataTable.setRegulationId(item.getRegulationId());
                    reportDataTable.setTypeAssetId(item.getTypeAssetId());

                    totalFormalityRoll = 0;
                    totalFormalityDirect = 0;
                    totalMethodUp = 0;
                    totalMethodDown = 0;

                    totalRegulation = 1;
                    totalAsset = 1;

                    if (item.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_POLL) {
                        totalFormalityRoll = 1;
                        totalFormalityDirect = 0;
                    }
                    if (item.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_DIRECT) {
                        totalFormalityRoll = 0;
                        totalFormalityDirect = 1;
                        if (item.getAuctionMethodId().intValue() == DbConstant.AUCTION_METHOD_ID_UP) {
                            totalMethodUp = 1;
                            totalMethodDown = 0;
                        }
                        if (item.getAuctionMethodId().intValue() == DbConstant.AUCTION_METHOD_ID_DOWN) {
                            totalMethodUp = 0;
                            totalMethodDown = 1;
                        }
                    }
                    reportDataTable.setTotalRegulation(totalRegulation);
                    reportDataTable.setTotalAsset(totalAsset);
                    reportDataTable.setTotalFormalityRoll(totalFormalityRoll);
                    reportDataTable.setTotalMethodUp(totalMethodUp);
                    reportDataTable.setTotalMethodDown(totalMethodDown);
                    reportDataTable.setTotalFormalityDirect(totalFormalityDirect);
                    mapReportDataTable.put(item.getTypeAssetId(), reportDataTable);
                } else {
                    ReportDataTable reportDataTable = mapReportDataTable.get(item.getTypeAssetId());
                    if (item.getTypeAssetId().equals(reportDataTable.getTypeAssetId())) {
                        if (!item.getRegulationId().equals(reportDataTable.getRegulationId())) {
                            reportDataTable.setRegulationId(item.getRegulationId());
                            totalRegulation = reportDataTable.getTotalRegulation();
                            totalRegulation++;
                        }
                        totalAsset++;
                        if (item.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_POLL) {
                            totalFormalityRoll = reportDataTable.getTotalFormalityRoll();
                            totalFormalityRoll++;
                        }
                        if (item.getAuctionFormalityId().intValue() == DbConstant.AUCTION_FORMALITY_ID_DIRECT) {
                            totalFormalityDirect = reportDataTable.getTotalFormalityDirect();
                            totalFormalityDirect++;
                            if (item.getAuctionMethodId().intValue() == DbConstant.AUCTION_METHOD_ID_UP) {
                                totalMethodUp = reportDataTable.getTotalMethodUp();
                                totalMethodDown = reportDataTable.getTotalMethodDown();
                                totalMethodUp++;
                            }
                            if (item.getAuctionMethodId().intValue() == DbConstant.AUCTION_METHOD_ID_DOWN) {
                                totalMethodUp = reportDataTable.getTotalMethodUp();
                                totalMethodDown = reportDataTable.getTotalMethodDown();
                                totalMethodDown++;
                            }
                        }
                    }

                    reportDataTable.setTotalRegulation(totalRegulation);
                    reportDataTable.setTotalAsset(totalAsset);
                    reportDataTable.setTotalFormalityRoll(totalFormalityRoll);
                    reportDataTable.setTotalMethodUp(totalMethodUp);
                    reportDataTable.setTotalMethodDown(totalMethodDown);
                    reportDataTable.setTotalFormalityDirect(totalFormalityDirect);
                    mapReportDataTable.put(item.getTypeAssetId(), reportDataTable);
                }
            }
            reportExcelDto.setReportAssetDtoList(reportAssetDtoList);

            List<ReportDataTable> reportDataTableList = new ArrayList<>(mapReportDataTable.values());
            reportDataTableList.sort(Comparator.comparing(ReportDataTable::getNumericalOrder));

            reportExcelDto.setReportDataTableList(reportDataTableList);
            reportExcelDto.setExportDate(new Date());
            reportExcelDto.setFromDate(assetSearchDto.getFromDate());
            reportExcelDto.setToDate(assetSearchDto.getToDate());

            String fileName = ExportUtil.getFileNameExport("ThongKeDauGiaTaiSan");
            try {
                return ExportUtil.downloadExcelFile(reportExcelDto, fileName, Constant.TEMPLATE_REPORT_AND_STATISTICAL, Constant.REPORT_ASSET_LIST_EXPORT_FILE);
            } catch (IOException e) {
                log.error("Download asset list report error: ", e);
                FacesUtil.addErrorMessage(ERROR_MESSAGE);
                return null;
            }
        } else {
            FacesUtil.addErrorMessage(EMPTY_MESSAGE);
            return null;
        }
    }

    private boolean validate() {
        if (null == assetSearchDto.getFromDate() || null == assetSearchDto.getToDate()) {
            setErrorForm("Vui lòng chọn khoảng thời gian xuất báo cáo");
            return false;
        }
        if (!onDateSelect()) {
            return false;
        }
//        int range = Integer.parseInt(PropertiesUtil.getProperty("report.date.range"));
//        if (DateUtil.plusDay(assetSearchDto.getNgayTuNgay(), range).compareTo(assetSearchDto.getNgayDenNgay()) < 0) {
//            assetSearchDto.setNgayDenNgay(null);
//            setErrorForm("Khoảng thời gian xuất báo cáo tối đa là " + range + " ngày");
//            return false;
//        }
        return true;
    }

    private void buildAuctionFormalityList() {
        Iterable<AuctionFormality> auctionFormalityIterable = auctionFormalityRepository.getAuctionFormalityByAuctionFormalityId();
        auctionFormalityList = new ArrayList<>();
        auctionFormalityIterable.forEach(e -> auctionFormalityList.add(new SelectItem(e.getAuctionFormalityId(), e.getName())));
    }

    private void buildAuctionMethodList() {
        Iterable<AuctionMethod> auctionMethodIterable = auctionMethodRepository.getAuctionMethodByAuctionMethodId();
        auctionMethodList = new ArrayList<>();
        auctionMethodIterable.forEach(e -> auctionMethodList.add(new SelectItem(e.getAuctionMethodId(), e.getName())));
    }

    private void buildTypeAssetList() {
        Iterable<TypeAsset> typeAssetIterable = typeAssetRepository.findTypeAssetByStatus();
        typeAssetList = new ArrayList<>();
        typeAssetIterable.forEach(e -> typeAssetList.add(new SelectItem(e.getTypeAssetId(), e.getName())));
    }

    private void buildAuctioneerList() {
        auctioneerList = new ArrayList<>();
        List<Integer> roleIds = functionRepository.findRoleIdsFromScopes(EScope.AUCTION_MANAGE.toString());
        List<Account> account = accountRepository.findAccountByRoleIdInOrderByFullName(roleIds);
        for (Account acc : account) {
            if (acc.getAccountStatus().equals(DbConstant.ACCOUNT_ACTIVE_STATUS)) {
                auctioneerList.add(new SelectItem(acc.getAccountId(), acc.getFullName()));
            }
        }
        Iterable<TypeAsset> typeAssetIterable = typeAssetRepository.findTypeAssetByStatus();
        typeAssetList = new ArrayList<>();
        typeAssetIterable.forEach(e -> typeAssetList.add(new SelectItem(e.getTypeAssetId(), e.getName())));
    }

    //Get file path regulation
    public void getFilePathRegulation(ReportAssetDto dto) {
        regulationFilePath = dto.getRegulationFilePath();
    }

    public boolean renderCountString(String assetName) {
        if (assetName.length() < 100) {
            return true;
        }
        return false;
    }

    @Override
    protected EScope getMenuId() {
        return EScope.STATS_REPORT;
    }
}

