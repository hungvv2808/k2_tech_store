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
@Table(name= "api_religion")
public class ApiReligion extends BaseModel {
    private static final long serialVersionUID = -4967346783656429964L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="religion_id")
    private Long religionId;
    @Column(name = "name")
    private String name;

    public ApiReligion(Date createDate, Date updateDate, Long createBy, Long updateBy, String name) {
        super(createDate, updateDate, createBy, updateBy);
        this.name = name;
    }
}
