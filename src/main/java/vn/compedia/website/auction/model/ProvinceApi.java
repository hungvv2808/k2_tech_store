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
@Table(name = "province")
public class ProvinceApi extends BaseModel {
    private static final long serialVersionUID = 5688998157820720628L;

    @Id
    @Column(name="province_id", nullable=false)
    private Long provinceId;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "province_api_id")
    private Long provinceApiId;

    @Column(name = "version_id")
    private Long versionId;


    // api use
    public ProvinceApi(Date createDate, Date updateDate, Long createBy, Long updateBy, Long provinceId, String code, String name, Boolean status, Long provinceApiId, Long versionId) {
        super(createDate, updateDate, createBy, updateBy);
        this.provinceId = provinceId;
        this.code = code;
        this.name = name;
        this.status = status;
        this.provinceApiId = provinceApiId;
        this.versionId = versionId;
    }

    public ProvinceApi(Date createDate, Date updateDate, Long createBy, Long updateBy, String code, String name, Boolean status, Long provinceApiId) {
        super(createDate, updateDate, createBy, updateBy);
        this.code = code;
        this.name = name;
        this.status = status;
        this.provinceApiId = provinceApiId;
    }

    public ProvinceApi(Date createDate, Date updateDate, Long createBy, Long updateBy, Long provinceId, Long versionId) {
        super(createDate, updateDate, createBy, updateBy);
        this.provinceId = provinceId;
        this.versionId = versionId;
    }

    public ProvinceApi(Long provinceId, Long versionId) {
        this.provinceId = provinceId;
        this.versionId = versionId;
    }

    public ProvinceApi(Date createDate, Long provinceId, Long versionId) {
        super(createDate);
        this.provinceId = provinceId;
        this.versionId = versionId;
    }
}
