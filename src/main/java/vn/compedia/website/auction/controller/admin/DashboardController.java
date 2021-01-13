package vn.compedia.website.auction.controller.admin;


import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.auction.AuctionRegisterController;
import vn.compedia.website.auction.controller.admin.manage_auction.AssignAuctionController;
import vn.compedia.website.auction.controller.admin.regulation.RegulationController;
import vn.compedia.website.auction.controller.admin.regulation.RegulationDetailController;
import vn.compedia.website.auction.controller.admin.request.AuctionReqController;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.auction.AuctionRegisterSearchDto;
import vn.compedia.website.auction.dto.regulation.RegulationSearchDto;
import vn.compedia.website.auction.dto.request.AuctionReqSearchDto;
import vn.compedia.website.auction.entity.DashboardDoughnut;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.AuctionRegister;
import vn.compedia.website.auction.model.AuctionReq;
import vn.compedia.website.auction.model.Regulation;
import vn.compedia.website.auction.repository.AssetRepository;
import vn.compedia.website.auction.repository.AuctionRegisterRepository;
import vn.compedia.website.auction.repository.AuctionReqRepository;
import vn.compedia.website.auction.repository.RegulationRepository;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.FacesUtil;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.*;

@Getter
@Setter
@Named
@Scope(value = "session")
public class DashboardController extends BaseController {
    @Inject
    private AuctionReqController auctionReqController;
    @Inject
    private RegulationController regulationController;
    @Inject
    private AuctionRegisterController auctionRegisterController;
    @Inject
    private RegulationDetailController regulationDetailController;
    @Inject
    private AssignAuctionController assignAuctionController;
    @Autowired
    private AuctionReqRepository auctionReqRepository;
    @Autowired
    private RegulationRepository regulationRepository;
    @Autowired
    private AuctionRegisterRepository auctionRegisterRepository;
    @Autowired
    private AssetRepository assetRepository;

    private List<AuctionReq> auctionReqList;
    private List<Regulation> regulationList;
    private List<AuctionRegister> auctionRegisterForRejectedJoin;
    private Long auctionRegisterForOthers;
    private BarChartModel barModel;
    private Date time;
    private List<SelectItem> listYear;
    private String year;
    private Long selectedYear;
    private DashboardDoughnut dashboardDoughnut;

    @PostConstruct
    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
            createBarModel();
            createDonutModel();
        }
    }

    public void resetAll() {
        time = new Date();
        listYear = new ArrayList<>();
        year = getYear(time);
        selectedYear = Long.valueOf(getYear(time));
        for (Long i = selectedYear - 3; i <= selectedYear + 2; i++) {
            listYear.add(new SelectItem(i, "" + i));
        }
    }

    // Biểu đồ tròn
    public void createDonutModel() {
        dashboardDoughnut = assetRepository.dashboard();
    }

    // Biểu đồ cột
    public void onChange() {
        listYear = new ArrayList<>();
        for (Long i = selectedYear - 3; i <= selectedYear + 2; i++) {
            listYear.add(new SelectItem(i, "" + i));
        }
        year = String.valueOf(selectedYear);
        createBarModel();
    }

    //Biểu đồ cột
    public void createBarModel() {
        barModel = new BarChartModel();
        ChartData data = new ChartData();

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Thống kê kết quả đấu giá tài sản năm " + year);
        title.setFontSize(18);

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Tài sản đấu giá không thành");
        barDataSet.setBackgroundColor("rgb(237, 125, 49)");
        barDataSet.setBorderColor("rgb(237, 125, 49)");
        barDataSet.setBorderWidth(1);

        List<Number> values = new ArrayList<>();
        int t1 = 0, t2 = 0, t3 = 0, t4 = 0, t5 = 0, t6 = 0, t7 = 0, t8 = 0, t9 = 0, t10 = 0, t11 = 0, t12 = 0;
        List<AssetDto> assetDtoList = assetRepository.findEndedStatus();
        for (AssetDto asd : assetDtoList) {
            if (!asd.isAssetManagementEnding() && year.equals(getYear(asd.getStartTime())) && asd.getStatus() == DbConstant.ASSET_STATUS_ENDED) {
                if (getMonth(asd.getStartTime()).equals("01")) {
                    t1++;
                }
                if (getMonth(asd.getStartTime()).equals("02")) {
                    t2++;
                }
                if (getMonth(asd.getStartTime()).equals("03")) {
                    t3++;
                }
                if (getMonth(asd.getStartTime()).equals("04")) {
                    t4++;
                }
                if (getMonth(asd.getStartTime()).equals("05")) {
                    t5++;
                }
                if (getMonth(asd.getStartTime()).equals("06")) {
                    t6++;
                }
                if (getMonth(asd.getStartTime()).equals("07")) {
                    t7++;
                }
                if (getMonth(asd.getStartTime()).equals("08")) {
                    t8++;
                }
                if (getMonth(asd.getStartTime()).equals("09")) {
                    t9++;
                }
                if (getMonth(asd.getStartTime()).equals("10")) {
                    t10++;
                }
                if (getMonth(asd.getStartTime()).equals("11")) {
                    t11++;
                }
                if (getMonth(asd.getStartTime()).equals("12")) {
                    t12++;
                }
            }
        }
        values.add(t1);
        values.add(t2);
        values.add(t3);
        values.add(t4);
        values.add(t5);
        values.add(t6);
        values.add(t7);
        values.add(t8);
        values.add(t9);
        values.add(t10);
        values.add(t11);
        values.add(t12);
        barDataSet.setData(values);

        t1 = 0;
        t2 = 0;
        t3 = 0;
        t4 = 0;
        t5 = 0;
        t6 = 0;
        t7 = 0;
        t8 = 0;
        t9 = 0;
        t10 = 0;
        t11 = 0;
        t12 = 0;
        BarChartDataSet barDataSet2 = new BarChartDataSet();
        barDataSet2.setLabel("Tài sản đấu giá thành công");
        barDataSet2.setBackgroundColor("rgb(54, 162, 235)");
        barDataSet2.setBorderColor("rgb(54, 162, 235)");
        barDataSet2.setBorderWidth(1);
        List<Number> values2 = new ArrayList<>();
        for (AssetDto asd : assetDtoList) {
            if ((asd.isAssetManagementEnding() && year.equals(getYear(asd.getStartTime())) && asd.getStatus() == DbConstant.ASSET_STATUS_ENDED)
                    || (asd.getStatus() == DbConstant.ASSET_STATUS_NOT_SUCCESS && year.equals(getYear(asd.getStartTime())))) {
                if (getMonth(asd.getStartTime()).equals("01")) {
                    t1++;
                }
                if (getMonth(asd.getStartTime()).equals("02")) {
                    t2++;
                }
                if (getMonth(asd.getStartTime()).equals("03")) {
                    t3++;
                }
                if (getMonth(asd.getStartTime()).equals("04")) {
                    t4++;
                }
                if (getMonth(asd.getStartTime()).equals("05")) {
                    t5++;
                }
                if (getMonth(asd.getStartTime()).equals("06")) {
                    t6++;
                }
                if (getMonth(asd.getStartTime()).equals("07")) {
                    t7++;
                }
                if (getMonth(asd.getStartTime()).equals("08")) {
                    t8++;
                }
                if (getMonth(asd.getStartTime()).equals("09")) {
                    t9++;
                }
                if (getMonth(asd.getStartTime()).equals("10")) {
                    t10++;
                }
                if (getMonth(asd.getStartTime()).equals("11")) {
                    t11++;
                }
                if (getMonth(asd.getStartTime()).equals("12")) {
                    t12++;
                }
            }
        }
        values2.add(t1);
        values2.add(t2);
        values2.add(t3);
        values2.add(t4);
        values2.add(t5);
        values2.add(t6);
        values2.add(t7);
        values2.add(t8);
        values2.add(t9);
        values2.add(t10);
        values2.add(t11);
        values2.add(t12);
        barDataSet2.setData(values2);


        t1 = 0;
        t2 = 0;
        t3 = 0;
        t4 = 0;
        t5 = 0;
        t6 = 0;
        t7 = 0;
        t8 = 0;
        t9 = 0;
        t10 = 0;
        t11 = 0;
        t12 = 0;
        BarChartDataSet barDataSet3 = new BarChartDataSet();
        barDataSet3.setLabel("Tài sản tạm dừng đấu giá");
        barDataSet3.setBackgroundColor("rgb(165, 165, 165)");
        barDataSet3.setBorderColor("rgb(165, 165, 165)");
        barDataSet3.setBorderWidth(1);
        List<Number> values3 = new ArrayList<>();
        for (AssetDto asd : assetDtoList) {
            if (year.equals(getYear(asd.getStartTime())) && asd.getStatus() == DbConstant.ASSET_STATUS_CANCELED) {
                if (getMonth(asd.getStartTime()).equals("01")) {
                    t1++;
                }
                if (getMonth(asd.getStartTime()).equals("02")) {
                    t2++;
                }
                if (getMonth(asd.getStartTime()).equals("03")) {
                    t3++;
                }
                if (getMonth(asd.getStartTime()).equals("04")) {
                    t4++;
                }
                if (getMonth(asd.getStartTime()).equals("05")) {
                    t5++;
                }
                if (getMonth(asd.getStartTime()).equals("06")) {
                    t6++;
                }
                if (getMonth(asd.getStartTime()).equals("07")) {
                    t7++;
                }
                if (getMonth(asd.getStartTime()).equals("08")) {
                    t8++;
                }
                if (getMonth(asd.getStartTime()).equals("09")) {
                    t9++;
                }
                if (getMonth(asd.getStartTime()).equals("10")) {
                    t10++;
                }
                if (getMonth(asd.getStartTime()).equals("11")) {
                    t11++;
                }
                if (getMonth(asd.getStartTime()).equals("12")) {
                    t12++;
                }
            }
        }
        values3.add(t1);
        values3.add(t2);
        values3.add(t3);
        values3.add(t4);
        values3.add(t5);
        values3.add(t6);
        values3.add(t7);
        values3.add(t8);
        values3.add(t9);
        values3.add(t10);
        values3.add(t11);
        values3.add(t12);
        barDataSet3.setData(values3);

        data.addChartDataSet(barDataSet2);
        data.addChartDataSet(barDataSet);
        data.addChartDataSet(barDataSet3);

        List<String> labels = new ArrayList<>();
        if (Long.parseLong(getYear(time)) == Long.parseLong(year)) {
            for (int i = 1; i <= Long.parseLong(getMonth(time)); i++) {
                labels.add("Tháng " + i);
            }
        } else {
            for (int i = 1; i <= 12; i++) {
                labels.add("Tháng " + i);
            }
        }
        data.setLabels(labels);
        barModel.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
        options.setTitle(title);
        barModel.setOptions(options);
    }

    public String getYear(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY");
        return simpleDateFormat.format(date).toUpperCase();
    }

    public String getMonth(Date date) {
        SimpleDateFormat simpleMonthFormat = new SimpleDateFormat("MM");
        return simpleMonthFormat.format(date).toUpperCase();

    }

    public Integer getAmountAuctionReqNoProcess() {
        return auctionReqRepository.countByStatus(DbConstant.CHUA_XU_LY_ID);
    }

    public void onClickAmountAuctionReqNoProces() {
        auctionReqController.setSearchDto(new AuctionReqSearchDto());
        auctionReqController.getSearchDto().setStatus(DbConstant.CHUA_XU_LY_ID);
        FacesUtil.redirect("/admin/don-yeu-cau-dau-gia/quan-ly-don-yeu-cau-dau-gia-tai-san.xhtml?status=" + DbConstant.CHUA_XU_LY_ID);
    }

    public Integer getAmountRegulationNotYetPosted() {
        return regulationRepository.countByStatus(DbConstant.CHUA_DANG_TAI);
    }

    public void onClickAmountRegulationNotYetPosted() {
        regulationDetailController.setRegulationSearchDto(new RegulationSearchDto());
        regulationDetailController.getRegulationSearchDto().setStatus(DbConstant.CHUA_DANG_TAI);
        FacesUtil.redirect("/admin/quan-ly-dau-gia/quy-che-dau-gia.xhtml?status=" + DbConstant.CHUA_DANG_TAI);
    }

    public Integer getAmountAutcionRegisterRefuse() {
        return auctionRegisterRepository.countByStatusAndStatusRefund(
                DbConstant.TU_CHOI_THAM_GIA,
                DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE
        );
    }

    public void onClickAmountAutcionRegisterRefuse() {
        auctionRegisterController.setAuctionRegisterSearchDto(new AuctionRegisterSearchDto());
        auctionRegisterController.getAuctionRegisterSearchDto().setStatus(DbConstant.TU_CHOI_THAM_GIA);
        auctionRegisterController.getAuctionRegisterSearchDto().setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE);
        FacesUtil.redirect("/admin/quan-ly-dau-gia/thong-tin-dang-ky-tham-gia-dau-gia.xhtml?status=" + DbConstant.TU_CHOI_THAM_GIA + "&statusrefund=" + DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE);
    }

    public Integer getAmountAutcionRegisterRegistrationNotValid() {
        return auctionRegisterRepository.countByStatusAndStatusRefund(
                DbConstant.DANG_KY_THAT_BAI,
                DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE
        );
    }

    public void onClickAmountAutcionRegisterRegistrationNotValid() {
        auctionRegisterController.setAuctionRegisterSearchDto(new AuctionRegisterSearchDto());
        auctionRegisterController.getAuctionRegisterSearchDto().setStatus(DbConstant.DANG_KY_THAT_BAI);
        auctionRegisterController.getAuctionRegisterSearchDto().setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE);
        FacesUtil.redirect("/admin/quan-ly-dau-gia/thong-tin-dang-ky-tham-gia-dau-gia.xhtml?status=" + DbConstant.DANG_KY_THAT_BAI + "&statusrefund=" + DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE);
    }

    public Integer getAmountAuctionRegisterEndedAndCancel() {
        return auctionRegisterRepository.countByAssetStatusAndStatusRefund(
                Arrays.asList(
                        DbConstant.ASSET_STATUS_ENDED,
                        DbConstant.ASSET_STATUS_CANCELED
                ),
                DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE,
                DbConstant.AUCTION_REGISTER_STATUS_REFUSE_WIN_NORMAL,
                Collections.singletonList(DbConstant.REGULATION_AUCTION_STATUS_NOT_HAPPEN)
        );
    }

    public void onClickAmountAuctionRegisterEndedAndCancel() {
        auctionRegisterController.setAuctionRegisterSearchDto(new AuctionRegisterSearchDto());
        auctionRegisterController.getAuctionRegisterSearchDto().setStatusList(Arrays.asList(DbConstant.ASSET_STATUS_ENDED, DbConstant.ASSET_STATUS_CANCELED));
        auctionRegisterController.getAuctionRegisterSearchDto().setStatusRefund(DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE);
        FacesUtil.redirect(
                "/admin/quan-ly-dau-gia/thong-tin-dang-ky-tham-gia-dau-gia.xhtml"
                        + "?assetstatus=" + DbConstant.ASSET_STATUS_ENDED
                        + "&statuscancel=" + DbConstant.ASSET_STATUS_CANCELED
                        + "&statusrefund=" + DbConstant.AUCTION_REGISTER_STATUS_REFUND_THREE
                        + "&refundprice=1"
        );
    }

    public Integer getAmountAuctionNotHasAuctioneer() {
        return regulationRepository.countByAuctioneerIdAndStatusAndAuctionStatus(null, DbConstant.REGULATION_STATUS_PUBLIC, DbConstant.REGULATION_AUCTION_STATUS_WAITING);
    }

    public void onClickAmountAuctionNotHasActioneer() {
        FacesUtil.redirect("/admin/dieu-hanh-dau-gia/phan-cong-dieu-hanh-dau-gia.xhtml?auctioneerid=&status=" + DbConstant.REGULATION_STATUS_PUBLIC + "&auctionstatus=" + DbConstant.REGULATION_AUCTION_STATUS_WAITING);
    }

    @Override
    protected EScope getMenuId() {
        return EScope.PUBLIC;
    }
}
