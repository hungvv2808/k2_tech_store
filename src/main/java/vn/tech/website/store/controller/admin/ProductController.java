package vn.tech.website.store.controller.admin;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.controller.admin.auth.AuthorizationController;
import vn.tech.website.store.dto.ProductDto;
import vn.tech.website.store.dto.ProductSearchDto;
import vn.tech.website.store.model.Brand;
import vn.tech.website.store.model.Category;
import vn.tech.website.store.model.ProductOption;
import vn.tech.website.store.repository.BrandRepository;
import vn.tech.website.store.repository.CategoryRepository;
import vn.tech.website.store.repository.ProductOptionRepository;
import vn.tech.website.store.repository.ProductRepository;
import vn.tech.website.store.util.DbConstant;
import vn.tech.website.store.util.FacesUtil;

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
public class ProductController {
    @Inject
    private AuthorizationController authorizationController;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductOptionRepository productOptionRepository;

    private LazyDataModel<ProductDto> lazyDataModel;
    private ProductDto productDto;
    private ProductSearchDto searchDto;
    private List<SelectItem> categoryList;
    private List<SelectItem> brandList;
    private List<SelectItem> typeList;
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
        categoryList = new ArrayList<>();
        List<Category> categories = categoryRepository.findAll();
        for (Category obj : categories) {
            categoryList.add(new SelectItem(obj.getCategoryId(), obj.getCategoryName()));
        }
        brandList = new ArrayList<>();
        List<Brand> brands = brandRepository.findAll();
        for (Brand obj : brands) {
            brandList.add(new SelectItem(obj.getBrandId(), obj.getBrandName()));
        }
        typeList = new ArrayList<>();
        typeList.add(new SelectItem(DbConstant.TYPE_PRODUCT_PARENT, DbConstant.TYPE_PRODUCT_PARENT_STRING));
        typeList.add(new SelectItem(DbConstant.TYPE_PRODUCT_CHILD, DbConstant.TYPE_PRODUCT_CHILD_STRING));
        typeList.add(new SelectItem(DbConstant.TYPE_PRODUCT_NONE, DbConstant.TYPE_PRODUCT_NONE_STRING));
        listOptionSelect = new ArrayList<>();
        optionList = new ArrayList<>();
        List<ProductOption> options = productOptionRepository.findAll();
        for (ProductOption obj : options) {
            optionList.add(new SelectItem(obj.getProductOptionId(), obj.getOptionName() + "(" + obj.getOptionValue() + ")"));
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

    public void onSave() {

    }
}
