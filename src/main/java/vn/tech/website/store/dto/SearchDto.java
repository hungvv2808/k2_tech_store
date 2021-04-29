package vn.tech.website.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchDto {
    private List<ProductDto> productDtoList;
    private List<BrandDto> brandDtoList;
    private List<CategoryDto> categoryDtoList;
    private List<NewsDto> newsDtoList;
}
