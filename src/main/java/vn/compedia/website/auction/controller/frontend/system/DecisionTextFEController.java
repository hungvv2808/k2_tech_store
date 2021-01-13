package vn.compedia.website.auction.controller.frontend.system;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.internal.StringUtil;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.frontend.BaseFEController;
import vn.compedia.website.auction.controller.frontend.common.PaginationController;
import vn.compedia.website.auction.dto.system.DecisionNewsDto;
import vn.compedia.website.auction.dto.system.DecisionNewsSearchDto;
import vn.compedia.website.auction.model.DecisionNews;
import vn.compedia.website.auction.model.DecisionNewsFile;
import vn.compedia.website.auction.repository.DecisionNewsFileRepository;
import vn.compedia.website.auction.repository.DecisionNewsRepository;
import vn.compedia.website.auction.util.*;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = "session")
@Getter
@Setter
public class DecisionTextFEController extends BaseFEController {
    @Inject
    private HttpServletRequest request;
    @Autowired
    protected DecisionNewsRepository decisionNewsRepository;
    @Autowired
    protected DecisionNewsFileRepository decisionNewsFileRepository;
    @Inject
    private DecisionNewsFEController decisionNewsFEController;

    private PaginationController<DecisionNewsDto> pagination;
    private DecisionNewsSearchDto decisionNewsSearchDto;
    private List<DecisionNewsDto> decisionTextList;
    private List<DecisionNewsDto> newsList;
    private DecisionNews decisionNews;
    private DecisionNewsFile decisionNewsFile;
    private Long decisionNewsId;
    private String title;

    public DecisionTextFEController() {
        pagination = new PaginationController<>();
    }

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        title = "";
        decisionNewsFEController.resetAll();
        decisionNews = new DecisionNews();
        decisionTextList = new ArrayList<>();
        newsList = new ArrayList<>();
        decisionNewsSearchDto = new DecisionNewsSearchDto();
        decisionNewsSearchDto.setType(DbConstant.TYPE_NEWS);
        newsList = decisionNewsRepository.getDecisionNewsDto(decisionNewsSearchDto);
        decisionNewsSearchDto.setType(DbConstant.TYPE_DECISION_TEXT);
        decisionTextList = decisionNewsRepository.getDecisionNewsDto(decisionNewsSearchDto);
        if (StringUtil.isBlank(title)) {
            if (request.getParameter("id") == null) {
                loadDecisionNewsList();
            } else {
                loadDecisionNewsList(Long.parseLong(request.getParameter("id")));
            }
        }
        pagination.setRequest(request);
    }

    public void loadDecisionNewsList() {
        decisionNewsSearchDto.setTitle(title);
        pagination.setLazyDataModel(new LazyDataModel<DecisionNewsDto>() {
            @Override
            public List<DecisionNewsDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                decisionNewsSearchDto.setPageIndex(first);
                decisionNewsSearchDto.setPageSize(pageSize);
                decisionNewsSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder == null || sortOrder.equals(SortOrder.DESCENDING)) {
                    sort = "DESC";
                } else {
                    sort = "ASC";
                }
                decisionNewsSearchDto.setSortOrder(sort);
                return decisionNewsRepository.getDecisionNewsDto(decisionNewsSearchDto);
            }

            @Override
            public int getRowCount() {
                return decisionNewsRepository.countSearchFE(decisionNewsSearchDto).intValue();
            }
        });
        pagination.loadData();
    }

    public void loadDecisionNewsList(Long id) {
        searchDecisionNews(id);
        decisionNewsSearchDto.setType(DbConstant.TYPE_NEWS);
        decisionNewsSearchDto.setDecisionNewsId(id);
        newsList = decisionNewsRepository.getDecisionNewsDto(decisionNewsSearchDto);
        decisionNewsSearchDto.setType(DbConstant.TYPE_DECISION_TEXT);
        decisionNewsSearchDto.setDecisionNewsId(id);
        decisionTextList = decisionNewsRepository.getDecisionNewsDto(decisionNewsSearchDto);

        pagination.setLazyDataModel(new LazyDataModel<DecisionNewsDto>() {
            @Override
            public List<DecisionNewsDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                decisionNewsSearchDto.setPageIndex(first);
                decisionNewsSearchDto.setPageSize(pageSize);
                decisionNewsSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder == null || sortOrder.equals(SortOrder.DESCENDING)) {
                    sort = "DESC";
                } else {
                    sort = "ASC";
                }
                decisionNewsSearchDto.setSortOrder(sort);
                return decisionNewsRepository.getDecisionNewsDto(decisionNewsSearchDto);
            }

            @Override
            public int getRowCount() {
                return decisionNewsRepository.countSearch(decisionNewsSearchDto).intValue();
            }
        });
        pagination.loadData();
    }

    public void searchDecisionNews(Long id) {
        decisionNews = decisionNewsRepository.findDecisionNewsByDecisionNewsId(id);
        decisionNewsFile = decisionNewsFileRepository.findDecisionNewsFileByDecisionNewsId(id);
    }

    @Override
    protected String getMenuId() {
        return Constant.ID_CAU_HINH_TIN_TUC;
    }
}

