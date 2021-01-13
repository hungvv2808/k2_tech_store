package vn.compedia.website.auction.controller.admin;

import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.common.UploadSingleFileRegulationController;
import vn.compedia.website.auction.entity.EScope;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@Scope(value = "session")
public class SignatureDigitalController extends BaseController {


    @Inject
    private UploadSingleFileRegulationController uploadSingleFileRegulationController;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
        }
    }

    public void resetAll() {
        uploadSingleFileRegulationController.resetAll(null);
    }

    @Override
    protected EScope getMenuId() {
        return EScope.PUBLIC;
    }
}



