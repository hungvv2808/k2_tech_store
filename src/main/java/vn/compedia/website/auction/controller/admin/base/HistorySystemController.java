package vn.compedia.website.auction.controller.admin.base;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.common.UploadSingleImageController;
import vn.compedia.website.auction.dto.base.HistorySystemDto;
import vn.compedia.website.auction.dto.base.HistorySystemSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.model.HistorySystem;
import vn.compedia.website.auction.repository.AccountRepository;
import vn.compedia.website.auction.repository.HistorySystemRepository;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = "session")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistorySystemController extends BaseController {

    @Inject
    protected UploadSingleImageController uploadSingleImageController;

    @Autowired
    protected HistorySystemRepository auctionRegisterRepository;
    @Autowired
    protected AccountRepository accountRepository;

    private HistorySystem historySystem;
    private HistorySystemDto historySystemDto;
    private HistorySystemSearchDto historySystemSearchDto;
    private List<SelectItem> accountList;
    private LazyDataModel<HistorySystemDto> lazyDataModel;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        historySystem = new HistorySystem();
        historySystemDto = new HistorySystemDto();
        historySystemSearchDto = new HistorySystemSearchDto();
        accountList = new ArrayList<>();
        List<Account> account = accountRepository.getAccountByAccountId(DbConstant.ROLE_ID_USER);
        for (Account ac : account) {
            accountList.add(new SelectItem(ac.getAccountId(), ac.getFullName()));
        }
        onSearch();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void resetDialog() {
        historySystemDto = new HistorySystemDto();
    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<HistorySystemDto>() {

            @Override
            public List<HistorySystemDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                historySystemSearchDto.setPageIndex(first);
                historySystemSearchDto.setPageSize(pageSize);
                historySystemSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                historySystemSearchDto.setSortOrder(sort);
                return auctionRegisterRepository.search(historySystemSearchDto);
            }

            @Override
            public HistorySystemDto getRowData(String rowKey) {
                List<HistorySystemDto> listNhatKy = getWrappedData();
                Long value = Long.valueOf(rowKey);
                for (HistorySystemDto obj : listNhatKy) {
                    if (obj.getHistorySystemId().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };
        int count = auctionRegisterRepository.countSearch(historySystemSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }
    public void onDateSelect() {
        if (null !=  this.historySystemSearchDto.getFromDate() && null != this.historySystemSearchDto.getToDate()
                && this.historySystemSearchDto.getFromDate().compareTo(this.historySystemSearchDto.getToDate()) > 0) {
            this.getHistorySystemSearchDto().setToDate(null);
            setErrorForm("Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
        }
    }

    @Override
    protected EScope getMenuId() {
        return EScope.HISTORY_SYSTEM;
    }
}

