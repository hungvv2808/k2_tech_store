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
import vn.tech.website.store.controller.admin.common.UploadMultipleImageController;
import vn.tech.website.store.dto.CategoryDto;
import vn.tech.website.store.dto.ProductDto;
import vn.tech.website.store.dto.ProductSearchDto;
import vn.tech.website.store.entity.EScope;
import vn.tech.website.store.model.*;
import vn.tech.website.store.repository.*;
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
public class ProductController extends BaseController {
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private UploadMultipleImageController uploadMultipleImageController;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductOptionRepository productOptionRepository;
    @Autowired
    private ProductLinkRepository productLinkRepository;
    @Autowired
    private ProductOptionDetailRepository productOptionDetailRepository;

    private LazyDataModel<ProductDto> lazyDataModel;
    private ProductDto productDto;
    private ProductSearchDto searchDto;
    private List<SelectItem> categoryList;
    private List<SelectItem> brandList;
    private List<SelectItem> typeList;
    private List<SelectItem> productParentList;
    private List<SelectItem> optionList;
    private List<Long> listOptionSelect;


    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            //init();
            resetAll();
        }
    }

    public void resetAll() {
        productDto = new ProductDto();
        searchDto = new ProductSearchDto();

        // include upload image controller
        uploadMultipleImageController.resetAll(null);

        //add combobox category
        categoryList = new ArrayList<>();
        List<Category> categories = categoryRepository.findAll();
        for (Category obj : categories) {
            categoryList.add(new SelectItem(obj.getCategoryId(), obj.getCategoryName()));
        }
        //add combobox brand
        brandList = new ArrayList<>();
        List<Brand> brands = brandRepository.findAll();
        for (Brand obj : brands) {
            brandList.add(new SelectItem(obj.getBrandId(), obj.getBrandName()));
        }
        //add combobox type
        typeList = new ArrayList<>();
        typeList.add(new SelectItem(DbConstant.TYPE_PRODUCT_PARENT, DbConstant.TYPE_PRODUCT_PARENT_STRING));
        typeList.add(new SelectItem(DbConstant.TYPE_PRODUCT_CHILD, DbConstant.TYPE_PRODUCT_CHILD_STRING));
        typeList.add(new SelectItem(DbConstant.TYPE_PRODUCT_NONE, DbConstant.TYPE_PRODUCT_NONE_STRING));
        listOptionSelect = new ArrayList<>();
        //add combobox productParent
        productParentList = new ArrayList<>();
        List<Product> parentList = productRepository.getAllByType(DbConstant.TYPE_PRODUCT_PARENT);
        for (Product obj : parentList) {
            productParentList.add(new SelectItem(obj.getProductId(), obj.getProductName()));
        }
        //add combobox option
        optionList = new ArrayList<>();
        List<ProductOption> options = productOptionRepository.findAll();
        for (ProductOption obj : options) {
            optionList.add(new SelectItem(obj.getProductOptionId(), obj.getName() + "(" + obj.getValue() + ")"));
        }
        //onSearch();
    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<ProductDto>() {
            @Override
            public List<ProductDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
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
                return productRepository.search(searchDto);
            }

            @Override
            public ProductDto getRowData(String rowKey) {
                List<ProductDto> dtoList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (ProductDto obj : dtoList) {
                    if (obj.getProductId().equals(Long.valueOf(value))) {
                        return obj;
                    }
                }
                return null;
            }
        };

        int count = productRepository.countSearch(searchDto).intValue();
        lazyDataModel.setRowCount(count);
    }

    public void resetDialog() {
        productDto = new ProductDto();
        uploadMultipleImageController.resetAll(null);
    }

    public boolean validateData() {
        if (productDto.getCategoryId() == null) {
            FacesUtil.addErrorMessage("Bạn vui lòng chọn loại sản phẩm");
            return false;
        }
        if (productDto.getBrandId() == null) {
            FacesUtil.addErrorMessage("Bạn vui lòng chọn thương hiệu");
            return false;
        }
        if (productDto.getType() == null) {
            FacesUtil.addErrorMessage("Bạn vui lòng chọn kiểu sản phẩm");
            return false;
        }
        if (StringUtils.isBlank(productDto.getProductName())) {
            FacesUtil.addErrorMessage("Bạn vui lòng nhập tên sản phẩm");
            return false;
        } else {
            productDto.setProductName(removeSpaceOfString(productDto.getProductName()));
        }
        if (productDto.getType() == DbConstant.TYPE_PRODUCT_PARENT) {
            List<Product> productList = productRepository.getAllByType(DbConstant.TYPE_PRODUCT_PARENT);
            for (Product product : productList) {
                if (productDto.getProductName().equalsIgnoreCase(product.getProductName())) {
                    FacesUtil.addErrorMessage("Sản phẩm đã tồn tại");
                    return false;
                }
            }
        } else {
            if (productDto.getType() == DbConstant.TYPE_PRODUCT_CHILD) {
                if (productDto.getProductParentId() == null) {
                    FacesUtil.addErrorMessage("Bạn vui lòng chọn sản phẩm cha");
                    return false;
                }
            }
            if (productDto.getQuantity() == null) {
                FacesUtil.addErrorMessage("Bạn vui lòng nhập số lượng");
                return false;
            }
            if (StringUtils.isBlank(productDto.getDescription())) {
                FacesUtil.addErrorMessage("Bạn vui lòng nhập mô tả");
                return false;
            }
            if (productDto.getPrice() == null) {
                FacesUtil.addErrorMessage("Bạn vui lòng nhập đơn giá");
                return false;
            }
            if (productDto.getDiscount() == null) {
                FacesUtil.addErrorMessage("Bạn vui lòng nhập giảm giá");
                return false;
            }
            if (listOptionSelect.size() == 0) {
                FacesUtil.addErrorMessage("Bạn vui lòng chọn thuộc tính cho sản phẩm");
                return false;
            }
        }
        return true;
    }

    public void onSave() {
        if (!validateData()) {
            return;
        }
        //save to product
        Product product = new ProductDto();
        BeanUtils.copyProperties(productDto, product);
        product.setStatus(DbConstant.STATUS_PRODUCT_ACTIVE);
        product.setUpdateDate(new Date());
        product.setUpdateBy(authorizationController.getAccountDto() == null ? authorizationController.getAccountDto().getAccountId() : 1);
        productRepository.save(product);
        if (product.getType() != DbConstant.TYPE_PRODUCT_PARENT) {
            //save to product_option_detail
            if (productDto.getProductId() != null) {
                List<ProductOptionDetail> optionDetailListDelete = productOptionDetailRepository.findAllByProductId(productDto.getProductId());
                if (optionDetailListDelete != null) {
                    for (ProductOptionDetail productOptionDetail : optionDetailListDelete) {
                        productOptionDetailRepository.delete(productOptionDetail);
                    }
                }
            }
            for (Long productOptionId : listOptionSelect) {
                ProductOptionDetail productOptionDetail = new ProductOptionDetail();
                productOptionDetail.setProductId(product.getProductId());
                productOptionDetail.setProductOptionId(productOptionId);
                productOptionDetailRepository.save(productOptionDetail);
            }
            //save to product_link
            if (product.getType() == DbConstant.TYPE_PRODUCT_CHILD) {
                ProductLink productLink = new ProductLink();
                productLink.setChildId(product.getProductId());
                productLink.setParentId(productDto.getProductParentId());
                productLinkRepository.save(productLink);
            }
        }
        FacesUtil.addSuccessMessage("Lưu thành công.");
        FacesUtil.closeDialog("dialogInsertUpdate");
        onSearch();
    }

    public String removeSpaceOfString(String str) {
        return str.trim().replaceAll("[\\s|\\u00A0]+", " ");
    }

    @Override
    protected EScope getMenuId() {
        return null;
    }
}
