package vn.tech.website.store.controller.admin;


import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.charts.bar.BarChartModel;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.entity.EScope;

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
    private Long storeRegisterForOthers;
    private BarChartModel barModel;
    private Date time;
    private List<SelectItem> listYear;
    private String year;
    private Long selectedYear;

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
