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
@Table(name= "api_career_group")
public class ApiCareerGroup extends BaseModel {
    private static final long serialVersionUID = 7365939487871746854L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="career_group_id")
    private Long careerGroupId;
    @Column(name = "name")
    private String name;

    public ApiCareerGroup(Date createDate, Date updateDate, Long createBy, Long updateBy, String name) {
        super(createDate, updateDate, createBy, updateBy);
        this.name = name;
    }
}
