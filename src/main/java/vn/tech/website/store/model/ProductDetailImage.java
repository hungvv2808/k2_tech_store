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
@Table(name = "product_detail_image")
public class ProductDetailImage extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_detail_image_id")
    private Long productDetailImageId;

    @Column(name = "product_detail_id")
    private Long productDetailId;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "image_path")
    private String imagePath;
}
