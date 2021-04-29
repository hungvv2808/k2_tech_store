package vn.tech.website.store.controller.frontend;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.dto.*;
import vn.tech.website.store.repository.BrandRepository;
import vn.tech.website.store.repository.CategoryRepository;
import vn.tech.website.store.repository.NewsRepository;
import vn.tech.website.store.repository.ProductRepository;
import vn.tech.website.store.util.Constant;
import vn.tech.website.store.util.DbConstant;

import javax.faces.context.FacesContext;
import javax.inject.Named;

@Getter
@Setter
@Named
@Scope(value = "session")
public class SearchFEController extends BaseFEController {
    private ProductRepository productRepository;
    private BrandRepository brandRepository;
    private CategoryRepository categoryRepository;
    private NewsRepository newsRepository;

    private String keyword;
    private SearchDto searchDto;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            resetAll();
        }
    }

    public void resetAll() {
        keyword = null;
        searchDto = new SearchDto();
    }

    public void onSearch() {
        // search product
        ProductSearchDto productSearchDto = new ProductSearchDto();
        productSearchDto.setExpertType(DbConstant.PRODUCT_TYPE_CHILD);
        productSearchDto.setPageSize(Constant.PAGE_SIZE_MAX);
        searchDto.setProductDtoList(productRepository.search(productSearchDto));

        BrandSearchDto brandSearchDto = new BrandSearchDto();
        brandSearchDto.setPageSize(Constant.PAGE_SIZE_MAX);
        searchDto.setBrandDtoList(brandRepository.search(brandSearchDto));

        CategorySearchDto categorySearchDto = new CategorySearchDto();
        categorySearchDto.setPageSize(Constant.PAGE_SIZE_MAX);
        searchDto.setCategoryDtoList(categoryRepository.search(categorySearchDto));

        NewsSearchDto newsSearchDto = new NewsSearchDto();
        newsSearchDto.setPageSize(Constant.PAGE_SIZE_MAX);
        searchDto.setNewsDtoList(newsRepository.search(newsSearchDto));
    }

    @Override
    protected String getMenuId() {
        return null;
    }
}
