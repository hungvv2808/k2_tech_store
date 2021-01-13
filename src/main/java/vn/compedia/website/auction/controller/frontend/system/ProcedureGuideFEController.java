package vn.compedia.website.auction.controller.frontend.system;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.UploadSingleFileController;
import vn.compedia.website.auction.controller.frontend.BaseFEController;
import vn.compedia.website.auction.dto.system.ProcedureGuideSearchDto;
import vn.compedia.website.auction.model.ProcedureGuide;
import vn.compedia.website.auction.repository.ProcedureGuideRepository;
import vn.compedia.website.auction.util.Constant;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = "session")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProcedureGuideFEController extends BaseFEController {

    @Inject
    protected UploadSingleFileController uploadSingleFileController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private HttpServletRequest request;
    @Autowired
    protected ProcedureGuideRepository procedureGuideRepository;

    private ProcedureGuide procedureGuide;
    private ProcedureGuideSearchDto procedureGuideSearchDto = new ProcedureGuideSearchDto();
    private ProcedureGuide objBackup;
    private List<ProcedureGuide> procedureGuideList;
    private Boolean checkShowFile;
    private boolean viewAll;

    private final int NUMBER_ROW = 5;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        checkShowFile = false;
        // view all
        viewAll = request.getParameter("view_all") != null;
        // load data
        if(request.getParameter("id") == null && procedureGuideSearchDto.getTitle() == null){
            findListProcedureGuide();
        } else if (request.getParameter("id") != null) {
            checkShowFile = true;
            findProcedureGuide(Long.parseLong(request.getParameter("id")));
        } else if (procedureGuideSearchDto.getTitle() != null) {
            loadProcedureGuideList();
        } else {
            request.setAttribute("id",null);
            findListProcedureGuide();
        }
        if (procedureGuideList != null && procedureGuideList.size() <= NUMBER_ROW) {
            viewAll = true;
        }
    }

    public void onSaveData() {

    }

    public void loadProcedureGuideList() {
        String title = procedureGuideSearchDto.getTitle().trim();
        procedureGuideList = procedureGuideRepository.getProcedureGuideByTitle("%"+ title + "%", true);
        showDefaultFile();
    }

    public void findListProcedureGuide() {
        procedureGuideList = new ArrayList<>();
        procedureGuide = new ProcedureGuide();
        if (viewAll) {
            procedureGuideList = procedureGuideRepository.getProcedureGuideSortById(true);
        } else {
            procedureGuideList = procedureGuideRepository.getProcedureGuideSortByIdLimit(true, NUMBER_ROW);
        }
        showDefaultFile();
    }

    public void findProcedureGuide(Long id) {
        procedureGuide = new ProcedureGuide();
        procedureGuide = procedureGuideRepository.findProcedureGuideByProcedureGuideId(id);
    }

    private void showDefaultFile() {
        // default view first file
        if (!procedureGuideList.isEmpty()) {
            procedureGuide = procedureGuideList.get(0);
            checkShowFile = true;
        }
    }

    @Override
    protected String getMenuId() {
        return Constant.ID_INDEX_FONTEND;
    }

}

