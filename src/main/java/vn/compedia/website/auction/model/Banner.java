package vn.compedia.website.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "banner")
public class Banner extends BaseModel {
    private static final long serialVersionUID = 4688240999721981841L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="banner_id")
    private Long bannerId;
    @Column(name="image_path")
    private String imagePath;
    @Column(name="banner_link")
    private String bannerLink;


    public Banner(Long bannerId, String imagePath, String bannerLink, Date createDate, Long createBy, Date updateDate, Long updateBy) {
        super();
    }
}
