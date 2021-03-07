package vn.tech.website.store.controller.admin.product;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.controller.admin.BaseController;
import vn.tech.website.store.controller.admin.auth.AuthorizationController;
import vn.tech.website.store.dto.ProductOptionDto;
import vn.tech.website.store.dto.ProductOptionSearchDto;
import vn.tech.website.store.entity.EScope;
import vn.tech.website.store.model.ProductOption;
import vn.tech.website.store.repository.ProductOptionRepository;
import vn.tech.website.store.util.DbConstant;
import vn.tech.website.store.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = "session")
@Getter
@Setter
public class ProductOptionController extends BaseController {
    @Inject
    private AuthorizationController authorizationController;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    private LazyDataModel<ProductOptionDto> lazyDataModel;
    private ProductOptionDto productOptionDto;
    private ProductOptionSearchDto searchDto;
    private List<SelectItem> typeList;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            //init();
            resetAll();
        }
    }

    public void resetAll() {
        productOptionDto = new ProductOptionDto();
        searchDto = new ProductOptionSearchDto();
        typeList = new ArrayList<>();
        typeList.add(new SelectItem(DbConstant.TYPE_OPTION_SIZE, DbConstant.TYPE_OPTION_SIZE_STRING));
        typeList.add(new SelectItem(DbConstant.TYPE_OPTION_COLOR, DbConstant.TYPE_OPTION_COLOR_STRING));
        typeList.add(new SelectItem(DbConstant.TYPE_OPTION_RELEASE, DbConstant.TYPE_OPTION_RELEASE_STRING));
        onSearch();
    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<ProductOptionDto>() {
            @Override
            public List<ProductOptionDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                searchDto.setPageIndex(first);
                searchDto.setPageSize(pageSize);
                searchDto.setSortField(sortField);
                String sort;
                if (sortOrder.equals(sortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                searchDto.setSortOrder(sort);
                return productOptionRepository.search(searchDto);
            }

            @Override
            public ProductOptionDto getRowData(String rowKey) {
                List<ProductOptionDto> dtoList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (ProductOptionDto obj : dtoList) {
                    if (obj.getProductOptionId().equals(Long.valueOf(value))) {
                        return obj;
                    }
                }
                return null;
            }
        };

        int count = productOptionRepository.countSearch(searchDto).intValue();
        lazyDataModel.setRowCount(count);
    }

    public boolean validateData() {
        if (productOptionDto.getType() == null) {
            FacesUtil.addErrorMessage("Bạn vui lòng chọn loại thuộc tính");
            return false;
        }
        if (StringUtils.isBlank(productOptionDto.getName())) {
            FacesUtil.addErrorMessage("Bạn vui lòng nhập tên thuộc tính");
            return false;
        } else {
            productOptionDto.setName(removeSpaceOfString(productOptionDto.getName()));
        }
        if (StringUtils.isBlank(productOptionDto.getValue())) {
            FacesUtil.addErrorMessage("Bạn vui lòng nhập giá trị thuộc tính");
            return false;
        } else {
            productOptionDto.setValue(removeSpaceOfString(productOptionDto.getValue()));
        }
        List<ProductOption> productOptionList = new ArrayList<>();
        if(productOptionDto.getProductOptionId() == null){
            productOptionList = productOptionRepository.findAll();
        }else {
            productOptionList = productOptionRepository.findAllExpertId(productOptionDto.getProductOptionId());
        }
        for (ProductOption option : productOptionList){
            if (productOptionDto.getType() == option.getType() && productOptionDto.getName().equalsIgnoreCase(option.getName()) && productOptionDto.getValue().equalsIgnoreCase(option.getValue())){
                FacesUtil.addErrorMessage("Thuộc tính này đã tồn tại");
                return false;
            }
        }
        return true;
    }

    public void onSave() {
        if (!validateData()) {
            return;
        }
        ProductOption productOption = new ProductOption();
        BeanUtils.copyProperties(productOptionDto, productOption);
        productOption.setStatus(DbConstant.STATUS_OPTION_ACTIVE);
        productOption.setUpdateDate(new Date());
        productOption.setUpdateBy(authorizationController.getAccountDto() == null ? authorizationController.getAccountDto().getAccountId() : 1);
        productOptionRepository.save(productOption);
        FacesUtil.addSuccessMessage("Lưu thành công.");
        FacesUtil.closeDialog("dialogInsertUpdate");
        onSearch();
    }

    public void showUpdatePopup(ProductOptionDto resultDto) {
        BeanUtils.copyProperties(resultDto, productOptionDto);
    }

    public void onDelete(ProductOptionDto resultDto) {
        resultDto.setStatus(DbConstant.STATUS_OPTION_INACTIVE);
        ProductOption productOption = new ProductOption();
        BeanUtils.copyProperties(resultDto, productOption);
        productOptionRepository.save(productOption);
        FacesUtil.addSuccessMessage("Xóa thành công.");
        onSearch();
    }

    public void resetDialog() {
        productOptionDto = new ProductOptionDto();
    }

    public String removeSpaceOfString(String str) {
        return str.trim().replaceAll("[\\s|\\u00A0]+", " ");
    }

    @Override
    protected EScope getMenuId() {
        return null;
    }
}
