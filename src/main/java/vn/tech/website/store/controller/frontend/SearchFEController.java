package vn.tech.website.store.controller.frontend;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.dto.*;
import vn.tech.website.store.model.Product;
import vn.tech.website.store.repository.BrandRepository;
import vn.tech.website.store.repository.CategoryRepository;
import vn.tech.website.store.repository.NewsRepository;
import vn.tech.website.store.repository.ProductRepository;
import vn.tech.website.store.util.Constant;
import vn.tech.website.store.util.DbConstant;
import vn.tech.website.store.util.StringUtil;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Named
@Scope(value = "session")
public class SearchFEController extends BaseFEController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private NewsRepository newsRepository;

    private String keyword;
    private boolean checkResult;
    private List<SearchDto> searchDtoList;
    private String searchDtoJson;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            resetAll();
        }
    }

    public void resetAll() {
        keyword = null;
        checkResult = false;
        searchDtoList = new ArrayList<>();
    }

    public void onSearch() {
        // search product
        ProductSearchDto productSearchDto = new ProductSearchDto();
        productSearchDto.setType(DbConstant.PRODUCT_TYPE_CHILD);
        productSearchDto.setPageSize(Constant.PAGE_SIZE_MAX);
        productSearchDto.setKeyword(keyword);
        List<ProductDto> productDtos = productRepository.search(productSearchDto);

        BrandSearchDto brandSearchDto = new BrandSearchDto();
        brandSearchDto.setKeyword(keyword);
        brandSearchDto.setPageSize(Constant.PAGE_SIZE_MAX);
        List<BrandDto> brandDtos = brandRepository.search(brandSearchDto);

        CategorySearchDto categorySearchDto = new CategorySearchDto();
        categorySearchDto.setKeyword(keyword);
        categorySearchDto.setType(DbConstant.CATEGORY_TYPE_PRODUCT);
        categorySearchDto.setPageSize(Constant.PAGE_SIZE_MAX);
        List<CategoryDto> categoryDtos = categoryRepository.search(categorySearchDto);

        NewsSearchDto newsSearchDto = new NewsSearchDto();
        newsSearchDto.setKeyword(keyword);
        newsSearchDto.setPageSize(Constant.PAGE_SIZE_MAX);
        List<NewsDto> newsDtos = newsRepository.search(newsSearchDto);

        for (ProductDto p : productDtos) {
            SearchDto s = new SearchDto();
            s.setObjectId(p.getProductId());
            s.setName(p.getProductName());
            s.setType("product");
            searchDtoList.add(s);
        }

        for (BrandDto b : brandDtos) {
            SearchDto s = new SearchDto();
            s.setObjectId(b.getBrandId());
            s.setName(b.getBrandName());
            s.setType("brand");
            searchDtoList.add(s);
        }

        for (CategoryDto c : categoryDtos) {
            SearchDto s = new SearchDto();
            s.setObjectId(c.getCategoryId());
            s.setName(c.getCategoryName());
            s.setType("category");
            searchDtoList.add(s);
        }

        for (NewsDto n : newsDtos) {
            SearchDto s = new SearchDto();
            s.setObjectId(n.getNewsId());
            s.setName(n.getShortContent());
            s.setType("news");
            searchDtoList.add(s);
        }

        searchDtoJson = StringUtil.toJson(searchDtoList);

        if (!searchDtoList.isEmpty()) {
            checkResult = true;
        }
    }

    public void clearList() {
        searchDtoList = new ArrayList<>();
    }

    @Override
    protected String getMenuId() {
        return null;
    }
}
