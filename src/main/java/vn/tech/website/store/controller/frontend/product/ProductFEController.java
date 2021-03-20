package vn.tech.website.store.controller.frontend.product;


import lombok.Getter;
import lombok.Setter;
import org.docx4j.wml.P;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.controller.frontend.AuthorizationFEController;
import vn.tech.website.store.controller.frontend.BaseFEController;
import vn.tech.website.store.controller.frontend.common.PaginationController;
import vn.tech.website.store.dto.OrdersDetailDto;
import vn.tech.website.store.dto.ProductDto;
import vn.tech.website.store.dto.ProductSearchDto;
import vn.tech.website.store.repository.BrandRepository;
import vn.tech.website.store.repository.CategoryRepository;
import vn.tech.website.store.repository.ProductImageRepository;
import vn.tech.website.store.repository.ProductRepository;
import vn.tech.website.store.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductImageRepository productImageRepository;

    private PaginationController<ProductDto> pagination;
    private List<ProductDto> productDtoList;
    private Long categoryId;
    private Long brandId;
    private Map<String, OrdersDetailDto> mapAddToCart;
    private ProductSearchDto searchDto;

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
        productDtoList = new ArrayList<>();
        searchDto = new ProductSearchDto();
        pagination.setRequest(request);
        onSearch(categoryId, brandId);
    }

    public void onSearch(Long categoryId, Long brandId){
        searchDto.setCategoryId(categoryId);
        searchDto.setBrandId(brandId);
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

    public void addToCart(ProductDto resultDto) {
        HttpSession session = request.getSession(false);
        if (session.getAttribute("cartList") == null) {
            mapAddToCart = new LinkedHashMap<>();
            session.setAttribute("cartList", mapAddToCart);
        }
        mapAddToCart = (Map<String, OrdersDetailDto>) session.getAttribute("cartList");

        OrdersDetailDto dto = new OrdersDetailDto();
        BeanUtils.copyProperties(resultDto, dto);
        dto.setProductId(resultDto.getProductId());
        dto.setQuantity(1L);
        dto.setAmount(resultDto.getPriceAfterDiscount() == null ? resultDto.getPrice() : resultDto.getPriceAfterDiscount());
        dto.setCodeProduct(resultDto.getCode());
        dto.setProductDto(resultDto);

        if (mapAddToCart.size() == 0) {
            mapAddToCart.put(resultDto.getCode(), dto);
        } else {
            boolean checkExits = true;
            for (OrdersDetailDto obj : mapAddToCart.values()) {
                if (dto.getProductId().equals(obj.getProductId())) {
                    Long qty = obj.getQuantity() + 1;
                    if (qty > resultDto.getQuantity()) {
                        setErrorForm("Số lượng trong kho không đủ để mua thêm, vui lòng quay lại sau.");
                        return;
                    }
                    obj.setQuantity(qty);
                    checkExits = false;
                    break;
                }
            }
            if (checkExits) {
                mapAddToCart.put(resultDto.getCode(), dto);
            }
        }
        session.setAttribute("cartList", mapAddToCart);
        setSuccessForm("Thêm sản phẩm \"" + resultDto.getProductName() + "\" vào giỏ hàng thành công");
    }

    public void viewDetailProduct(ProductDto productDto) {
        productDetailFEController.setDto(new ProductDto());
        productDetailFEController.setDto(productDto);
        FacesUtil.redirect("/frontend/product/detail.xhtml");
    }

    @Override
    protected String getMenuId() {
        return null;
    }
}
