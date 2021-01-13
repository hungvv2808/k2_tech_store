package vn.compedia.website.auction.model.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.model.BaseModel;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "api_family_relationship")
public class ApiFamilyRelationship extends BaseModel {
    private static final long serialVersionUID = -2181838589563488601L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="family_relationship_id")
    private Long familyRegulationId;
    @Column(name = "name")
    private String name;

    public ApiFamilyRelationship(Date createDate, Date updateDate, Long createBy, Long updateBy, String name) {
        super(createDate, updateDate, createBy, updateBy);
        this.name = name;
    }
}
