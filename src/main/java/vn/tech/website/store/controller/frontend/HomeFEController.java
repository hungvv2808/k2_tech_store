package vn.tech.website.store.controller.frontend;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.controller.frontend.news.NewsFEController;
import vn.tech.website.store.controller.frontend.product.ProductFEController;
import vn.tech.website.store.dto.NewsDto;
import vn.tech.website.store.dto.NewsSearchDto;
import vn.tech.website.store.dto.ProductDto;
import vn.tech.website.store.dto.ProductSearchDto;
import vn.tech.website.store.repository.CategoryRepository;
import vn.tech.website.store.repository.NewsRepository;
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
    @Inject
    private NewsFEController newsFEController;
    @Inject
    private ProductFEController productFEController;

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private Date now;
    private List<NewsDto> newsDtoList;
    private List<ProductDto> mobileList;
    private List<ProductDto> laptopList;
    private List<ProductDto> watchList;
    private List<ProductDto> airList;
    private List<ProductDto> shirtList;
    private List<ProductDto> pantList;
    private List<ProductDto> sneakerList;
    private List<ProductDto> accessoriesList;

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

        productFEController.setCheckType(false);

        // list news
        NewsSearchDto newsSearchDto = new NewsSearchDto();
        newsSearchDto.setPageSize(DbConstant.LIMIT_SHOW_FE);
        newsDtoList = new ArrayList<>();
        newsDtoList = newsRepository.search(newsSearchDto);

        // list products
        mobileList = new ArrayList<>();
        mobileList = onSearchListProduct(Constant.CATE_PHONE);
        laptopList = new ArrayList<>();
        laptopList = onSearchListProduct(Constant.CATE_LAPTOP);
        watchList = new ArrayList<>();
        watchList = onSearchListProduct(Constant.CATE_WATCH);
        airList = new ArrayList<>();
        airList = onSearchListProduct(Constant.CATE_HEADPHONE);
        shirtList = new ArrayList<>();
        shirtList = onSearchListProductByCodeCate(Constant.CATE_AO);
        pantList = new ArrayList<>();
        pantList = onSearchListProductByCodeCate(Constant.CATE_QUAN);
        sneakerList = new ArrayList<>();
        sneakerList = onSearchListProductByCodeCate(Constant.CATE_GIAY);
        accessoriesList = new ArrayList<>();
        accessoriesList = onSearchListProductByCodeCate(Constant.CATE_PHU_KIEN);
    }

    private List<ProductDto> onSearchListProduct(Integer categoryId) {
        searchDto.setPageSize(DbConstant.LIMIT_SHOW_FE);
        searchDto.setType(DbConstant.PRODUCT_TYPE_PARENT);
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

    private List<ProductDto> onSearchListProductByCodeCate(String cateCode) {
        searchDto.setPageSize(DbConstant.LIMIT_SHOW_FE);
        searchDto.setType(DbConstant.PRODUCT_TYPE_PARENT);
        searchDto.setCategoryId(categoryRepository.getByCode(cateCode).getCategoryId());
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

    public void viewNewsDetail(NewsDto resultDto) {
        NewsDto newsDto = new NewsDto();
        BeanUtils.copyProperties(resultDto, newsDto);
        newsFEController.setNewsDto(newsDto);
        FacesUtil.redirect("/frontend/news/news.xhtml");
    }

    @Override
    protected String getMenuId() {
        return null;
    }
}
