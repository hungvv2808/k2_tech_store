package vn.compedia.website.auction.controller.admin.system;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.UploadSingleFileController;
import vn.compedia.website.auction.controller.admin.common.UploadSingleFileRegulationController;
import vn.compedia.website.auction.controller.frontend.common.PaginationController;
import vn.compedia.website.auction.dto.system.ProcedureGuideDto;
import vn.compedia.website.auction.dto.system.ProcedureGuideSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.ProcedureGuide;
import vn.compedia.website.auction.repository.ProcedureGuideRepository;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.util.FileUtil;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = "session")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProcedureGuideController extends BaseController {

    @Inject
    protected UploadSingleFileRegulationController uploadSingleFileController;
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private HttpServletRequest request;

    @Autowired
    protected ProcedureGuideRepository procedureGuideRepository;

    private LazyDataModel<ProcedureGuideDto> lazyDataModel;
    private ProcedureGuideDto procedureGuideDto;
    private Account account;
    private ProcedureGuide procedureGuide;
    private ProcedureGuideSearchDto procedureGuideSearchDto = new ProcedureGuideSearchDto();;
    private List<SelectItem> dsProcedureGuide;
    private ProcedureGuide objBackup;
    private List<ProcedureGuide> procedureGuideList;
    private PaginationController<ProcedureGuideDto> pagination;
    private Boolean checkShowFile;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        dsProcedureGuide = new ArrayList<>();
        procedureGuide = new ProcedureGuide();
        procedureGuideSearchDto = new ProcedureGuideSearchDto();
        uploadSingleFileController.resetAll(null);
        onSearch();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void onSaveData() {
        if (StringUtils.isBlank(procedureGuide.getTitle())) {
            FacesUtil.addErrorMessage("Tiêu đề là trường bắt buộc");
            return;
        }
        procedureGuide.setFileName(uploadSingleFileController.getFileName());
        procedureGuide.setFilePath(uploadSingleFileController.getFilePath());
        if (StringUtils.isBlank(procedureGuide.getFilePath())) {
            FacesUtil.addErrorMessage("Bạn vui lòng chọn file!");
            FacesUtil.updateView("growl");
            return;
        }

        if (procedureGuide.getCreateBy() == null) {
            procedureGuide.setCreateBy(authorizationController.getAccountDto().getAccountId());
            procedureGuide.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        } else {
            procedureGuide.setCreateBy(objBackup.getCreateBy());
            procedureGuide.setCreateDate(objBackup.getCreateDate());
            procedureGuide.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        }
        if(procedureGuide.getProcedureGuideId() == null){
            actionSystemController().onSave("Tạo thông tin thủ tục " + procedureGuide.getTitle(), authorizationController.getAccountDto().getAccountId());
        }else {
            actionSystemController().onSave("Sửa thông tin thủ tục " + procedureGuide.getTitle(), authorizationController.getAccountDto().getAccountId());
        }
        procedureGuideRepository.save(procedureGuide);
        FacesUtil.addSuccessMessage("Lưu thành công");
        FacesUtil.closeDialog("dialogInsertUpdate");
        resetAll();
        FacesUtil.updateView("growl");
        onSearch();
        FacesUtil.updateView("searchForm");
    }

    public void showUpdatePopup(ProcedureGuideDto obj) {
        if (obj.getFilePath() != null && obj.getProcedureGuideId() != null) {
            uploadSingleFileController.resetAll(obj.getFilePath());
        }
        objBackup = new ProcedureGuide();
        BeanUtils.copyProperties(obj, procedureGuide);
        BeanUtils.copyProperties(obj, objBackup);
    }

    public void resetDialog() {
        uploadSingleFileController.resetAll(null);
        procedureGuide = new ProcedureGuide();
    }

    public void onDelete(ProcedureGuide deleteObj) {
        try {
            procedureGuideRepository.deleteById(deleteObj.getProcedureGuideId());
            actionSystemController().onSave("Xóa thông tin thủ tục " + deleteObj.getTitle(), authorizationController.getAccountDto().getAccountId());
            FacesUtil.addSuccessMessage("Xóa thành công");
            onSearch();
        } catch (Exception e) {
            FacesUtil.addErrorMessage("Xóa thất bại");
        }
    }

    public void onSearch() {
        lazyDataModel = new LazyDataModel<ProcedureGuideDto>() {
            @Override
            public List<ProcedureGuideDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                procedureGuideSearchDto.setPageIndex(first);
                procedureGuideSearchDto.setPageSize(pageSize);
                procedureGuideSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                procedureGuideSearchDto.setSortOrder(sort);
                return procedureGuideRepository.search(procedureGuideSearchDto);
            }

            @Override
            public ProcedureGuideDto getRowData(String rowKey) {
                List<ProcedureGuideDto> ProcedureGuideList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (ProcedureGuideDto obj : ProcedureGuideList) {
                    if (obj.getTitle().equals(value) || obj.getFilePath().equals(value)) {
                        return obj;
                    }

                }
                return null;
            }
        };
        int count = procedureGuideRepository.countSearch(procedureGuideSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView(" ");
    }

    // tải về file đính kèm khi k mở được file
    public void download(String file) {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.reset();
            response.setHeader("Content-Type", "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + file);
            OutputStream responseOutputStream = response.getOutputStream();
            InputStream fileInputStream = FileUtil.getDownloadFileFromDatabase(file).getStream();
            byte[] bytesBuffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(bytesBuffer)) > 0) {
                responseOutputStream.write(bytesBuffer, 0, bytesRead);
            }
            responseOutputStream.flush();
            fileInputStream.close();
            responseOutputStream.close();
            facesContext.responseComplete();
        } catch (Exception e) {
            FacesUtil.redirect("/error/404.xhtml");
        }
    }

    public StreamedContent getFileDownload(String fileName) {
        StreamedContent streamedContent = FileUtil.getDownloadFileFromDatabase(fileName);
        if (streamedContent == null) {
            FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, "File không tồn tại");
        }
        return streamedContent;
    }


    @Override
    protected EScope getMenuId() {
        return EScope.PROCEDURE_GUIDE;
    }

}

