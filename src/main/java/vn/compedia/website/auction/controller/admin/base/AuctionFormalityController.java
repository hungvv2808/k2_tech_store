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
import vn.compedia.website.auction.dto.manage.AuctionFormalitySearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.AuctionFormality;
import vn.compedia.website.auction.repository.AuctionFormalityRepository;
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
public class AuctionFormalityController extends BaseController {
    @Inject
    private AuthorizationController authorizationController;

    @Autowired
    protected AuctionFormalityRepository auctionFormalityRepository;
    @Autowired
    protected RegulationRepository regulationRepository;

    private AuctionFormality auctionFormality;
    private LazyDataModel<AuctionFormality> lazyDataModel;
    private AuctionFormalitySearchDto auctionFormalitySearchDto;
    private List<SelectItem> auctionFormList;
    private AuctionFormality objBackup;
    private Long auctionFormalityId;


    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        auctionFormList = new ArrayList<>();
        auctionFormality = new AuctionFormality();
        auctionFormalitySearchDto = new AuctionFormalitySearchDto();
        onSearch();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");

    }

    public void onSaveData(Long auctionFormalityId ) {
        auctionFormality.setCode(auctionFormality.getCode().trim());
        if (auctionFormality.getAuctionFormalityId() == null || !auctionFormality.getCode().equalsIgnoreCase(objBackup.getCode().trim())) {
            AuctionFormality oldCode = auctionFormalityRepository.findByCode(auctionFormality.getCode());
            if (oldCode != null) {
                FacesUtil.addErrorMessage("Mã hình thức đấu giá đã tồn tại");
                FacesUtil.updateView("growl");
                return ;
            }
        }

        if (auctionFormality.getCreateBy() == null) {
            auctionFormality.setCreateBy(authorizationController.getAccountDto().getAccountId());
            auctionFormality.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        } else {
            auctionFormality.setCreateBy(objBackup.getCreateBy());
            auctionFormality.setCreateDate(objBackup.getCreateDate());
            auctionFormality.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        }
        if (auctionFormality.getAuctionFormalityId() != null && checkEditButton(auctionFormalityId)) {
            FacesUtil.addErrorMessage("Không được sửa vì hình thức " + auctionFormality.getName() + " đang được sử dụng");
            FacesUtil.updateView("growl");
            FacesUtil.closeDialog("dialogInsertUpdate");
        }else {
            if(auctionFormality.getAuctionFormalityId() == null){
                actionSystemController().onSave("Tạo thông tin hình thức " + auctionFormality.getName(), authorizationController.getAccountDto().getAccountId());
            }else {
                actionSystemController().onSave("Sửa thông tin hình thức " + auctionFormality.getName(), authorizationController.getAccountDto().getAccountId());
            }
            auctionFormalityRepository.save(auctionFormality);
            FacesUtil.addSuccessMessage("Lưu thành công");
            FacesUtil.closeDialog("dialogInsertUpdate");
            resetAll();
            FacesUtil.updateView("growl");
            FacesUtil.updateView("searchForm");
            onSearch();
        }
    }

    public void clearData() {
        if (auctionFormality.getAuctionFormalityId() != null && auctionFormality.getAuctionFormalityId() > 0) {
            BeanUtils.copyProperties(objBackup, auctionFormality);
        } else
            resetDialog();
    }

    public void showUpdatePopup(AuctionFormality obj) {
            objBackup = new AuctionFormality();
            BeanUtils.copyProperties(obj, auctionFormality);
            BeanUtils.copyProperties(obj, objBackup);
            FacesUtil.updateView("growl");
    }


    public void resetDialog() {
        auctionFormality = new AuctionFormality();
    }

    public void onDelete(AuctionFormality deleteObj) {
        if (checkEditButton(deleteObj.getAuctionFormalityId())) {
            FacesUtil.addErrorMessage("Không được xóa vì hình thức " + deleteObj.getName() + " đang được sử dụng");
            FacesUtil.updateView("growl");
            FacesUtil.closeDialog("dialogInsertUpdate");
        }else {
            auctionFormalityRepository.deleteById(deleteObj.getAuctionFormalityId());
            actionSystemController().onSave("Xóa thông tin hình thức " + deleteObj.getName(), authorizationController.getAccountDto().getAccountId());
            FacesUtil.addSuccessMessage("Xóa thành công");
            onSearch();
        }
    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<AuctionFormality>() {

            @Override
            public List<AuctionFormality> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                auctionFormalitySearchDto.setPageIndex(first);
                auctionFormalitySearchDto.setPageSize(pageSize);
                auctionFormalitySearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                auctionFormalitySearchDto.setSortOrder(sort);
                return auctionFormalityRepository.search(auctionFormalitySearchDto);
            }

            @Override
            public AuctionFormality getRowData(String rowKey) {
                List<AuctionFormality> auctionFormalityList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (AuctionFormality obj : auctionFormalityList) {
                    if (obj.getCode().equals(value) || obj.getName().equals(value)) {
                        return obj;
                    }

                }
                return null;
            }
        };
        int count = auctionFormalityRepository.countSearch(auctionFormalitySearchDto).intValue();
        lazyDataModel.setRowCount(count);
    }
    public boolean checkEditButton(Long auctionFormalityId) {
        List<Long> listAuctionFormalityId = regulationRepository.findAuctionFormalityId();
        if (listAuctionFormalityId.contains(auctionFormalityId)) {
            return true;
        }
        return false;
    }

    @Override
    protected EScope getMenuId() {
        return EScope.AUCTION_FORMALITY;
    }

}


