package vn.tech.website.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.tech.website.store.model.Product;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto extends Product {
    private String brandName;
    private String categoryName;
}
