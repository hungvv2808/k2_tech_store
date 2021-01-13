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
@Table(name = "commune_version")
public class CommuneVersion extends BaseModel {
    private static final long serialVersionUID = -457533941714660482L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commune_version_id")
    private Long communeVersionId;

    @Column(name = "commune_id")
    private Long communeId;


    @Column(name = "province_id")
    private Long provinceId;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "version_id")
    private Long versionId;

    public CommuneVersion(Date createDate, Date updateDate, Long createBy, Long updateBy, Long provinceId, Long districtId, String code, String name, Boolean status, Long versionId) {
        super(createDate, updateDate, createBy, updateBy);
        this.provinceId = provinceId;
        this.districtId = districtId;
        this.code = code;
        this.name = name;
        this.status = status;
        this.versionId = versionId;
    }

    public CommuneVersion(Date createDate, Date updateDate, Long createBy, Long updateBy, Long provinceId, Long districtId, String code, String name, Boolean status) {
        super(createDate, updateDate, createBy, updateBy);
        this.provinceId = provinceId;
        this.districtId = districtId;
        this.code = code;
        this.name = name;
        this.status = status;
    }
}

