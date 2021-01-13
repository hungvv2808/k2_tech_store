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
public class Province extends BaseModel {
    private static final long serialVersionUID = -5406999938328491965L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "province_id")
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
    public Province(Date createDate, Date updateDate, Long createBy, Long updateBy, String code, String name, Boolean status, Long provinceApiId, Long versionId) {
        super(createDate, updateDate, createBy, updateBy);
        this.code = code;
        this.name = name;
        this.status = status;
        this.provinceApiId = provinceApiId;
        this.versionId = versionId;
    }

    public Province(Date createDate, Date updateDate, Long createBy, Long updateBy, String code, String name, Boolean status, Long provinceApiId) {
        super(createDate, updateDate, createBy, updateBy);
        this.code = code;
        this.name = name;
        this.status = status;
        this.provinceApiId = provinceApiId;
    }


}
