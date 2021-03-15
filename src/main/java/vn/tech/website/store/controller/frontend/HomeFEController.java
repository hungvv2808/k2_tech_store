package vn.tech.website.store.controller.frontend;


import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.util.Constant;
import vn.tech.website.store.util.DbConstant;
import vn.tech.website.store.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Date;

@Named
@Scope(value = "session")
@Getter
@Setter
public class HomeFEController extends BaseFEController {
    @Inject
    private AuthorizationFEController authorizationFEController;
    private Date now;
    private Boolean noLogin = true;
    private Boolean hasLogin;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
//            init();
            resetAll();
        }
    }

    public void resetAll() {
        now = new Date();
        noLogin = true;
        hasLogin = false;
    }

    public void onSearch() {

    }

    public void redirectProduct(Integer cateId) {
        String request = FacesUtil.getContextPath();
        FacesUtil.redirect(request + "frontend/product/product.xhtml?catid=" + cateId);
    }

    @Override
    protected String getMenuId() {
        return null;
    }
}
