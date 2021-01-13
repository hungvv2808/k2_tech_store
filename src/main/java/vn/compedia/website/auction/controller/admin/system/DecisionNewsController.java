package vn.compedia.website.auction.controller.admin.system;


import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.UploadSingleImageController;
import vn.compedia.website.auction.controller.frontend.common.PaginationController;
import vn.compedia.website.auction.dto.system.DecisionNewsDto;
import vn.compedia.website.auction.dto.system.DecisionNewsSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.DecisionNews;
import vn.compedia.website.auction.model.DecisionNewsFile;
import vn.compedia.website.auction.repository.DecisionNewsFileRepository;
import vn.compedia.website.auction.repository.DecisionNewsRepository;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.util.FileUtil;
import vn.compedia.website.auction.util.StringUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = "session")
@Getter
@Setter
public class DecisionNewsController extends BaseController {

    @Inject
    protected UploadSingleImageController uploadSingleImageController;
    @Inject
    private AuthorizationController authorizationController;
    @Autowired
    protected DecisionNewsRepository decisionNewsRepository;
    @Autowired
    protected DecisionNewsFileRepository decisionNewsFileRepository;

    private boolean type;   //false là tin tức true là văn bản
    private DecisionNews decisionNews;
    private LazyDataModel<DecisionNewsDto> lazyDataModel;
    private DecisionNewsSearchDto decisionNewsSearchDto;
    private PaginationController<DecisionNewsDto> paginationController;
    private String contentNews;
    private String contentDecision;
    private DecisionNewsDto decisionNewsDto;

    public DecisionNewsController() {
        paginationController = new PaginationController<>();
        paginationController.resetAll();
    }

    public void initData(boolean type) {
        this.type = type;
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        decisionNews = new DecisionNews();
        decisionNewsDto = new DecisionNewsDto();
        decisionNewsSearchDto = new DecisionNewsSearchDto();
        decisionNewsSearchDto.setType(type);
        uploadSingleImageController.resetAll(null);
        contentNews = null;
        contentDecision = null;
        paginationController = new PaginationController<>();
        onSearch();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    //Lưu thông tin tin tức
    public void onSaveDataNew() {
        if (!validateData(decisionNews)) {
            FacesUtil.updateView(Constant.ERROR_GROWL_ID);
            return;
        }
        Date now = new Date();
        if (decisionNews.getDecisionNewsId() == null) {
            actionSystemController().onSave("Tạo tin tức " + decisionNews.getTitle(), authorizationController.getAccountDto().getAccountId());
        } else {
            actionSystemController().onSave("Sửa tin tức " + decisionNews.getTitle(), authorizationController.getAccountDto().getAccountId());
        }
        if (decisionNews.getDecisionNewsId() == null) {
            decisionNews.setCreateDate(now);
            decisionNews.setCreateBy(authorizationController.getAccountDto().getAccountId());
            decisionNews.setType(false);
            decisionNewsRepository.save(decisionNews);
            if (uploadSingleImageController.getImagePath() != null) {
                DecisionNewsFile decisionNewsFile = new DecisionNewsFile();
                decisionNewsFile.setImagePath(uploadSingleImageController.getImagePath());
                decisionNewsFile.setDecisionNewsId(decisionNews.getDecisionNewsId());
                decisionNewsFileRepository.save(decisionNewsFile);
            }
        } else {
            decisionNews.setUpdateDate(now);
            decisionNews.setUpdateBy(authorizationController.getAccountDto().getAccountId());
            decisionNewsRepository.save(decisionNews);

            DecisionNewsFile decisionNewsFile = decisionNewsFileRepository.findDecisionNewsFileByDecisionNewsId(decisionNews.getDecisionNewsId());
            if (decisionNewsFile == null && uploadSingleImageController.getImagePath() != null) {
                DecisionNewsFile newsFile = new DecisionNewsFile();
                newsFile.setImagePath(uploadSingleImageController.getImagePath());
                newsFile.setDecisionNewsId(decisionNews.getDecisionNewsId());
                decisionNewsFileRepository.save(newsFile);
            } else if (uploadSingleImageController.getImagePath() != null) {
                decisionNewsFile.setImagePath(uploadSingleImageController.getImagePath());
                decisionNewsFile.setDecisionNewsId(decisionNews.getDecisionNewsId());
                decisionNewsFileRepository.save(decisionNewsFile);
            } else if (uploadSingleImageController.getImagePath() == null && decisionNewsFile != null) {
                decisionNewsFileRepository.deleteByDecisionNewsId(decisionNews.getDecisionNewsId());
            }
        }


        setSuccessForm("Lưu thành công");
        FacesUtil.updateView(Constant.ERROR_GROWL_ID);
        FacesUtil.closeDialog("dialogInsertUpdate");
        resetAll();
    }


    // Lưu thông tin văn bản
    public void onSaveDataDecision() {
        if (!validateData(decisionNews)) {
            FacesUtil.updateView(Constant.ERROR_GROWL_ID);
            return;
        }
        Date now = new Date();
        if (decisionNews.getDecisionNewsId() == null) {
            decisionNews.setCreateDate(now);
            decisionNews.setCreateBy(authorizationController.getAccountDto().getAccountId());
            decisionNews.setType(true);

        } else {
            decisionNews.setUpdateDate(now);
            decisionNews.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        }
        if (decisionNews.getDecisionNewsId() == null) {
            actionSystemController().onSave("Tạo văn bản " + decisionNews.getTitle(), authorizationController.getAccountDto().getAccountId());
        } else {
            actionSystemController().onSave("Sửa văn bản " + decisionNews.getTitle(), authorizationController.getAccountDto().getAccountId());
        }
        decisionNewsRepository.save(decisionNews);
        FacesUtil.addSuccessMessage("Lưu thành công");
        FacesUtil.closeDialog("dialogInsertUpdate");
        resetAll();
        onSearch();

        // update view
        FacesUtil.updateView(Constant.ERROR_GROWL_ID);
        FacesUtil.updateView("searchForm");
    }

    public boolean validateData(DecisionNews decisionNews) {
        if (StringUtils.isBlank(decisionNews.getTitle().trim())) {
            FacesUtil.addErrorMessage("Tiêu đề là trường bắt buộc");
            return false;
        }
        if (StringUtils.isBlank(decisionNews.getDecisionSummary().trim())) {
            FacesUtil.addErrorMessage("Bạn vui lòng nhập nội dung tóm tắt");
            return false;
        }
        if (StringUtils.isBlank(StringUtil.removeHtmlTags(decisionNews.getContent().trim()))) {
            FacesUtil.addErrorMessage("Bạn vui lòng nhập nội dung");
            return false;
        }
        return true;
    }

    public void showUpdateNewPopup(DecisionNewsDto obj) {
        decisionNews = new DecisionNews();

        BeanUtils.copyProperties(obj, decisionNews);
        if (decisionNews.getDecisionNewsId() != null) {
            uploadSingleImageController.resetAll(obj.getImagePath());
        }
    }

    public void showUpdateDecisionPopup(DecisionNewsDto obj) {
        decisionNews = new DecisionNews();
        BeanUtils.copyProperties(obj, decisionNews);
    }

    public void onDelete(DecisionNews deleteObj) {
        try {
            if(deleteObj.isType()) {
                actionSystemController().onSave("Xóa văn bản " + deleteObj.getTitle(), authorizationController.getAccountDto().getAccountId());
            } else {
                actionSystemController().onSave("Xóa tin tức " + deleteObj.getTitle(), authorizationController.getAccountDto().getAccountId());
            }
            decisionNewsRepository.deleteById(deleteObj.getDecisionNewsId());
            decisionNewsFileRepository.deleteByDecisionNewsId(deleteObj.getDecisionNewsId());
            FacesUtil.addSuccessMessage("Xóa thành công");
            onSearch();
        } catch (Exception e) {
            FacesUtil.addErrorMessage("Xóa thất bại");
        }
    }

    public void resetDialog() {
        decisionNews = new DecisionNews();
        uploadSingleImageController.resetAll(null);
        decisionNewsDto = new DecisionNewsDto();
    }

    public void onSearch() {
        lazyDataModel = new LazyDataModel<DecisionNewsDto>() {

            @Override
            public List<DecisionNewsDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                decisionNewsSearchDto.setPageIndex(first);
                decisionNewsSearchDto.setPageSize(pageSize);
                decisionNewsSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                decisionNewsSearchDto.setSortOrder(sort);
                return decisionNewsRepository.search(decisionNewsSearchDto);
            }

            @Override
            public DecisionNewsDto getRowData(String rowKey) {
                List<DecisionNewsDto> DecisionNewsList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (DecisionNewsDto obj : DecisionNewsList) {
                    if (obj.getTitle().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = decisionNewsRepository.countSearch(decisionNewsSearchDto).intValue();
        lazyDataModel.setRowCount(count);
    }

    public void onShowContentNews(Long id) {
        contentNews = decisionNewsRepository.findDecisionNewsByDecisionNewsId(id).getContent();
    }

    public void onShowContentDecision(Long id) {
        contentDecision = decisionNewsRepository.findDecisionNewsByDecisionNewsId(id).getContent();
    }

    public void onUpload(FileUploadEvent e) {
//        if (e.getFile().getSize() > Constant.MAX_FILE_SIZE) {
//            FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, String.format(getErrorMessage(ErrorConstant.KEY_ERROR_FILE_SIZE), Constant.MAX_FILE_SIZE / 1000000));
//            return;
//        }
        decisionNewsDto.setUploadedFile(e.getFile());
        decisionNewsDto.setImagePath(FileUtil.saveImageFile(e.getFile()));
    }

    @Override
    protected EScope getMenuId() {
        return EScope.DECISION;
    }

}

