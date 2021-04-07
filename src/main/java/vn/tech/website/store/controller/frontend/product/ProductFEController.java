package vn.tech.website.store.controller.frontend.product;


import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.controller.frontend.BaseFEController;
import vn.tech.website.store.controller.frontend.common.PaginationController;
import vn.tech.website.store.dto.ProductDto;
import vn.tech.website.store.dto.ProductSearchDto;
import vn.tech.website.store.model.ProductLink;
import vn.tech.website.store.repository.ProductLinkRepository;
import vn.tech.website.store.repository.ProductRepository;
import vn.tech.website.store.util.DbConstant;
import vn.tech.website.store.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = "session")
@Getter
@Setter
public class ProductFEController extends BaseFEController {
    @Inject
    private ProductDetailFEController productDetailFEController;
    @Inject
    private HttpServletRequest request;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductLinkRepository productLinkRepository;

    private PaginationController<ProductDto> pagination;
    private Long categoryId;
    private Long brandId;
    private ProductSearchDto searchDto;
    private boolean checkType = false;

    public ProductFEController() {
        pagination = new PaginationController<>();
    }

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            String cateId = request.getParameter("catid");
            categoryId = cateId == null ? null : Long.parseLong(cateId);
            String braId = request.getParameter("braid");
            brandId = braId == null ? null : Long.parseLong(braId);
            resetAll();
        }
    }

    public void resetAll() {
        searchDto = new ProductSearchDto();
        pagination.setRequest(request);
        onSearch(categoryId, brandId);
        checkType = true;
    }

    public void onSearch(Long categoryId, Long brandId){
        searchDto.setCategoryId(categoryId);
        searchDto.setBrandId(brandId);
        searchDto.setType(DbConstant.PRODUCT_TYPE_CHILD);
        pagination.setLazyDataModel(new LazyDataModel<ProductDto>() {
            @Override
            public List<ProductDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                searchDto.setPageIndex(first);
                searchDto.setPageSize(pageSize);
                searchDto.setSortField(sortField);
                String sort = "";
                if (sortOrder == null || sortOrder.equals(SortOrder.DESCENDING)) {
                    sort = "DESC";
                } else {
                    sort = "ASC";
                }
                searchDto.setSortOrder(sort);
                return productRepository.search(searchDto);
            }

            @Override
            public int getRowCount() {
                return productRepository.countSearch(searchDto).intValue();
            }
        });
        pagination.loadData();
    }

    public void viewDetailProduct(ProductDto productDto) {
        ProductSearchDto searchDto = new ProductSearchDto();
        if (!checkType) {
            productDetailFEController.setParentId(productDto.getProductId());
            searchDto.setParentId(productDto.getProductId());
        } else {
            productDetailFEController.setProductId(productDto.getProductId());

            ProductLink productLink = productLinkRepository.findProductLinkByChildId(productDto.getProductId());
            Long parentId = productLink.getParentId();
            productDetailFEController.setParentId(parentId);
            searchDto.setParentId(parentId);
        }

        List<ProductDto> childProducts = productRepository.search(searchDto);
        Map<Long, ProductDto> childProductMap = new HashMap<>();
        for (ProductDto child : childProducts) {
            childProductMap.put(child.getProductId(), child);
        }
        productDetailFEController.setChildProductMap(new HashMap<>());
        productDetailFEController.setChildProductMap(childProductMap);

        FacesUtil.redirect("/frontend/product/detail.xhtml");
    }

    @Override
    protected String getMenuId() {
        return null;
    }
}
