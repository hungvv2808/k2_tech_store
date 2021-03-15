package vn.tech.website.store.controller.frontend.product;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.controller.frontend.AuthorizationFEController;
import vn.tech.website.store.controller.frontend.BaseFEController;
import vn.tech.website.store.dto.BrandDto;
import vn.tech.website.store.dto.BrandSearchDto;
import vn.tech.website.store.dto.OrdersDetailDto;
import vn.tech.website.store.dto.ProductDto;
import vn.tech.website.store.model.OrdersDetail;
import vn.tech.website.store.model.Product;
import vn.tech.website.store.model.ProductImage;
import vn.tech.website.store.repository.BrandRepository;
import vn.tech.website.store.repository.CategoryRepository;
import vn.tech.website.store.repository.ProductImageRepository;
import vn.tech.website.store.repository.ProductRepository;
import vn.tech.website.store.util.DbConstant;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Named
@Scope(value = "session")
@Getter
@Setter
public class ProductFEController extends BaseFEController {
    @Inject
    private AuthorizationFEController authorizationFEController;
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

    private List<ProductDto> productDtoList;
    private String categoryId;
    private List<OrdersDetailDto> listAddToCart;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            //init();
            categoryId = request.getParameter("catid");
            resetAll();
        }
    }

    public void resetAll() {
        productDtoList = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        if (categoryId == null) {
            products = productRepository.getAllExpertType(DbConstant.PRODUCT_TYPE_CHILD);
        } else {
            products = productRepository.getByCategoryIdExpertType(Long.valueOf(categoryId), DbConstant.PRODUCT_TYPE_CHILD);
        }
        for (Product obj : products) {
            ProductDto dto = new ProductDto();
            BeanUtils.copyProperties(obj, dto);
            dto.setProductImages(new LinkedHashSet<>());
            dto.setProductImages(productImageRepository.getImagePathByProductId(dto.getProductId()));
            if (dto.getProductImages().size() != 0) {
                dto.setImageToShow(dto.getProductImages().iterator().next());
            }
            if (dto.getDiscount() != null) {
                dto.setPriceAfterDiscount(dto.getPrice() - dto.getPrice() * dto.getDiscount() / 100);
            } else {
                dto.setPriceAfterDiscount(dto.getPrice());
            }
            productDtoList.add(dto);
        }
    }


    public void addToCart(ProductDto resultDto) {
        HttpSession session = request.getSession(false);
        if (listAddToCart == null) {
            listAddToCart = new ArrayList<>();
            session.setAttribute("cartList", listAddToCart);
        }
        listAddToCart = (List<OrdersDetailDto>) session.getAttribute("cartList");

        OrdersDetailDto dto = new OrdersDetailDto();
        BeanUtils.copyProperties(resultDto, dto);
        dto.setQuantity(1L);
        dto.setAmount(resultDto.getPriceAfterDiscount());

        if (listAddToCart.size() == 0) {
            listAddToCart.add(dto);
        } else {
            boolean checkExits = true;
            for (OrdersDetailDto obj : listAddToCart) {
                if (dto.getProductId() == obj.getProductId()) {
                    obj.setQuantity(obj.getQuantity() + 1);
                    checkExits = false;
                    break;
                }
            }
            if (checkExits){
                listAddToCart.add(dto);
            }
        }
        session.setAttribute("cartList", listAddToCart);
        setSuccessForm("Thêm vào giỏ hàng thành công");
    }

    @Override
    protected String getMenuId() {
        return null;
    }
}
