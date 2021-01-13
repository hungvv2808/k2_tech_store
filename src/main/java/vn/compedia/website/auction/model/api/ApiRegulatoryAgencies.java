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
@Table(name = "api_regulatory_agencies")
public class ApiRegulatoryAgencies extends BaseModel {
    private static final long serialVersionUID = -8768955299138741076L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "regulatory_agencies_id")
    private Long regulatoryAgenciesId;

    @Column(name = "name")
    private String name;
    public ApiRegulatoryAgencies(Date createDate, Date updateDate, Long createBy, Long updateBy, String name) {
        super(createDate, updateDate, createBy, updateBy);
        this.name = name;
    }

}
