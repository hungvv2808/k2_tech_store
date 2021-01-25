package vn.tech.website.store.controller.frontend;


import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.util.Constant;
import vn.tech.website.store.util.DbConstant;

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

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {


            init();
            resetAll();
        }
    }

    public void resetAll() {
        now = new Date();
    }

    public void onSearch() {

    }

    public boolean hasEnded(Integer status) {
        return Arrays.asList(
                DbConstant.ASSET_STATUS_ENDED,
                DbConstant.ASSET_STATUS_CANCELED,
                DbConstant.ASSET_STATUS_NOT_SUCCESS
        ).contains(status);
    }

    @Override
    protected String getMenuId() {
        return Constant.ID_INDEX_FONTEND;
    }
}
