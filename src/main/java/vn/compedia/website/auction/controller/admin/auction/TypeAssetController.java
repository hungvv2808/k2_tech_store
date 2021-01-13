package vn.compedia.website.auction.controller.admin.auction;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.dto.auction.TypeAssetSearchDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.TypeAsset;
import vn.compedia.website.auction.repository.AssetRepository;
import vn.compedia.website.auction.repository.TypeAssetRepository;
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
public class TypeAssetController extends BaseController {
    @Autowired
    protected TypeAssetRepository typeAssetRepository;
    @Autowired
    protected AssetRepository assetRepository;

    @Inject
    private AuthorizationController authorizationController;

    private TypeAsset typeAsset;
    private LazyDataModel<TypeAsset> lazyDataModel;
    private TypeAssetSearchDto typeAssetSearchDto;
    private List<SelectItem> typeAssetList;
    private TypeAsset typeAssetCopy;
    private Long typeAssetId;


    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        typeAssetList = new ArrayList<>();
        typeAsset = new TypeAsset();
        typeAssetSearchDto = new TypeAssetSearchDto();
        onSearch();
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
    }

    public void onSaveData(Long typeAssetId) {
        typeAsset.setCode(typeAsset.getCode().trim());
        if (typeAsset.getTypeAssetId() == null || !typeAssetCopy.getCode().equalsIgnoreCase(typeAsset.getCode().trim())) {
            TypeAsset oldCode = typeAssetRepository.findByCode(typeAsset.getCode());
            if (oldCode != null) {
                FacesUtil.addErrorMessage("Mã loại tài sản đã tồn tại");
                FacesUtil.updateView("growl");
                return ;
            }
        }

        if (typeAsset.getCreateBy() == null) {
            typeAsset.setCreateBy(authorizationController.getAccountDto().getAccountId());
            typeAsset.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        } else {
            typeAsset.setCreateBy(typeAssetCopy.getCreateBy());
            typeAsset.setCreateDate(typeAssetCopy.getCreateDate());
            typeAsset.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        }

        if (typeAsset.getTypeAssetId() != null && checkEditButton(typeAssetId)) {
            FacesUtil.addErrorMessage("Không được sửa vì loại tài sản " + typeAssetCopy.getName() + " đang được sử dụng để tạo quy chế");
            FacesUtil.updateView("growl");
            FacesUtil.closeDialog("dialogInsertUpdate");
        }
        else {
            if(typeAsset.getTypeAssetId() == null){
                actionSystemController().onSave("Tạo thông tin loại tài sản " + typeAsset.getName(), authorizationController.getAccountDto().getAccountId());
            }else {
                actionSystemController().onSave("Sửa thông tin loại tài sản " + typeAsset.getName(), authorizationController.getAccountDto().getAccountId());
            }
            typeAssetRepository.save(typeAsset);
            FacesUtil.addSuccessMessage("Lưu thành công");
            FacesUtil.closeDialog("dialogInsertUpdate");
            resetAll();
            FacesUtil.updateView("growl");
            FacesUtil.updateView("searchForm");
            onSearch();
        }

    }

    public void clearData() {
        if (typeAsset.getTypeAssetId() != null && typeAsset.getTypeAssetId() > 0) {
            BeanUtils.copyProperties(typeAssetCopy, typeAsset);
        } else
            resetDialog();
    }

    public void showUpdatePopup(TypeAsset obj) {
        typeAssetCopy = new TypeAsset();
        BeanUtils.copyProperties(obj, typeAsset);
        BeanUtils.copyProperties(obj, typeAssetCopy);
    }


    public void resetDialog() {
        typeAsset = new TypeAsset();
    }

    public void onDelete(TypeAsset deleteObj) {
        if (checkEditButton(deleteObj.getTypeAssetId())) {
            FacesUtil.addErrorMessage("Không được xóa vì loại tài sản " + deleteObj.getName() + " đang được sử dụng để tạo quy chế");
            FacesUtil.updateView("growl");
            FacesUtil.closeDialog("dialogInsertUpdate");
        }else {
            typeAssetRepository.deleteById(deleteObj.getTypeAssetId());
            actionSystemController().onSave("Xóa loại tài sản " + deleteObj.getName(), authorizationController.getAccountDto().getAccountId());
            FacesUtil.addSuccessMessage("Xóa thành công");
            onSearch();
        }
    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<TypeAsset>() {

            @Override
            public List<TypeAsset> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                typeAssetSearchDto.setPageIndex(first);
                typeAssetSearchDto.setPageSize(pageSize);
                typeAssetSearchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                typeAssetSearchDto.setSortOrder(sort);
                return typeAssetRepository.search(typeAssetSearchDto);
            }

            @Override
            public TypeAsset getRowData(String rowKey) {
                List<TypeAsset> TypeAssetList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (TypeAsset obj : TypeAssetList) {
                    if (obj.getCode().equals(value) || obj.getName().equals(value)) {
                        return obj;
                    }

                }
                return null;
            }
        };
        int count = typeAssetRepository.countSearch(typeAssetSearchDto).intValue();
        lazyDataModel.setRowCount(count);
        FacesUtil.updateView("searchForm");
    }

    public boolean checkEditButton(Long typeAssetId) {
        List<Long> listTypeAssetId = assetRepository.findTypeAssetId();
        if (listTypeAssetId.contains(typeAssetId)) {
            return true;
        }
        return false;
    }

    @Override
    protected EScope getMenuId() {
        return EScope.TYPE_ASSET;
    }

}


