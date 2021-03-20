package vn.tech.website.store.controller.frontend.product;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.controller.frontend.BaseFEController;
import vn.tech.website.store.dto.ProductDto;
import vn.tech.website.store.model.ProductImage;
import vn.tech.website.store.repository.ProductImageRepository;
import vn.tech.website.store.repository.ProductRepository;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = "session")
@Getter
@Setter
public class ProductDetailFEController extends BaseFEController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageRepository productImageRepository;

    private ProductDto dto;
    private ProductDto productDtoView;
    private List<ProductImage> productImageList;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        productDtoView = new ProductDto();
        productImageList = new ArrayList<>();
        if (dto != null) {
            BeanUtils.copyProperties(dto, productDtoView);
        }
    }

    @Override
    protected String getMenuId() {
        return null;
    }
}
