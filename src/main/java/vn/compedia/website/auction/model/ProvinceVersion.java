package vn.compedia.website.auction.model;


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
@Table(name = "province_version")
public class ProvinceVersion extends BaseModel {
    private static final long serialVersionUID = 3158635241874235497L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "province_version_id")
    private Long provinceVersionId;

    @Column(name = "province_id")
    private Long provinceId;

    @Column(name = "version_id")
    private Long versionId;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "province_api_id")
    private Long provinceApiId;

}
