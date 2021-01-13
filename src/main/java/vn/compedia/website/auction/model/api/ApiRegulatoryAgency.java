package vn.compedia.website.auction.model.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.model.BaseModel;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "api_regulatory_agency")
public class ApiRegulatoryAgency extends BaseModel {
    private static final long serialVersionUID = -346933539505466684L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "regulatory_agency_id")
    private Long regulatoryAgencyId;

    @Column(name = "name")
    private String name;

    public ApiRegulatoryAgency(Date createDate, Date updateDate, Long createBy, Long updateBy, String name) {
        super(createDate, updateDate, createBy, updateBy);
        this.name = name;
    }


}
