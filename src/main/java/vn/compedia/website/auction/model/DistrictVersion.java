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
@Table(name = "district_version")
public class DistrictVersion extends BaseModel {
    private static final long serialVersionUID = 4518454309239587729L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "district_version_id")
    private Long districtVersionId;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "province_id")
    private Long provinceId;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "district_api_id")
    private Long districtApiId;

    @Column(name = "version_id")
    private Long versionId;

    // api
    public DistrictVersion(Date createDate, Date updateDate, Long createBy, Long updateBy, Long provinceId, String code, String name, Boolean status, Long districtApiId, Long versionId) {
        super(createDate, updateDate, createBy, updateBy);
        this.provinceId = provinceId;
        this.code = code;
        this.name = name;
        this.status = status;
        this.districtApiId = districtApiId;
        this.versionId = versionId;
    }

    public DistrictVersion(Date createDate, Date updateDate, Long createBy, Long updateBy, Long provinceId, String code, String name, Boolean status, Long districtApiId) {
        super(createDate, updateDate, createBy, updateBy);
        this.provinceId = provinceId;
        this.code = code;
        this.name = name;
        this.status = status;
        this.districtApiId = districtApiId;
    }
}

