package vn.tech.website.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "type")
    private Integer type;

    @Column(name = "name")
    private String productName;

    @Column(name = "code")
    private String code;

    @Column(name = "count_code")
    private Long countCode;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "status")
    private Integer status;
}
