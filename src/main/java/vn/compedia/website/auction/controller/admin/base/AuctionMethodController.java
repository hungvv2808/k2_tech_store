package vn.compedia.website.auction.controller.admin.base;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.dto.manage.AuctionMethodSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.AuctionMethod;
import vn.compedia.website.auction.repository.AuctionMethodRepository;
import vn.compedia.website.auction.repository.RegulationRepository;
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
public class AuctionMethodController extends BaseController {

    @Autowired
    protected AuctionMethodRepository auctionMethodRepository;
    @Autowired
    protected RegulationRepository regulationRepository;

    @Inject
    private AuthorizationController authorizationController;

    private AuctionMethod auctionMethod;
    private LazyDataModel<AuctionMethod> lazyDataModel;
    private AuctionMethodSearchDto auctionMethodSearchDto;
    private List<SelectItem> auctionMethodList;
    private AuctionMethod objBackup;
    private Long auctionMethodId;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        auctionMethodList = new ArrayList<>();
        auctionMethod = new AuctionMethod();
        auctionMethodSearchDto = new AuctionMethodSearchDto();
        onSearch();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void onSaveData(Long auctionMethodId) {
        auctionMethod.setCode(auctionMethod.getCode().trim());
        if (auctionMethod.getAuctionMethodId() == null || !auctionMethod.getCode().equalsIgnoreCase(objBackup.getCode().trim())) {
            AuctionMethod oldCode = auctionMethodRepository.findByCode(auctionMethod.getCode());
            if (oldCode != null) {
                FacesUtil.addErrorMessage("id1","Mã phương thức đã tồn tại");
                FacesUtil.updateView("growl");
                return ;
            }
        }

        if (auctionMethod.getCreateBy() == null) {
            auctionMethod.setCreateBy(authorizationController.getAccountDto().getAccountId());
            auctionMethod.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        } else {
            auctionMethod.setCreateBy(objBackup.getCreateBy());
            auctionMethod.setCreateDate(objBackup.getCreateDate());
            auctionMethod.setUpdateBy(authorizationController.getAccountDto().getAccountId());

        }

        if (auctionMethod.getAuctionMethodId() != null && checkEditButton(auctionMethodId)) {
            FacesUtil.addErrorMessage("Không được sửa vì phương thức " + auctionMethod.getName() + " đang được sử dụng");
            FacesUtil.updateView("growl");
            FacesUtil.closeDialog("dialogInsertUpdate");
        } else {
            if (auctionMethod.getAuctionMethodId() == null) {
                actionSystemController().onSave("Tạo thông tin phương thức " + auctionMethod.getName(), authorizationController.getAccountDto().getAccountId());
            } else {
                actionSystemController().onSave("Sửa thông tin phương thức " + auctionMethod.getName(), authorizationController.getAccountDto().getAccountId());
            }
            auctionMethodRepository.save(auctionMethod);
            FacesUtil.addSuccessMessage("Lưu thành công");
            FacesUtil.closeDialog("dialogInsertUpdate");
            resetAll();
            onSearch();
            FacesUtil.updateView("growl");
            FacesUtil.updateView("searchForm");
        }
    }

    public void clearData() {
        if (auctionMethod.getAuctionMethodId() != null && auctionMethod.getAuctionMethodId() > 0) {
            BeanUtils.copyProperties(objBackup, auctionMethod);
        } else
            resetDialog();
    }

    public void showUpdatePopup(AuctionMethod obj) {
        if (obj.getAuctionMethodId() == null) {
            objBackup = new AuctionMethod();
            BeanUtils.copyProperties(obj, auctionMethod);
            BeanUtils.copyProperties(obj, objBackup);
        } else {
            objBackup = new AuctionMethod();
            BeanUtils.copyProperties(obj, auctionMethod);
            BeanUtils.copyProperties(obj, objBackup);
        }
    }

    public void resetDialog() {
        auctionMethod = new AuctionMethod();
    }

    public void onDelete(AuctionMethod deleteObj) {
        if (checkEditButton(deleteObj.getAuctionMethodId())) {
            FacesUtil.addErrorMessage("Không được xóa vì phương thức " + deleteObj.getName() + " đang được sử dụng");
            FacesUtil.updateView("growl");
            FacesUtil.closeDialog("dialogInsertUpdate");
        }else {
            auctionMethodRepository.deleteById(deleteObj.getAuctionMethodId());
            actionSystemController().onSave("Xóa thông tin phương thức " + deleteObj.getName(), authorizationController.getAccountDto().getAccountId());
            FacesUtil.addSuccessMessage("Xóa thành công");
            onSearch();
        }
    }

    public void onSearch() {
        lazyDataModel = new LazyDataModel<AuctionMethod>() {

            @Override
            public List<AuctionMethod> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                auctionMethodSearchDto.setPageIndex(first);
                auctionMethodSearchDto.setPageSize(pageSize);
                auctionMethodSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                auctionMethodSearchDto.setSortOrder(sort);
                return auctionMethodRepository.search(auctionMethodSearchDto);
            }

            @Override
            public AuctionMethod getRowData(String rowKey) {
                List<AuctionMethod> AuctionMethodList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (AuctionMethod obj : AuctionMethodList) {
                    if (obj.getCode().equals(value) || obj.getName().equals(value)) {
                        return obj;
                    }

                }
                return null;
            }
        };
        int count = auctionMethodRepository.countSearch(auctionMethodSearchDto).intValue();
        lazyDataModel.setRowCount(count);
    }

    public boolean checkEditButton(Long auctionMethodId) {
        List<Long> listAuctionMethodId = regulationRepository.findAuctionMethodId();
        if (listAuctionMethodId.contains(auctionMethodId)) {
            return true;
        }
        return false;
    }

    @Override
    protected EScope getMenuId() {
        return EScope.AUCTION_METHOD;
    }

}


