package vn.tech.website.store.controller.frontend;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.dto.ProductDto;
import vn.tech.website.store.dto.ProductSearchDto;
import vn.tech.website.store.repository.ProductImageRepository;
import vn.tech.website.store.repository.ProductRepository;
import vn.tech.website.store.util.Constant;
import vn.tech.website.store.util.DbConstant;
import vn.tech.website.store.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = "session")
@Getter
@Setter
public class HomeFEController extends BaseFEController {
    @Inject
    private AuthorizationFEController authorizationFEController;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageRepository productImageRepository;

    private Date now;
    private List<ProductDto> maleList;
    private List<ProductDto> femaleList;
    private List<ProductDto> kidList;
    private ProductSearchDto searchDto;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        now = new Date();
        searchDto = new ProductSearchDto();
        maleList = new ArrayList<>();
        maleList = onSearchListProduct(Constant.CATE_MALE);
        femaleList = new ArrayList<>();
        femaleList = onSearchListProduct(Constant.CATE_FEMALE);
        kidList = new ArrayList<>();
        kidList = onSearchListProduct(Constant.CATE_KID);
    }

    private List<ProductDto> onSearchListProduct(Integer categoryId) {
        searchDto.setPageSize(DbConstant.LIMIT_SHOW_FE);
        searchDto.setExpertType(DbConstant.PRODUCT_TYPE_CHILD);
        searchDto.setCategoryId(Long.valueOf(categoryId));
        List<ProductDto> showList = productRepository.search(searchDto);
        for (ProductDto dto : showList){
            dto.setProductImages(new LinkedHashSet<>());
            dto.setProductImages(productImageRepository.getImagePathByProductId(dto.getProductId()));
            if (dto.getProductImages().size() != 0) {
                dto.setImageToShow(dto.getProductImages().iterator().next());
            }
        }
        return showList;
    }

    public void onSearch() {

    }

    public void redirectProduct(Integer cateId) {
        String request = FacesUtil.getContextPath();
        FacesUtil.redirect(request + "frontend/product/product.xhtml?catid=" + cateId);
    }

    @Override
    protected String getMenuId() {
        return null;
    }
}
