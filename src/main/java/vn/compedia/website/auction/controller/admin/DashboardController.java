package vn.compedia.website.auction.controller.admin;


import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.charts.bar.BarChartModel;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.entity.DashboardDoughnut;
import vn.compedia.website.auction.entity.EScope;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Named
@Scope(value = "session")
public class DashboardController extends BaseController {
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
        }
    }

    public void resetAll() {

    }

    @Override
    protected EScope getMenuId() {
        return EScope.PUBLIC;
    }
}
