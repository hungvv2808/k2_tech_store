package vn.tech.website.store.repository;

import vn.tech.website.store.dto.ProductDto;
import vn.tech.website.store.dto.ProductSearchDto;

import java.util.List;

public interface ProductRepositoryCustom {
    List<ProductDto> search(ProductSearchDto searchDto);

    Long countSearch(ProductSearchDto searchDto);
}
