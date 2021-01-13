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
@Table(name = "api_sex")
public class ApiSex extends BaseModel {
    private static final long serialVersionUID = 1423978848315226128L;

    @Id
    @Column(name = "sex_id")
    private Integer sexId;

    @Column(name = "name")
    private String name;

    public ApiSex(Date createDate, Date updateDate, Long createBy, Long updateBy, Integer sexId, String name) {
        super(createDate, updateDate, createBy, updateBy);
        this.sexId = sexId;
        this.name = name;
    }
}
