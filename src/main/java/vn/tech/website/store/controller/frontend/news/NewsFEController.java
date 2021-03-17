package vn.tech.website.store.controller.frontend.news;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.controller.frontend.AuthorizationFEController;
import vn.tech.website.store.controller.frontend.BaseFEController;
import vn.tech.website.store.dto.*;
import vn.tech.website.store.model.Product;
import vn.tech.website.store.repository.*;
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
public class NewsFEController extends BaseFEController {
    @Inject
    private AuthorizationFEController authorizationFEController;
    @Inject
    private HttpServletRequest request;

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductImageRepository productImageRepository;

    private List<NewsDto> newsDtoList;
    private String cateId;
    private List<OrdersDetailDto> listAddToCart;
    private NewsSearchDto searchDto;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            cateId = request.getParameter("catid");
            resetAll(null);
        }
    }

    public void resetAll(Long categoryId) {
        newsDtoList = new ArrayList<>();
        searchDto = new NewsSearchDto();
        onSearch(categoryId);
    }

    public void onSearch(Long categoryId) {
        List<NewsDto> showList = newsRepository.search(searchDto);
//        for (NewsDto dto : showList) {
//            dto.setProductImages(new LinkedHashSet<>());
//            dto.setProductImages(productImageRepository.getImagePathByProductId(dto.getProductId()));
//            if (dto.getProductImages().size() != 0) {
//                dto.setImageToShow(dto.getProductImages().iterator().next());
//            }
//        }
//
//        List<Product> products = new ArrayList<>();
//        if (categoryId == null) {
//            products = productRepository.getAllExpertType(DbConstant.PRODUCT_TYPE_CHILD);
//        } else {
//            products = productRepository.getByCategoryIdExpertType(Long.valueOf(cateId), DbConstant.PRODUCT_TYPE_CHILD);
//        }
//        for (Product obj : products) {
//            NewsDto dto = new NewsDto();
//            BeanUtils.copyProperties(obj, dto);
//            dto.setProductImages(new LinkedHashSet<>());
//            dto.setProductImages(productImageRepository.getImagePathByProductId(dto.getProductId()));
//            if (dto.getProductImages().size() != 0) {
//                dto.setImageToShow(dto.getProductImages().iterator().next());
//            }
//            if (dto.getDiscount() != null) {
//                dto.setPriceAfterDiscount(dto.getPrice() - dto.getPrice() * dto.getDiscount() / 100);
//            } else {
//                dto.setPriceAfterDiscount(dto.getPrice());
//            }
//            productDtoList.add(dto);
//        }
    }

    @Override
    protected String getMenuId() {
        return null;
    }
}
